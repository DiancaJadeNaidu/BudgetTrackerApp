package com.dianca.budgettrackerapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val accountBtn = findViewById<Button>(R.id.account_button)
        val notificationsBtn = findViewById<Button>(R.id.notifications_button)
        val reportBtn = findViewById<Button>(R.id.report_button)
        val rewardsBtn = findViewById<Button>(R.id.rewards_button)
        val logoutBtn = findViewById<Button>(R.id.logout_button)

        accountBtn.setOnClickListener {
            startActivity(Intent(this, AccountActivity::class.java))
        }

        notificationsBtn.setOnClickListener {
            Toast.makeText(this, "🔔 Notification settings coming soon!", Toast.LENGTH_SHORT).show()
        }

        reportBtn.setOnClickListener {
            startActivity(Intent(this, ReportActivity::class.java))
        }

        rewardsBtn.setOnClickListener {
            Toast.makeText(this, "🏆 Viewing rewards...", Toast.LENGTH_SHORT).show()
        }

        logoutBtn.setOnClickListener {
            Toast.makeText(this, "🔒 Logged out successfully!", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, LoginActivity::class.java)

            //intent.flags basically clears back stack, meaning that when users click back they wont go to previous screen
            //they will just go to login screen
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

    }
    }

