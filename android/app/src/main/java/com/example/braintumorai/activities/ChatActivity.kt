package com.example.braintumorai.activities

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.braintumorai.R
import com.example.braintumorai.adapters.ChatAdapter
import com.example.braintumorai.models.ChatMessage
import kotlinx.coroutines.*

class ChatActivity : AppCompatActivity() {

    private lateinit var chatRecycler: RecyclerView
    private lateinit var inputMessage: EditText
    private lateinit var btnSend: ImageButton
    private lateinit var chatAdapter: ChatAdapter
    private val chatList = mutableListOf<ChatMessage>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        chatRecycler = findViewById(R.id.chatRecycler)
        inputMessage = findViewById(R.id.inputMessage)
        btnSend = findViewById(R.id.btnSend)

        chatAdapter = ChatAdapter(chatList)
        chatRecycler.layoutManager = LinearLayoutManager(this)
        chatRecycler.adapter = chatAdapter

        btnSend.setOnClickListener {
            val message = inputMessage.text.toString().trim()
            if (message.isNotEmpty()) {
                addUserMessage(message)
                getAiResponse(message)
                inputMessage.text.clear()
            }
        }
        
        // Initial AI message
        addAiMessage("Hello! I'm your Brain Health AI. How can I assist you today?")
    }

    private fun addUserMessage(message: String) {
        chatList.add(ChatMessage(message, true))
        chatAdapter.notifyItemInserted(chatList.size - 1)
        chatRecycler.scrollToPosition(chatList.size - 1)
    }

    private fun addAiMessage(message: String) {
        chatList.add(ChatMessage(message, false))
        chatAdapter.notifyItemInserted(chatList.size - 1)
        chatRecycler.scrollToPosition(chatList.size - 1)
    }

    private fun getAiResponse(userMessage: String) {
        // Simulate AI response with a delay
        CoroutineScope(Dispatchers.Main).launch {
            // Typing indicator simulation
            delay(1000)
            
            val response = when {
                userMessage.contains("hello", ignoreCase = true) -> "Hi there! How can I help you with your brain health concerns?"
                userMessage.contains("tumor", ignoreCase = true) -> "Brain tumors are abnormal growths of cells in the brain. If you have an MRI, you can use our Scan feature for an initial analysis."
                userMessage.contains("symptoms", ignoreCase = true) -> "Common symptoms include headaches, seizures, vision changes, and balance issues. Always consult a doctor for a proper diagnosis."
                userMessage.contains("mri", ignoreCase = true) -> "You can upload your MRI scan in the 'Scan' section of this app for analysis."
                userMessage.contains("thank", ignoreCase = true) -> "You're welcome! Feel free to ask more questions."
                else -> "I understand. I'm trained to help with brain health and MRI-related queries. Could you please provide more details?"
            }
            
            addAiMessage(response)
        }
    }
}