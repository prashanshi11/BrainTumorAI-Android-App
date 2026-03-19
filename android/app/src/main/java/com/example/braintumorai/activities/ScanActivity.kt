package com.example.braintumorai.activities


import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.braintumorai.R
import com.example.braintumorai.api.RetrofitClient
import com.example.braintumorai.utils.ImageUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch

class ScanActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var txtResult: TextView
    private var imageUri: Uri? = null

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.slide_in, R.anim.fade_in)
        setContentView(R.layout.activity_scan)

        imageView = findViewById(R.id.imageView)
        txtResult = findViewById(R.id.txtResult)

        val btnSelect = findViewById<Button>(R.id.btnSelect)
        val btnUpload = findViewById<Button>(R.id.btnUpload)

        btnSelect.setOnClickListener {
            pickImage()
        }

        btnUpload.setOnClickListener {
            imageUri?.let {
                uploadImage(it)
            } ?: run {
                txtResult.text = "Please select an image first"
            }
        }
    }

    // -----------------------------
    // Pick Image
    // -----------------------------
    private fun pickImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, 1)
    }

    override fun onActivityResult(reqCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(reqCode, resultCode, data)

        if (reqCode == 1 && resultCode == Activity.RESULT_OK) {
            imageUri = data?.data
            imageView.setImageURI(imageUri)
        }
    }

    // -----------------------------
    // Upload to Firebase
    // -----------------------------
    private fun uploadImage(uri: Uri) {

        txtResult.text = "Uploading MRI..."

        val storageRef = FirebaseStorage.getInstance().reference
        val imageRef = storageRef.child("mri_images/${System.currentTimeMillis()}.jpg")

        imageRef.putFile(uri)
            .addOnSuccessListener {

                imageRef.downloadUrl.addOnSuccessListener { downloadUri ->

                    val imageUrl = downloadUri.toString()

                    txtResult.text = "Running AI model..."

                    sendToBackend(uri, imageUrl)
                }
            }
            .addOnFailureListener {
                txtResult.text = "Upload failed"
            }
    }

    // -----------------------------
    // Send to Backend
    // -----------------------------
    private fun sendToBackend(uri: Uri, imageUrl: String) {

        lifecycleScope.launch {

            try {

                val part = ImageUtils.uriToMultipart(this@ScanActivity, uri)
                val response = RetrofitClient.api.uploadImage(part)

                if (response.isSuccessful) {

                    val result = response.body()!!

                    // 🔥 Move to Result Screen
                    val intent = Intent(this@ScanActivity, ResultActivity::class.java)
                    intent.putExtra("prediction", result.prediction)
                    intent.putExtra("confidence", result.confidence)
                    intent.putExtra("heatmap", result.heatmap_url)

                    startActivity(intent)

                    // Save to Firestore
                    savePredictionToFirestore(
                        imageUrl,
                        result.prediction,
                        result.confidence,
                        result.heatmap_url
                    )

                } else {
                    txtResult.text = "Server Error"
                }

            } catch (e: Exception) {
                txtResult.text = "Error: ${e.message}"
            }
        }
    }

    // -----------------------------
    // Save to Firestore
    // -----------------------------
    private fun savePredictionToFirestore(
        imageUrl: String,
        prediction: String,
        confidence: Float,
        heatmapUrl: String
    ) {
        val user = auth.currentUser ?: return

        val data = hashMapOf(
            "user_id" to user.uid,
            "image_url" to imageUrl,
            "prediction" to prediction,
            "confidence" to confidence,
            "heatmap_url" to heatmapUrl,
            "timestamp" to System.currentTimeMillis()
        )

        db.collection("predictions").add(data)
    }
}