package com.dianca.budgettrackerapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class SummaryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //set the layout for this activity
        setContentView(R.layout.activity_summary)

        //replace fragment container with SummaryFragment
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, SummaryFragment())
            .commit() //commit basically saves
    }
}
