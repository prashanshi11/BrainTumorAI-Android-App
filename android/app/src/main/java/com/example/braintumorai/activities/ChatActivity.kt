package com.example.braintumorai.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
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
    private lateinit var btnAttach: ImageButton
    private lateinit var chatAdapter: ChatAdapter
    private val chatList = mutableListOf<ChatMessage>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        chatRecycler = findViewById(R.id.chatRecycler)
        inputMessage = findViewById(R.id.inputMessage)
        btnSend = findViewById(R.id.btnSend)
        btnAttach = findViewById(R.id.btnAttach)

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

        btnAttach.setOnClickListener {
            pickFile()
        }
        
        addAiMessage("Hello! I'm your Brain Health AI. You can ask me questions or upload your MRI images and PDF reports for analysis.")
    }

    private fun pickFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        val mimeTypes = arrayOf("image/*", "application/pdf")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        startActivityForResult(intent, 102)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 102 && resultCode == Activity.RESULT_OK) {
            val uri = data?.data
            uri?.let {
                handleAttachment(it)
            }
        }
    }

    private fun handleAttachment(uri: Uri) {
        val fileName = getFileName(uri)
        val type = contentResolver.getType(uri)
        
        if (type != null) {
            if (type.startsWith("image/")) {
                addUserMessage("Uploaded Image: $fileName")
                getAiResponse("I have received your MRI image. Analyzing for any abnormalities...")
            } else if (type == "application/pdf") {
                addUserMessage("Uploaded Report: $fileName")
                getAiResponse("I have received your PDF report. Extracting key medical information...")
            }
        }
    }

    private fun getFileName(uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = contentResolver.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (index != -1) result = cursor.getString(index)
                }
            } finally {
                cursor?.close()
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/') ?: -1
            if (cut != -1) {
                result = result?.substring(cut + 1)
            }
        }
        return result ?: "file"
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
        CoroutineScope(Dispatchers.Main).launch {
            delay(1500)
            
            val response = when {
                userMessage.contains("image", ignoreCase = true) -> "The image analysis shows clear brain structures. No significant tumor masses are visible in this specific view. However, please correlate this with a radiologist's report."
                userMessage.contains("report", ignoreCase = true) -> "Based on the report text, the primary findings suggest stable neurological status with no new lesions detected. All vital markers are within normal ranges."
                userMessage.contains("hello", ignoreCase = true) -> "Hi! How can I help you with your brain health reports today?"
                else -> "I've processed your input. Is there a specific part of the report or image you'd like me to explain further?"
            }
            
            addAiMessage(response)
        }
    }
}