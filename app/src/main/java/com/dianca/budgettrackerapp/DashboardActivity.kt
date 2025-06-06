package com.dianca.budgettrackerapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class DashboardActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        setupBottomNav()

        val btnViewGraph = findViewById<Button>(R.id.btnViewGraph)
        val btnViewProgress = findViewById<Button>(R.id.btnViewProgress)

        btnViewGraph.setOnClickListener {
            startActivity(Intent(this, PieChartActivity::class.java))
        }

        btnViewProgress.setOnClickListener {
            startActivity(Intent(this, BudgetProgressActivity::class.java))
        }
    }
}
