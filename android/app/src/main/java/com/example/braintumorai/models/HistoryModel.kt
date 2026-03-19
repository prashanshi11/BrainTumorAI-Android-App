package com.example.braintumorai.models

data class HistoryModel(
    val id: String = "",
    val imageUrl: String = "",
    val result: String = "",
    val confidence: String = "",
    val timestamp: Long = 0L
)