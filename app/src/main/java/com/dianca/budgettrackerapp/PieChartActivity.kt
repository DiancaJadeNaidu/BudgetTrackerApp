package com.dianca.budgettrackerapp

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dianca.budgettrackerapp.data.CategoryEntity
import com.dianca.budgettrackerapp.data.ExpenseEntity
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class PieChartActivity : BaseActivity() {

    private lateinit var pieChart: PieChart
    private lateinit var btnBack: Button
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pie)

        setupBottomNav()

        pieChart = findViewById(R.id.pieChart)
        btnBack = findViewById(R.id.btnBackToDashboard)

        btnBack.setOnClickListener { finish() }

        setupPieChartStyle()
        loadChartData()
    }

    private fun setupPieChartStyle() {
        // Pie chart visual enhancements
        pieChart.setUsePercentValues(false)
        pieChart.description.isEnabled = false
        pieChart.isDrawHoleEnabled = true
        pieChart.setHoleColor(Color.TRANSPARENT)
        pieChart.setTransparentCircleAlpha(0)

        // Center Text
        pieChart.setCenterTextSize(22f)
        pieChart.setCenterTextColor(Color.WHITE)
        pieChart.centerText = "Spending"

        // Entry Labels
        pieChart.setEntryLabelTextSize(16f)
        pieChart.setEntryLabelColor(Color.WHITE)
        pieChart.setExtraOffsets(20f, 20f, 20f, 20f)

        // Legend styling
        val legend = pieChart.legend
        legend.textSize = 16f
        legend.textColor = Color.BLUE
        legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        legend.orientation = Legend.LegendOrientation.HORIZONTAL
        legend.isWordWrapEnabled = true
        legend.isEnabled = true
    }

    private fun loadChartData() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val categoriesSnapshot = firestore.collection("categories").get().await()
                val expensesSnapshot = firestore.collection("expenses").get().await()

                val categoryMap = categoriesSnapshot.documents.associateBy(
                    { it.id },  // document ID = categoryId
                    { it.toObject(CategoryEntity::class.java)?.name ?: "Unknown" }
                )

                val expenses = expensesSnapshot.toObjects(ExpenseEntity::class.java)

                val totalsByCategory = mutableMapOf<String, Double>()
                for (expense in expenses) {
                    val categoryName = categoryMap[expense.categoryId] ?: "Uncategorized"
                    totalsByCategory[categoryName] = totalsByCategory.getOrDefault(categoryName, 0.0) + expense.amount
                }

                val entries = totalsByCategory.map { (category, total) ->
                    PieEntry(total.toFloat(), category)
                }

                runOnUiThread {
                    if (entries.isNotEmpty()) {
                        val dataSet = PieDataSet(entries, "")
                        dataSet.colors = listOf(
                            Color.parseColor("#FFA726"),
                            Color.parseColor("#66BB6A"),
                            Color.parseColor("#29B6F6"),
                            Color.parseColor("#EF5350"),
                            Color.parseColor("#AB47BC"),
                            Color.parseColor("#FF7043")
                        )

                        dataSet.valueTextSize = 18f
                        dataSet.valueTextColor = Color.WHITE

                        val data = PieData(dataSet)

                        pieChart.data = data
                        pieChart.invalidate()
                    } else {
                        pieChart.clear()
                        pieChart.centerText = "No data available"
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    pieChart.clear()
                    pieChart.centerText = "Error loading data"
                }
            }
        }
    }
}
