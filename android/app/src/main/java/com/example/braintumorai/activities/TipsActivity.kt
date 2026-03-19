package com.example.braintumorai.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.braintumorai.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class TipsActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var txtStatus: TextView
    private lateinit var tipsContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tips)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        txtStatus = findViewById(R.id.txtStatus)
        tipsContainer = findViewById(R.id.tipsContainer)

        fetchLatestScanResult()
    }

    private fun fetchLatestScanResult() {
        val user = auth.currentUser ?: return

        db.collection("predictions")
            .whereEqualTo("user_id", user.uid) // Ensure you save user_id in ScanActivity
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    txtStatus.text = "No recent scans found. Stay healthy!"
                    showGeneralTips()
                } else {
                    val prediction = documents.documents[0].getString("prediction") ?: "Normal"
                    txtStatus.text = "Based on your last scan: $prediction"
                    generateAiTips(prediction)
                }
            }
            .addOnFailureListener {
                txtStatus.text = "Error fetching status. Showing general tips."
                showGeneralTips()
            }
    }

    private fun generateAiTips(prediction: String) {
        tipsContainer.removeAllViews()

        val tips = when {
            prediction.contains("Tumor", ignoreCase = true) -> listOf(
                "Consult an oncologist or neurosurgeon immediately.",
                "Keep a detailed log of any neurological symptoms.",
                "Prioritize rest and avoid high-stress activities.",
                "Maintain a balanced, anti-inflammatory diet.",
                "Discuss potential treatment plans with your medical team."
            )
            prediction.contains("Normal", ignoreCase = true) -> listOf(
                "Continue maintaining a healthy lifestyle!",
                "Incorporate brain-training exercises like puzzles.",
                "Ensure consistent sleep patterns for cognitive health.",
                "Keep up with regular physical activity.",
                "Schedule periodic checkups to stay proactive."
            )
            else -> listOf(
                "Stay hydrated and maintain a balanced diet.",
                "Get regular exercise and adequate sleep.",
                "Consult a professional if you feel unusual symptoms."
            )
        }

        for (tip in tips) {
            addTipView(tip)
        }
    }

    private fun showGeneralTips() {
        generateAiTips("General")
    }

    private fun addTipView(tip: String) {
        val view = LayoutInflater.from(this).inflate(R.layout.item_tip, tipsContainer, false)
        val txtTip = view.findViewById<TextView>(R.id.txtTip)
        txtTip.text = "• $tip"
        tipsContainer.addView(view)
    }
}