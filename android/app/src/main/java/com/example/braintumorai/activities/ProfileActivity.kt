package com.example.braintumorai.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.braintumorai.R
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage

class ProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var profileImage: ShapeableImageView
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        profileImage = findViewById(R.id.profileImage)

        setupProfileInfo()
        setupButtons()
        fetchLastScanResult()
    }

    private fun setupProfileInfo() {
        val user = auth.currentUser
        val txtName = findViewById<TextView>(R.id.txtProfileName)
        val txtEmail = findViewById<TextView>(R.id.txtProfileEmail)

        if (user != null) {
            txtName.text = user.displayName ?: "User"
            txtEmail.text = user.email
            
            // Load existing profile image if available
            user.photoUrl?.let {
                Glide.with(this).load(it).into(profileImage)
            }
        }
    }

    private fun setupButtons() {
        profileImage.setOnClickListener {
            pickImage()
        }

        findViewById<LinearLayout>(R.id.btnChatHistory).setOnClickListener {
            startActivity(Intent(this, ChatActivity::class.java))
        }

        findViewById<LinearLayout>(R.id.btnDownloadReport).setOnClickListener {
            // Implementation for PDF Download
        }

        findViewById<LinearLayout>(R.id.btnViewTips).setOnClickListener {
            startActivity(Intent(this, TipsActivity::class.java))
        }

        findViewById<LinearLayout>(R.id.btnSettings).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        findViewById<Button>(R.id.btnLogout).setOnClickListener {
            auth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun pickImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, 101)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101 && resultCode == Activity.RESULT_OK) {
            imageUri = data?.data
            imageUri?.let {
                uploadProfileImage(it)
            }
        }
    }

    private fun uploadProfileImage(uri: Uri) {
        val user = auth.currentUser ?: return
        val ref = storage.reference.child("profile_pics/${user.uid}.jpg")

        ref.putFile(uri).addOnSuccessListener {
            ref.downloadUrl.addOnSuccessListener { downloadUri ->
                updateUserProfile(downloadUri)
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Upload failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUserProfile(uri: Uri) {
        val user = auth.currentUser
        val profileUpdates = userProfileChangeRequest {
            photoUri = uri
        }

        user?.updateProfile(profileUpdates)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Glide.with(this).load(uri).into(profileImage)
                Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchLastScanResult() {
        val user = auth.currentUser ?: return
        val txtLastResult = findViewById<TextView>(R.id.txtLastResult)

        db.collection("predictions")
            .whereEqualTo("user_id", user.uid)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val prediction = documents.documents[0].getString("prediction")
                    txtLastResult.text = prediction ?: "Normal"
                }
            }
    }
}