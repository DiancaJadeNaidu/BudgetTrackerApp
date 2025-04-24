package com.dianca.budgettrackerapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

abstract class BaseActivity : AppCompatActivity() {

    //sets up the bottom navigation bar
    protected fun setupBottomNav() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)

        //handle nav item clicks
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    //navigate to home screen
                    startActivity(Intent(this, HomeActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.nav_profile -> {
                    //navigate to profile screen
                    startActivity(Intent(this, ProfileActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.nav_settings -> {
                    //navigate to settings screen
                    startActivity(Intent(this, SettingsActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                else -> false
            }
        }
        //make sure no item is pre-selected
        bottomNav.menu.setGroupCheckable(0, true, false)
        for (i in 0 until bottomNav.menu.size()) {
            bottomNav.menu.getItem(i).isChecked = false
        }
    }

}
