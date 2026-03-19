package com.example.braintumorai.activities

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.braintumorai.R

class ResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val prediction = intent.getStringExtra("prediction")
        val confidence = intent.getFloatExtra("confidence", 0f)
        val heatmap = intent.getStringExtra("heatmap")

        findViewById<TextView>(R.id.txtResult).text =
            "$prediction\nConfidence: $confidence"

        Glide.with(this)
            .load(heatmap)
            .into(findViewById(R.id.imageView))
    }
}
