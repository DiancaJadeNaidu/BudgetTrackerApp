package com.dianca.budgettrackerapp

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dianca.budgettrackerapp.databinding.ActivityManageexpensesBinding
import kotlinx.coroutines.launch

class ManageExpensesActivity : BaseActivity() {

    private lateinit var binding: ActivityManageexpensesBinding
    private lateinit var adapter: ExpenseListAdapter
    private var allExpenses: List<ExpenseEntity> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageexpensesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomNav()

        setupRecyclerView()
        loadExpenses()

        setupSearchFilter()
        setupAmountFilter()

        setupBottomNav()
    }

    private fun setupRecyclerView() {
        adapter = ExpenseListAdapter(
            onEditClick = { expense ->
                Toast.makeText(this, "Edit clicked for: ${expense.expenseName}", Toast.LENGTH_SHORT).show()
            },
            onDeleteClick = { expense ->
                lifecycleScope.launch {
                    AppDatabase.getInstance(applicationContext).expenseDao().delete(expense)
                    loadExpenses() // Refresh the list after deletion
                    Toast.makeText(this@ManageExpensesActivity, "Deleted", Toast.LENGTH_SHORT).show()
                }
            }
        )
        binding.recyclerExpenses.layoutManager = LinearLayoutManager(this)
        binding.recyclerExpenses.adapter = adapter
    }


    private fun loadExpenses() {
        lifecycleScope.launch {
            val expenses = AppDatabase.getInstance(applicationContext).expenseDao().getAll()
            allExpenses = expenses
            adapter.submitList(expenses)
        }
    }

    private fun setupSearchFilter() {
        binding.editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                filterExpenses(charSequence.toString(), binding.seekBarAmount.progress)
            }
            override fun afterTextChanged(editable: Editable?) {}
        })
    }

    private fun setupAmountFilter() {
        binding.seekBarAmount.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val maxAmountText = "R${progress}" // Corrected string interpolation
                binding.txtSeekBarValue.text = "Max Amount: $maxAmountText"
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
            matchesSearch && matchesAmount
        }
        adapter.submitList(filteredList)
    }
}
