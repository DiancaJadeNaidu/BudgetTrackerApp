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

class BudgetGoalsActivity : BaseActivity() {

    //UI elements
    private lateinit var edtMinBudget: EditText
    private lateinit var edtMaxBudget: EditText
    private lateinit var seekBarMinBudget: SeekBar
    private lateinit var seekBarMaxBudget: SeekBar
    private lateinit var txtMinSeek: TextView
    private lateinit var txtMaxSeek: TextView
    private lateinit var btnSaveBudget: Button

    //database instance
    private val db by lazy { AppDatabase.getInstance(this) }
    private val maxSeekLimit = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_budget_goals)

        //setup bottom navigation bar
        setupBottomNav()

        //link views with layout
        edtMinBudget = findViewById(R.id.edtMinBudget)
        edtMaxBudget = findViewById(R.id.edtMaxBudget)
        seekBarMinBudget = findViewById(R.id.seekBarMinBudget)
        seekBarMaxBudget = findViewById(R.id.seekBarMaxBudget)
        txtMinSeek = findViewById(R.id.txtMinSeekValue)
        txtMaxSeek = findViewById(R.id.txtMaxSeekValue)
        btnSaveBudget = findViewById(R.id.btnSaveBudget)

        //set seekbar limits and default values
        seekBarMinBudget.max = maxSeekLimit
        seekBarMaxBudget.max = maxSeekLimit

        seekBarMinBudget.progress = 100
        seekBarMaxBudget.progress = 500
        edtMinBudget.setText("100")
        edtMaxBudget.setText("500")
        txtMinSeek.text = "Min Budget: R100"
        txtMaxSeek.text = "Max Budget: R500"

        //update EditText and label when seekbar changes
        seekBarMinBudget.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                edtMinBudget.setText(progress.toString())
                txtMinSeek.text = "Min Budget: R$progress"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        seekBarMaxBudget.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                edtMaxBudget.setText(progress.toString())
                txtMaxSeek.text = "Max Budget: R$progress"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        //update seekbar when EditText changes
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

        //save budget goals when button is clicked
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

                    //save goal to database
                    lifecycleScope.launch {
                        try {
                            db.budgetGoalDAO().insert(goal)
                            Toast.makeText(this@BudgetGoalsActivity, "Budget goals saved!", Toast.LENGTH_SHORT).show()
                            //reset UI
                            edtMinBudget.setText("")
                            edtMaxBudget.setText("")
                            seekBarMinBudget.progress = 0
                            seekBarMaxBudget.progress = 0
                            txtMinSeek.text = "Min Budget: R0"
                            txtMaxSeek.text = "Max Budget: R0"
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

    //returns formatted current month
    private fun getCurrentMonthFormatted(): String {
        val dateFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        return dateFormat.format(Date())
    }
}
