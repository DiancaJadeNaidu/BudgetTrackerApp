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

        //set up bottom navigation bar
        setupBottomNav()

        //get references to the input field and button from the layout
        val issueMessage = findViewById<EditText>(R.id.issue_message)
        val sendButton = findViewById<Button>(R.id.send_message_button)

        //handle send button click
        sendButton.setOnClickListener {
            val message = issueMessage.text.toString().trim()

            //if message is not empty, show confirmation and clear the input
            if (message.isNotEmpty()) {
                Toast.makeText(this, "✅ Report has been submitted!", Toast.LENGTH_LONG).show()
                issueMessage.text.clear()
            } else {
                //show warning if message is empty
                Toast.makeText(this, "⚠️ Please describe the issue first.", Toast.LENGTH_SHORT).show()
            }
        }
        //setup bottom navigation again
        setupBottomNav()
    }
}