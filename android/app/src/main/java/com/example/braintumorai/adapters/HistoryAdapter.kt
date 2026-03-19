package com.example.braintumorai.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.braintumorai.R
import com.example.braintumorai.models.HistoryModel
import com.google.android.material.imageview.ShapeableImageView
import java.text.SimpleDateFormat
import java.util.*

class HistoryAdapter(private val historyList: List<HistoryModel>) :
    RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val historyImage: ShapeableImageView = view.findViewById(R.id.historyImage)
        val historyResult: TextView = view.findViewById(R.id.historyResult)
        val historyConfidence: TextView = view.findViewById(R.id.historyConfidence)
        val historyDate: TextView = view.findViewById(R.id.historyDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val history = historyList[position]

        holder.historyResult.text = history.result
        holder.historyConfidence.text = "Confidence: ${history.confidence}%"
        
        // Format timestamp to readable date
        val sdf = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
        holder.historyDate.text = sdf.format(Date(history.timestamp))

        // Load image using Glide
        Glide.with(holder.itemView.context)
            .load(history.imageUrl)
            .placeholder(R.drawable.card_bg)
            .into(holder.historyImage)
    }

    override fun getItemCount(): Int = historyList.size
}