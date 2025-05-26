package com.dianca.budgettrackerapp

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dianca.budgettrackerapp.databinding.ActivityChatBinding

class ChatActivity : BaseActivity() {

    private lateinit var binding: ActivityChatBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var editText: EditText
    private lateinit var sendButton: ImageButton
    private lateinit var adapter: ChatAdapter
    private lateinit var chatbot: SimpleChatbot

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup bottom navigation
        setupBottomNav()

        // Initialize views
        recyclerView = binding.chatRecyclerView
        editText = binding.messageEditText
        sendButton = binding.sendButton

        adapter = ChatAdapter()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        chatbot = SimpleChatbot()

        sendButton.setOnClickListener {
            sendMessage()
        }

        // Handle "Send" from soft keyboard
        editText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendMessage()
                true
            } else {
                false
            }
        }
    }

    private fun sendMessage() {
        val userMsg = editText.text.toString().trim()
        if (userMsg.isNotEmpty()) {
            adapter.addMessage(Message(userMsg, isUser = true))
            recyclerView.scrollToPosition(adapter.itemCount - 1)

            editText.text.clear()

            val botReply = chatbot.getResponse(userMsg)
            adapter.addMessage(Message(botReply, isUser = false))
            recyclerView.scrollToPosition(adapter.itemCount - 1)
        }
    }
}
