package com.dianca.budgettrackerapp

import android.os.Bundle

class SummaryActivity : BaseActivity() { // Changed from AppCompatActivity to BaseActivity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the layout for this activity
        setContentView(R.layout.activity_summary)

        // Set up the bottom navigation bar from BaseActivity
        setupBottomNav()

        // Replace fragment container with SummaryFragment
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, SummaryFragment())
            .commit()
    }
}
