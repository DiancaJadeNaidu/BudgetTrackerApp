package com.dianca.budgettrackerapp

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dianca.budgettrackerapp.data.CategoryEntity
import com.dianca.budgettrackerapp.data.ExpenseEntity
import com.dianca.budgettrackerapp.data.BudgetGoalDAO
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

/**
 * Attribution:
 * Website: Android ProgressBar using Kotlin – DigitalOcean Community.
 *
 *  Author: Anupam Chugh
 *  URL: https://www.digitalocean.com/community/tutorials/android-progressbar-using-kotlin
 *  Accessed on: 2025-06-07
 */


class BudgetProgressActivity : BaseActivity() {

    private lateinit var spinnerMonth  : Spinner
    private lateinit var txtGoalStatus : TextView
    private lateinit var layoutBars    : LinearLayout
    private lateinit var btnBack       : Button

    //helpers
    private val firestore = FirebaseFirestore.getInstance()
    private val goalDAO   = BudgetGoalDAO()

    private val monthFmtFull = SimpleDateFormat("MMMM yyyy", Locale.getDefault()) //"June 2025"
    private val monthFmtName = SimpleDateFormat("MMMM",      Locale.getDefault()) //"June"
    private var selectedLabel = monthFmtFull.format(Date())                       //default = now

    //lifecycle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_progress)

        setupBottomNav()

        //bind views
        spinnerMonth  = findViewById(R.id.spinnerMonth)
        txtGoalStatus = findViewById(R.id.txtGoalStatus)
        layoutBars    = findViewById(R.id.layoutProgressBars)
        btnBack       = findViewById(R.id.btnBack)

        btnBack.setOnClickListener { finish() }

        configureSpinner()
    }

    //month chooser
    private fun configureSpinner() {
        val months = generatePast12Months()
        spinnerMonth.adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_item, months
        ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
        spinnerMonth.setSelection(0)

        spinnerMonth.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View?, pos: Int, id: Long
            ) {
                selectedLabel = months[pos]
                loadProgressData()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        //first load (current month)
        loadProgressData()
    }

    private fun generatePast12Months(): List<String> {
        val cal = Calendar.getInstance()
        return (0 until 12).map {
            val lbl = monthFmtFull.format(cal.time)
            cal.add(Calendar.MONTH, -1)
            lbl
        }
    }

    //core loader
    private fun loadProgressData() = lifecycleScope.launch(Dispatchers.IO) {
        try {

            val catMap = firestore.collection("categories")
                .get().await()
                .documents.associateBy(
                    { it.id },
                    { it.toObject(CategoryEntity::class.java)?.name ?: "Unknown" }
                )

            //expenses filtered to selected month
            val allExpenses = firestore.collection("expenses")
                .get().await()
                .toObjects(ExpenseEntity::class.java)

            val (startMs, endMs) = monthBounds(selectedLabel)
            val expenses = allExpenses.filter { it.timestamp in startMs..endMs }

            //aggregate per category + grand total
            var grandTotal = 0.0
            val totals = mutableMapOf<String, Double>()
            for (e in expenses) {
                val cat = catMap[e.categoryId] ?: "Uncategorised"
                val newTotal = totals.getOrDefault(cat, 0.0) + e.amount
                totals[cat] = newTotal
                grandTotal += e.amount
            }

            //Goal for this month (if any)
            val monthNameOnly = monthFmtName.format(monthFmtFull.parse(selectedLabel)!!)
            val goal = goalDAO.getGoalsForMonth(monthNameOnly).lastOrNull()

            runOnUiThread {
                // Overall banner
                txtGoalStatus.text = when (goal) {
                    null -> "No goals saved for $monthNameOnly"
                    else -> {
                        val within = grandTotal in goal.minGoalAmount..goal.maxGoalAmount
                        val over   = grandTotal > goal.maxGoalAmount
                        txtGoalStatus.setTextColor(
                            when {
                                over    -> Color.RED
                                within  -> Color.parseColor("#00FF00")
                                else    -> Color.YELLOW
                            }
                        )
                        "Spent R${"%.0f".format(grandTotal)}  /  Range R${goal.minGoalAmount} – R${goal.maxGoalAmount}"
                    }
                }

                //per-category bars
                layoutBars.removeAllViews()
                for ((category, spent) in totals) {
                    //label
                    val lbl = TextView(this@BudgetProgressActivity).apply {
                        text = "$category  •  R${"%.0f".format(spent)}"
                        setTextColor(Color.WHITE)
                        textSize = 16f
                    }
                    layoutBars.addView(lbl)

                    //progress bar
                    val bar = ProgressBar(
                        this@BudgetProgressActivity,
                        null,
                        android.R.attr.progressBarStyleHorizontal
                    ).apply {
                        val maxVal = (goal?.maxGoalAmount ?: spent).toInt()
                        max = if (maxVal == 0) 1 else maxVal
                        progress = spent.toInt().coerceAtMost(max)

                        //colour logic
                        val tint = when {
                            goal == null                       -> Color.CYAN
                            spent > goal.maxGoalAmount         -> Color.RED
                            spent < goal.minGoalAmount         -> Color.YELLOW
                            else                               -> Color.GREEN
                        }
                        progressDrawable.mutate().setTint(tint)

                        layoutParams = LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        ).apply {
                            topMargin = 8
                            bottomMargin = 24
                        }
                    }
                    layoutBars.addView(bar)
                }

                if (totals.isEmpty()) {
                    val none = TextView(this@BudgetProgressActivity).apply {
                        text = "No expenses captured for $selectedLabel"
                        setTextColor(Color.LTGRAY)
                        textSize = 14f
                    }
                    layoutBars.addView(none)
                }
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
            runOnUiThread {
                txtGoalStatus.setTextColor(Color.RED)
                txtGoalStatus.text = "Error loading data."
                layoutBars.removeAllViews()
            }
        }
    }

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
