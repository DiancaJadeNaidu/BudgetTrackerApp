package com.dianca.budgettrackerapp

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dianca.budgettrackerapp.data.BudgetGoalEntity
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class BudgetGoalsActivity : AppCompatActivity() {

    private lateinit var edtMinBudget: EditText
    private lateinit var edtMaxBudget: EditText
    private lateinit var seekBarMinBudget: SeekBar
    private lateinit var seekBarMaxBudget: SeekBar
    private lateinit var txtMinSeek: TextView
    private lateinit var txtMaxSeek: TextView
    private lateinit var btnSaveBudget: Button

    private val db by lazy { AppDatabase.getInstance(this) }
    private val maxSeekLimit = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_budget_goals)

        edtMinBudget = findViewById(R.id.edtMinBudget)
        edtMaxBudget = findViewById(R.id.edtMaxBudget)
        seekBarMinBudget = findViewById(R.id.seekBarMinBudget)
        seekBarMaxBudget = findViewById(R.id.seekBarMaxBudget)
        txtMinSeek = findViewById(R.id.txtMinSeekValue)
        txtMaxSeek = findViewById(R.id.txtMaxSeekValue)
        btnSaveBudget = findViewById(R.id.btnSaveBudget)

        seekBarMinBudget.max = maxSeekLimit
        seekBarMaxBudget.max = maxSeekLimit

        // Initial values
        seekBarMinBudget.progress = 100
        seekBarMaxBudget.progress = 500
        edtMinBudget.setText("100")
        edtMaxBudget.setText("500")
        txtMinSeek.text = "Min Budget: $100"
        txtMaxSeek.text = "Max Budget: $500"

        // SeekBar listeners
        seekBarMinBudget.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                edtMinBudget.setText(progress.toString())
                txtMinSeek.text = "Min Budget: $$progress"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        seekBarMaxBudget.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                edtMaxBudget.setText(progress.toString())
                txtMaxSeek.text = "Max Budget: $$progress"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // EditText watchers to update SeekBars
        edtMinBudget.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val value = s.toString().toIntOrNull()
                value?.let {
                    if (it in 0..maxSeekLimit) seekBarMinBudget.progress = it
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        edtMaxBudget.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val value = s.toString().toIntOrNull()
                value?.let {
                    if (it in 0..maxSeekLimit) seekBarMaxBudget.progress = it
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

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
                            edtMinBudget.setText("")
                            edtMaxBudget.setText("")
                            seekBarMinBudget.progress = 0
                            seekBarMaxBudget.progress = 0
                            txtMinSeek.text = "Min Budget: $0"
                            txtMaxSeek.text = "Max Budget: $0"
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
