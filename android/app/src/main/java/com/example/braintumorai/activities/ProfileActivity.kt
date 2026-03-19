package com.example.braintumorai.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.braintumorai.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        setupProfileInfo()
        setupButtons()
        fetchLastScanResult()
    }

    private fun setupProfileInfo() {
        val user = auth.currentUser
        val txtName = findViewById<TextView>(R.id.txtProfileName)
        val txtEmail = findViewById<TextView>(R.id.txtProfileEmail)

        if (user != null) {
            txtName.text = user.displayName ?: user.email?.split("@")?.get(0) ?: "User"
            txtEmail.text = user.email
        }
    }

    private fun setupButtons() {
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