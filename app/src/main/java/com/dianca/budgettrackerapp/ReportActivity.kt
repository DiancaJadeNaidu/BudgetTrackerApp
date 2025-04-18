package com.dianca.budgettrackerapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ReportActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)

        setupBottomNav()

        val issueMessage = findViewById<EditText>(R.id.issue_message)
        val sendButton = findViewById<Button>(R.id.send_message_button)

        sendButton.setOnClickListener {
            val message = issueMessage.text.toString().trim()
            if (message.isNotEmpty()) {
                Toast.makeText(this, "✅ Report has been submitted!", Toast.LENGTH_LONG).show()
                issueMessage.text.clear()
            } else {
                Toast.makeText(this, "⚠️ Please describe the issue first.", Toast.LENGTH_SHORT).show()
            }
        }
        setupBottomNav()
    }
}