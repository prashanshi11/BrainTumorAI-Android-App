package com.example.braintumorai.models

data class PredictionResponse(
    val prediction: String,
    val confidence: Float,
    val heatmap_url: String
)