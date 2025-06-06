package com.dianca.budgettrackerapp

import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dianca.budgettrackerapp.data.CategoryEntity
import com.dianca.budgettrackerapp.data.ExpenseEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class BudgetProgressActivity : BaseActivity() {

    private val firestore = FirebaseFirestore.getInstance()
    private lateinit var layoutContainer: LinearLayout
    private lateinit var titleText: TextView
    private lateinit var btnBack: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_progress)

        setupBottomNav()

        layoutContainer = findViewById(R.id.layoutProgressBars)
        titleText = findViewById(R.id.txtProgressTitle)
        btnBack = findViewById(R.id.btnBack)

        titleText.text = "Progress Screen"

        btnBack.setOnClickListener {
            finish() // Go back to the previous activity
        }

        loadProgressData()
    }

    private fun loadProgressData() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val expensesSnapshot = firestore.collection("expenses").get().await()
                val categoriesSnapshot = firestore.collection("categories").get().await()

                val categoryMap = categoriesSnapshot.documents.associateBy(
                    { it.id },
                    { it.toObject(CategoryEntity::class.java)?.name ?: "Unknown" }
                )

                val categorySpendingMap = mutableMapOf<String, Double>()

                for (doc in expensesSnapshot.documents) {
                    val expense = doc.toObject(ExpenseEntity::class.java) ?: continue
                    val categoryName = categoryMap[expense.categoryId] ?: "Unknown"
                    categorySpendingMap[categoryName] =
                        categorySpendingMap.getOrDefault(categoryName, 0.0) + expense.amount
                }

                runOnUiThread {
                    layoutContainer.removeAllViews()

                    for ((category, totalSpent) in categorySpendingMap) {
                        val label = TextView(this@BudgetProgressActivity).apply {
                            text = "Category: $category    Spent: R$totalSpent"
                            setTextColor(Color.WHITE)
                            textSize = 16f
                        }

                        val progress = ProgressBar(
                            this@BudgetProgressActivity,
                            null,
                            android.R.attr.progressBarStyleHorizontal
                        ).apply {
                            max = 5000
                            progress = totalSpent.toInt().coerceAtMost(5000)
                            progressDrawable.setTint(Color.GREEN)
                            layoutParams = LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                            ).apply {
                                topMargin = 10
                                bottomMargin = 30
                            }
                        }

                        layoutContainer.addView(label)
                        layoutContainer.addView(progress)
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
