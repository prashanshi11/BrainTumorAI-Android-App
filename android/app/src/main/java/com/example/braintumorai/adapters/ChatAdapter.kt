package com.example.braintumorai.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.braintumorai.R
import com.example.braintumorai.models.ChatMessage

class ChatAdapter(private val chatList: List<ChatMessage>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TYPE_USER = 1
    private val TYPE_AI = 2

    override fun getItemViewType(position: Int): Int {
        return if (chatList[position].isUser) TYPE_USER else TYPE_AI
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_USER) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_chat_user, parent, false)
            UserViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_chat_ai, parent, false)
            AiViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val chat = chatList[position]
        if (holder is UserViewHolder) {
            holder.txtUserMessage.text = chat.message
        } else if (holder is AiViewHolder) {
            holder.txtAiMessage.text = chat.message
        }
    }

    override fun getItemCount(): Int = chatList.size

    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtUserMessage: TextView = view.findViewById(R.id.txtUserMessage)
    }

    class AiViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtAiMessage: TextView = view.findViewById(R.id.txtAiMessage)
    }
}