package com.dianca.budgettrackerapp

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.lifecycle.lifecycleScope
import com.dianca.budgettrackerapp.data.*
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

/**
 * Attribution:
 * Website: Android – Create a Pie Chart with Kotlin – GeeksforGeeks.
 *
 *  Author: GeeksforGeeks Team
 *  URL: https://www.geeksforgeeks.org/android-create-a-pie-chart-with-kotlin/
 *  Accessed on: 2025-06-07
 */


class PieChartActivity : BaseActivity() {

    //views
    private lateinit var pieChart    : PieChart
    private lateinit var spinnerMonth: Spinner
    private lateinit var spinnerMode : Spinner
    private lateinit var txtGoals    : TextView
    private lateinit var txtStatus   : TextView
    private lateinit var btnBack     : Button

    //helpers
    private val firestore = FirebaseFirestore.getInstance()
    private val goalDAO   = BudgetGoalDAO()

    private val monthFmtName = SimpleDateFormat("MMMM", Locale.getDefault())      // "June"
    private val monthFmtFull = SimpleDateFormat("MMMM yyyy", Locale.getDefault()) // "June 2025"
    private var selectedLabel = monthFmtFull.format(Date())   // default = this month
    private var showPercent   = true

    private val COLORS = listOf(
        Color.parseColor("#FFA726"), Color.parseColor("#66BB6A"),
        Color.parseColor("#29B6F6"), Color.parseColor("#EF5350"),
        Color.parseColor("#AB47BC"), Color.parseColor("#FF7043"),
        Color.parseColor("#8D6E63"), Color.parseColor("#26C6DA")
    )

    //lifecycle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pie)

        setupBottomNav()
        bindViews()
        configureSpinners()
        stylePieChart()

