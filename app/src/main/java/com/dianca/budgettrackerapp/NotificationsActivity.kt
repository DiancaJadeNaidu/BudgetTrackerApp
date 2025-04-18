package com.dianca.budgettrackerapp

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity

class NotificationsActivity : BaseActivity() {

    private lateinit var generalSwitch: Switch
    private lateinit var promoSwitch: Switch
    private lateinit var alertSwitch: Switch
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)

        setupBottomNav()

        generalSwitch = findViewById(R.id.general_notifications_switch)
        promoSwitch = findViewById(R.id.promotional_notifications_switch)
        alertSwitch = findViewById(R.id.alert_notifications_switch)

        sharedPreferences = getSharedPreferences("notification_prefs", MODE_PRIVATE)

        generalSwitch.isChecked = sharedPreferences.getBoolean("general", false)
        promoSwitch.isChecked = sharedPreferences.getBoolean("promotional", false)
        alertSwitch.isChecked = sharedPreferences.getBoolean("alert", false)

        generalSwitch.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean("general", isChecked).apply()
        }

        promoSwitch.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean("promotional", isChecked).apply()
        }

        alertSwitch.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean("alert", isChecked).apply()
        }
        setupBottomNav()
    }
}
