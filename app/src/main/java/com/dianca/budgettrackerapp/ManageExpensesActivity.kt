package com.dianca.budgettrackerapp

import android.Manifest
import android.app.DatePickerDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dianca.budgettrackerapp.databinding.ActivityManageexpensesBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ManageExpensesActivity : BaseActivity() {

    private lateinit var binding: ActivityManageexpensesBinding
    private lateinit var adapter: ExpenseListAdapter
    private var allExpenses: List<ExpenseEntity> = emptyList()
    private val dateFormat = SimpleDateFormat("YYYY-MM-DD", Locale.getDefault())
    private var startDate: Calendar? = null
    private var endDate: Calendar? = null

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                loadExpenses()
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageexpensesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomNav()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            == PackageManager.PERMISSION_GRANTED
        ) {
            loadExpenses()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        setupRecyclerView()
        setupSearchFilter()
        setupAmountFilter()
        setupDatePickers()
    }

    private fun setupDatePickers() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val startDatePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                binding.startDateButton.text = selectedDate
                startDate = Calendar.getInstance().apply {
                    set(selectedYear, selectedMonth, selectedDay)
                }
                filterExpenses(binding.editTextSearch.text.toString(), binding.seekBarAmount.progress)
            },
            year, month, day
        )

        val endDatePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                binding.endDateButton.text = selectedDate
                endDate = Calendar.getInstance().apply {
                    set(selectedYear, selectedMonth, selectedDay)
                }
                filterExpenses(binding.editTextSearch.text.toString(), binding.seekBarAmount.progress)
            },
            year, month, day
        )

        binding.startDateButton.setOnClickListener {
            startDatePickerDialog.show()
        }

        binding.endDateButton.setOnClickListener {
            endDatePickerDialog.show()
        }
    }

    private fun setupRecyclerView() {
        adapter = ExpenseListAdapter(

            onDeleteClick = { expense ->
                lifecycleScope.launch {
                    AppDatabase.getInstance(applicationContext).expenseDao().delete(expense)
                    loadExpenses()
                    Toast.makeText(this@ManageExpensesActivity, "Deleted", Toast.LENGTH_SHORT).show()
                }
            }
        )
        binding.recyclerExpenses.layoutManager = LinearLayoutManager(this)
        binding.recyclerExpenses.adapter = adapter
    }

    private fun loadExpenses() {
        lifecycleScope.launch {
            val db = AppDatabase.getInstance(applicationContext)
            val expenses = db.expenseDao().getAll()
            val categories = db.categoryDao().getAll()

            val expensesWithCategoryName = expenses.map { expense ->
                val categoryName = categories.find { it.id == expense.categoryId }?.name ?: "Unknown"
                expense.copy(expenseName = "${expense.expenseName} ($categoryName)")
            }

            allExpenses = expensesWithCategoryName
            adapter.submitList(expensesWithCategoryName)
        }
    }

    private fun setupSearchFilter() {
        binding.editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterExpenses(s.toString(), binding.seekBarAmount.progress)
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupAmountFilter() {
        binding.seekBarAmount.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.txtSeekBarValue.text = "Max Amount: R$progress"
                filterExpenses(binding.editTextSearch.text.toString(), progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun filterExpenses(searchText: String, maxAmount: Int) {
        val filteredList = allExpenses.filter { expense ->
            val matchesSearch = expense.expenseName.contains(searchText, ignoreCase = true)
            val matchesAmount = expense.amount <= maxAmount

            // Parse the expense's start and end dates using the `parseDate` method
            val expenseStartCal = parseDate(expense.startDate)
            val expenseEndCal = parseDate(expense.endDate)

            // Date filtering logic
            val matchesDate = when {
                startDate != null && endDate != null -> {
                    val start = trimTime(startDate!!)
                    val end = trimTime(endDate!!)

                    // Ensure the expense dates are within the selected range
                    (expenseStartCal != null && !expenseStartCal.before(start) && !expenseStartCal.after(end)) ||
                            (expenseEndCal != null && !expenseEndCal.before(start) && !expenseEndCal.after(end))
                }
                startDate != null -> {
                    val start = trimTime(startDate!!)
                    (expenseStartCal != null && !expenseStartCal.before(start)) ||
                            (expenseEndCal != null && !expenseEndCal.before(start))
                }
                endDate != null -> {
                    val end = trimTime(endDate!!)
                    (expenseStartCal != null && !expenseStartCal.after(end)) ||
                            (expenseEndCal != null && !expenseEndCal.after(end))
                }
                else -> true // No date filters applied
            }

            matchesSearch && matchesAmount && matchesDate
        }

        adapter.submitList(filteredList)
    }


    private fun parseDate(dateStr: String?): Calendar? {
        return try {
            if (dateStr.isNullOrBlank()) return null
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())  // Update to dd/MM/yyyy
            val date = dateFormat.parse(dateStr)
            Calendar.getInstance().apply {
                time = date!!
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
        } catch (e: Exception) {
            null
        }
    }




    private fun trimTime(calendar: Calendar): Calendar {
        return Calendar.getInstance().apply {
            timeInMillis = calendar.timeInMillis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
    }
}