        btnBack.setOnClickListener { finish() }
    }

    private fun bindViews() {
        pieChart     = findViewById(R.id.pieChart)
        spinnerMonth = findViewById(R.id.spinnerMonth)
        spinnerMode  = findViewById(R.id.spinnerMode)
        txtGoals     = findViewById(R.id.txtGoals)
        txtStatus    = findViewById(R.id.txtStatus)
        btnBack      = findViewById(R.id.btnBackToDashboard)
    }

    //setup for spinner
    private fun configureSpinners() {
        val months = generatePast12Months()
        spinnerMonth.adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_item, months
        ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
        spinnerMonth.setSelection(0)

        val modes = resources.getStringArray(R.array.value_modes).toList()
        spinnerMode.adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_item, modes
        ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
        spinnerMode.setSelection(0)

        spinnerMonth.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p: AdapterView<*>, v: View?, pos: Int, id: Long) {
                    selectedLabel = months[pos]
                    loadChartData()
                }
                override fun onNothingSelected(parent: AdapterView<*>) {}
            }

        spinnerMode.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p: AdapterView<*>, v: View?, pos: Int, id: Long) {
                    showPercent = (pos == 0)
                    loadChartData()
                }
                override fun onNothingSelected(parent: AdapterView<*>) {}
            }
    }

    private fun generatePast12Months(): List<String> {
        val cal = Calendar.getInstance()
        return (0 until 12).map {
            val label = monthFmtFull.format(cal.time)
            cal.add(Calendar.MONTH, -1)
            label
        }
    }

    //chart
    private fun stylePieChart() = pieChart.apply {
        description.isEnabled = false
        isDrawHoleEnabled = true
        setHoleColor(Color.TRANSPARENT)
        setTransparentCircleAlpha(0)
        setEntryLabelColor(Color.WHITE)
        setEntryLabelTextSize(14f)
        setCenterTextTypeface(Typeface.DEFAULT_BOLD)
        setCenterTextColor(Color.WHITE)
        legend.apply {
            textColor = Color.WHITE
            textSize  = 14f
            verticalAlignment   = Legend.LegendVerticalAlignment.BOTTOM
            horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
            orientation         = Legend.LegendOrientation.HORIZONTAL
            isWordWrapEnabled   = true
        }
    }

    //main loader
    private fun loadChartData() = lifecycleScope.launch(Dispatchers.IO) {
        try {
            //categories (id to name)
            val catMap = firestore.collection("categories")
                .get().await()
                .documents.associateBy(
                    { it.id },
                    { it.toObject(CategoryEntity::class.java)?.name ?: "Unknown" }
                )

            //expenses filtered to month
            val allExpenses = firestore.collection("expenses")
                .get().await()
                .toObjects(ExpenseEntity::class.java)

            val (startMs, endMs) = monthBounds(selectedLabel)
            val expensesThisMonth = allExpenses.filter { it.timestamp in startMs..endMs }

            //aggregate totals + grand total
            var grandTotal = 0.0
            val totals = mutableMapOf<String, Double>()
            for (e in expensesThisMonth) {
                val cat = catMap[e.categoryId] ?: "Uncategorised"
                val newTotal = totals.getOrDefault(cat, 0.0) + e.amount
                totals[cat] = newTotal
                grandTotal += e.amount
            }

            //latest goal (month name only)
            val monthNameOnly = monthFmtName.format(monthFmtFull.parse(selectedLabel)!!)
            val goal = goalDAO.getGoalsForMonth(monthNameOnly).lastOrNull()

            //build chart data
            val entries = totals.map { PieEntry(it.value.toFloat(), it.key) }
            val dataSet = PieDataSet(entries, "Spending by Category").apply {
                colors = COLORS
                valueTextColor = Color.WHITE
                valueTextSize  = 16f
            }
            val data = PieData(dataSet)

            if (showPercent) {
                pieChart.setUsePercentValues(true)
                data.setValueFormatter(PercentFormatter(pieChart))
                pieChart.centerText = "${selectedLabel}\n(%)"
            } else {
                pieChart.setUsePercentValues(false)
                data.setValueFormatter(object : ValueFormatter() {
                    override fun getPieLabel(value: Float, entry: PieEntry?): String =
                        "R${"%.0f".format(value)}"
                })
                pieChart.centerText = "${selectedLabel}\n(Rands)"
            }

          //ui thread
            runOnUiThread {
                if (entries.isEmpty()) {
                    pieChart.clear()
                    pieChart.centerText = "No data\n$selectedLabel"
                } else {
                    pieChart.data = data
                    pieChart.animateY(800)
                    pieChart.invalidate()
                }

                //goal read-out
                txtGoals.text = goal?.let {
                    "Goal for $monthNameOnly →  Min R${it.minGoalAmount}  •  Max R${it.maxGoalAmount}"
                } ?: "No budget goals saved for $monthNameOnly"

                //red(bad) and green(good) messages
                if (goal != null) {
                    txtStatus.visibility = View.VISIBLE
                    if (grandTotal > goal.maxGoalAmount) {
                        txtStatus.setTextColor(Color.RED)
                        txtStatus.text =
                            "⚠  You have EXCEEDED your budget!\nSpent R${"%.0f".format(grandTotal)} / Max R${goal.maxGoalAmount}"
                    } else {
                        txtStatus.setTextColor(Color.parseColor("#00FF00"))
                        txtStatus.text =
                            "✓  You are within budget.\nSpent R${"%.0f".format(grandTotal)} / Max R${goal.maxGoalAmount}"
                    }
                } else {
                    txtStatus.visibility = View.GONE
                }
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
            runOnUiThread {
                pieChart.clear()
                pieChart.centerText = "Error loading data"
                txtGoals.text = ""
                txtStatus.visibility = View.GONE
                Toast.makeText(this@PieChartActivity,
                    "Failed to load chart.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /** Returns first & last millis for label like "June 2025" */
    private fun monthBounds(label: String): Pair<Long, Long> {
        val cal = Calendar.getInstance().apply {
            time = monthFmtFull.parse(label) ?: Date()
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val start = cal.timeInMillis
        cal.add(Calendar.MONTH, 1)
        val end = cal.timeInMillis - 1
        return start to end
    }
}
