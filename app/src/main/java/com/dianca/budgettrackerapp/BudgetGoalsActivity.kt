package com.dianca.budgettrackerapp

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.lifecycle.lifecycleScope
import com.dianca.budgettrackerapp.data.BudgetGoalDAO
import com.dianca.budgettrackerapp.data.BudgetGoalEntity
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * Attribution:
 * Website: Android x Kotlin Beginner Tutorial : Budget Tracker App [2021]
 *
 * Author: The Zone
 * URL: https://www.youtube.com/playlist?list=PLpZQVidZ65jPUF-o0LUvkY-XVAAkvL-Xb
 * Accessed on: 2025-06-07
 */

class BudgetGoalsActivity : BaseActivity() {


    private lateinit var edtSalary: EditText
    private lateinit var edtMinBudget: EditText
    private lateinit var edtMaxBudget: EditText
    private lateinit var seekBarMinBudget: SeekBar
    private lateinit var seekBarMaxBudget: SeekBar
    private lateinit var txtMinSeek: TextView
    private lateinit var txtMaxSeek: TextView
    private lateinit var btnSaveBudget: Button
    private lateinit var txtDisplayGoals: TextView

    //helpers
    private val maxSeekLimit = 1_000
    private val goalDAO = BudgetGoalDAO()
    private val currentMonth by lazy { getCurrentMonthFormatted() }

    //lifecycle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_budget_goals)

        setupBottomNav()
        bindViews()
        initSeekBarsAndInputs()
        initListeners()

        //show any saved goals as soon as we open the screen
        loadGoalsForMonth(currentMonth)
    }

    private fun bindViews() {
        edtSalary        = findViewById(R.id.edtSalary)
        edtMinBudget     = findViewById(R.id.edtMinBudget)
        edtMaxBudget     = findViewById(R.id.edtMaxBudget)
        seekBarMinBudget = findViewById(R.id.seekBarMinBudget)
        seekBarMaxBudget = findViewById(R.id.seekBarMaxBudget)
        txtMinSeek       = findViewById(R.id.txtMinSeekValue)
        txtMaxSeek       = findViewById(R.id.txtMaxSeekValue)
        txtDisplayGoals  = findViewById(R.id.txtDisplayGoals)
        btnSaveBudget    = findViewById(R.id.btnSaveBudget)
    }

    private fun initSeekBarsAndInputs() {
        seekBarMinBudget.max = maxSeekLimit
        seekBarMaxBudget.max = maxSeekLimit

        seekBarMinBudget.progress = 100
        seekBarMaxBudget.progress = 500
        edtMinBudget.setText("100")
        edtMaxBudget.setText("500")
        txtMinSeek.text = "Min Budget: R100"
        txtMaxSeek.text = "Max Budget: R500"
    }

    private fun initListeners() {
        //seekbar code
        seekBarMinBudget.setOnSeekBarChangeListener(seekBarListener(edtMinBudget, txtMinSeek, "Min Budget"))
        seekBarMaxBudget.setOnSeekBarChangeListener(seekBarListener(edtMaxBudget, txtMaxSeek, "Max Budget"))
        addSeekSyncListener(edtMinBudget, seekBarMinBudget)
        addSeekSyncListener(edtMaxBudget, seekBarMaxBudget)

        //save btn
        btnSaveBudget.setOnClickListener { saveGoals() }
    }

    //saving logic
    private fun saveGoals() {
        val salary    = edtSalary.text.toString().toDoubleOrNull()
        val minBudget = edtMinBudget.text.toString().toDoubleOrNull()
        val maxBudget = edtMaxBudget.text.toString().toDoubleOrNull()

        if (salary == null || minBudget == null || maxBudget == null) {
            showToast("Please enter valid salary and budget amounts.")
            return
        }
        if (maxBudget > salary) {
            showToast("Error: Max budget cannot exceed salary (R$salary)")
            return
        }
        if (minBudget > maxBudget) {
            showToast("Min budget must be less than or equal to max budget.")
            return
        }

        val goal = BudgetGoalEntity(
            id = UUID.randomUUID().toString(),   //always a fresh document
            categoryId = "1",
            minGoalAmount = minBudget,
            maxGoalAmount = maxBudget,
            month = currentMonth
        )

        lifecycleScope.launch {
            try {
                goalDAO.insert(goal)
                showToast("Budget goals saved successfully!")
                loadGoalsForMonth(currentMonth)   //refresh label
                resetUI()
            } catch (e: Exception) {
                showToast("Failed to save to Firestore.")
            }
        }
    }

    //read and display goals
    private fun loadGoalsForMonth(month: String) {
        lifecycleScope.launch {
            val goals = goalDAO.getGoalsForMonth(month)
            val latestGoal = goals.lastOrNull()          //choose the newest record
            if (latestGoal != null) {
                txtDisplayGoals.text =
                    "Min Budget for $month: R${latestGoal.minGoalAmount}\n" +
                            "Max Budget for $month: R${latestGoal.maxGoalAmount}"
            } else {
                txtDisplayGoals.text = "No budget goals set for $month yet."
            }
        }
    }

    private fun resetUI() {
        edtSalary.setText("")
        edtMinBudget.setText("")
        edtMaxBudget.setText("")
        seekBarMinBudget.progress = 0
        seekBarMaxBudget.progress = 0
        txtMinSeek.text = "Min Budget: R0"
        txtMaxSeek.text = "Max Budget: R0"
    }

    private fun seekBarListener(edit: EditText, label: TextView, prefix: String) =
        object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, value: Int, fromUser: Boolean) {
                edit.setText(value.toString())
                label.text = "$prefix: R$value"
            }
            override fun onStartTrackingTouch(sb: SeekBar?) {}
            override fun onStopTrackingTouch(sb: SeekBar?) {}
        }

    private fun addSeekSyncListener(edit: EditText, seek: SeekBar) {
        edit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                s?.toString()?.toIntOrNull()?.let {
                    if (it in 0..maxSeekLimit) seek.progress = it
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun getCurrentMonthFormatted(): String =
        SimpleDateFormat("MMMM", Locale.getDefault()).format(Date())

    private fun showToast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}
