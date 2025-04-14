package com.dianca.budgettrackerapp

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity() {

    private lateinit var imgProfile: ImageView
    private lateinit var imgCategories: ImageView
    private lateinit var imgAddExpense: ImageView
    private lateinit var imgBudgetGoals: ImageView
    private lateinit var imgViewExpenses: ImageView
    private lateinit var imgCategorySummary: ImageView
    private lateinit var imgGraph: ImageView
    private lateinit var tvWelcome: TextView

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        auth = FirebaseAuth.getInstance()


        tvWelcome = findViewById(R.id.tvWelcome)
        imgProfile = findViewById(R.id.imgProfile)
        imgCategories = findViewById(R.id.imgCategories)
        imgAddExpense = findViewById(R.id.imgAddExpense)
        imgBudgetGoals = findViewById(R.id.imgBudgetGoals)
        imgViewExpenses = findViewById(R.id.imgViewExpenses)
        imgCategorySummary = findViewById(R.id.imgCategorySummary)
        imgGraph = findViewById(R.id.imgGraph)

        val currentUser = auth.currentUser
        currentUser?.email?.let { email ->
            val username = email.substringBefore("@")
            tvWelcome.text = "Welcome, ${username.capitalize()}!"
        }

        // Click listeners
        imgProfile.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        imgCategories.setOnClickListener {
            // startActivity(Intent(this, CategoriesActivity::class.java))
        }

        imgAddExpense.setOnClickListener {
             startActivity(Intent(this, AddExpenseActivity::class.java))
        }

        imgBudgetGoals.setOnClickListener {
             startActivity(Intent(this, BudgetGoalsActivity::class.java))
        }

        imgViewExpenses.setOnClickListener {
            // startActivity(Intent(this, ViewExpensesActivity::class.java))
        }

        imgCategorySummary.setOnClickListener {
            // startActivity(Intent(this, CategorySummaryActivity::class.java))
        }

        imgGraph.setOnClickListener {
            // startActivity(Intent(this, GraphActivity::class.java))
        }
    }
}
