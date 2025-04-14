package com.dianca.budgettrackerapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dianca.budgettrackerapp.data.AppDatabase
import com.dianca.budgettrackerapp.data.BudgetGoalEntity
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class BudgetGoalsActivity : AppCompatActivity() {

    private lateinit var edtMinBudget: EditText
    private lateinit var edtMaxBudget: EditText
    private lateinit var btnSaveBudget: Button

    private val db by lazy { AppDatabase.getInstance(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_budget_goals)

        edtMinBudget = findViewById(R.id.edtMinBudget)
        edtMaxBudget = findViewById(R.id.edtMaxBudget)
        btnSaveBudget = findViewById(R.id.btnSaveBudget)

        //dynamically getting month
        val currentMonth = getCurrentMonthFormatted()

        btnSaveBudget.setOnClickListener {
            val minBudget = edtMinBudget.text.toString().toDoubleOrNull()
            val maxBudget = edtMaxBudget.text.toString().toDoubleOrNull()

            if (minBudget != null && maxBudget != null) {
                if (minBudget <= maxBudget) {
                    val goal = BudgetGoalEntity(
                        categoryId = 1,
                        minGoalAmount = minBudget,
                        maxGoalAmount = maxBudget,
                        month = currentMonth
                    )

                    lifecycleScope.launch {
                        try {
                            db.budgetGoalDAO().insert(goal)
                            Toast.makeText(this@BudgetGoalsActivity, "Budget goals saved!", Toast.LENGTH_SHORT).show()
                            edtMinBudget.text.clear()
                            edtMaxBudget.text.clear()
                        } catch (e: Exception) {
                            Toast.makeText(this@BudgetGoalsActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Minimum budget cannot be greater than maximum budget.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please enter valid numbers.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getCurrentMonthFormatted(): String {
        val dateFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        return dateFormat.format(Date())
    }
}
