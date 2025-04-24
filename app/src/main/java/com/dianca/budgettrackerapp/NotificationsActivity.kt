package com.dianca.budgettrackerapp

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity

class NotificationsActivity : BaseActivity() {

    //declare switch components for different notification types
    private lateinit var generalSwitch: Switch
    private lateinit var promoSwitch: Switch
    private lateinit var alertSwitch: Switch

    //sharedPreferences to store switch states persistently
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)

        //set up bottom navigation bar
        setupBottomNav()

        //initialize switch views by ID from layout
        generalSwitch = findViewById(R.id.general_notifications_switch)
        promoSwitch = findViewById(R.id.promotional_notifications_switch)
        alertSwitch = findViewById(R.id.alert_notifications_switch)

        //access SharedPreferences to store user preferences
        sharedPreferences = getSharedPreferences("notification_prefs", MODE_PRIVATE)

        //load saved preferences and apply them to switches
        generalSwitch.isChecked = sharedPreferences.getBoolean("general", false)
        promoSwitch.isChecked = sharedPreferences.getBoolean("promotional", false)
        alertSwitch.isChecked = sharedPreferences.getBoolean("alert", false)

        //save changes when the general notification switch is toggled
        generalSwitch.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean("general", isChecked).apply()
        }

        //save changes when the promotional notification switch is toggled
        promoSwitch.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean("promotional", isChecked).apply()
        }

        //save changes when the alert notification switch is toggled
        alertSwitch.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean("alert", isChecked).apply()
        }
        //set up bottom navigation again
        setupBottomNav()
    }
}
