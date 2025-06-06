package com.dianca.budgettrackerapp

import android.Manifest
import android.app.DatePickerDialog
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dianca.budgettrackerapp.data.CategoryEntity
import com.dianca.budgettrackerapp.data.ExpenseEntity
import com.dianca.budgettrackerapp.databinding.ActivityManageexpensesBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

class ManageExpensesActivity : BaseActivity() {

    private lateinit var binding: ActivityManageexpensesBinding
    private lateinit var adapter: ExpenseListAdapter

    private var allExpenses: List<ExpenseEntity> = emptyList()
    private var startDate: Calendar? = null
    private var endDate: Calendar?   = null

    private val db = FirebaseFirestore.getInstance()

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) loadExpenses() else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }

    // ─────────────────────────────────────────
    // lifecycle
    // ─────────────────────────────────────────
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageexpensesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomNav()
        checkStoragePermissionAndLoad()
        setupRecyclerView()
        setupSearchFilter()
        setupDatePickers()
    }

    // ─────────────────────────────────────────
    // permissions & initial load
    // ─────────────────────────────────────────
    private fun checkStoragePermissionAndLoad() {
        val readPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (ContextCompat.checkSelfPermission(this, readPermission)
            == PackageManager.PERMISSION_GRANTED
        ) {
            loadExpenses()
        } else {
            requestPermissionLauncher.launch(readPermission)
        }
    }

    // ─────────────────────────────────────────
    // Recycler
    // ─────────────────────────────────────────
    private fun setupRecyclerView() {
        adapter = ExpenseListAdapter(
            onDeleteClick = { expense ->
                lifecycleScope.launch(Dispatchers.IO) {
                    try {
                        db.collection("expenses").document(expense.id).delete().await()
                        loadExpenses()
                        runOnUiThread {
                            Toast.makeText(this@ManageExpensesActivity, "Deleted", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        runOnUiThread {
                            Toast.makeText(this@ManageExpensesActivity, "Failed to delete", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        )
        binding.recyclerExpenses.layoutManager = LinearLayoutManager(this)
        binding.recyclerExpenses.adapter = adapter
    }

    // ─────────────────────────────────────────
    // Live text search
    // ─────────────────────────────────────────
    private fun setupSearchFilter() {
        binding.editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterExpenses(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    // ─────────────────────────────────────────
    // Date pickers  (INCLUSIVE!)
    // ─────────────────────────────────────────
    private fun setupDatePickers() {
        val today = Calendar.getInstance()
        val (year, month, day) =
            arrayOf(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH))

        val startPicker = DatePickerDialog(this, { _, y, m, d ->
            startDate = Calendar.getInstance().apply {
                set(y, m, d, 0, 0, 0)          // ← 00:00:00.000  (truncate)
                set(Calendar.MILLISECOND, 0)
            }
            binding.startDateButton.text =
                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(startDate!!.time)
            filterExpenses(binding.editTextSearch.text.toString())
        }, year, month, day)

        val endPicker = DatePickerDialog(this, { _, y, m, d ->
            endDate = Calendar.getInstance().apply {
                set(y, m, d, 23, 59, 59)       // ← 23:59:59.000  (end-of-day)
                set(Calendar.MILLISECOND, 999)
            }
            binding.endDateButton.text =
                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(endDate!!.time)
            filterExpenses(binding.editTextSearch.text.toString())
        }, year, month, day)

        binding.startDateButton.setOnClickListener { startPicker.show() }
        binding.endDateButton.setOnClickListener { endPicker.show() }
    }

    // ─────────────────────────────────────────
    // Firestore fetch
    // ─────────────────────────────────────────
    private fun loadExpenses() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val expensesSnap = db.collection("expenses")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .get()
                    .await()

                val categorySnap = db.collection("categories").get().await()
                val categoryMap = categorySnap.documents.associateBy(
                    { it.id },
                    { it.toObject(CategoryEntity::class.java)?.name ?: "Uncategorised" }
                )

                allExpenses = expensesSnap.documents.mapNotNull { doc ->
                    doc.toObject(ExpenseEntity::class.java)?.apply {
                        id = doc.id
                        val catName = categoryMap[categoryId] ?: "Uncategorised"
                        expenseName = "$expenseName ($catName)"
                    }
                }

                runOnUiThread {
                    binding.editTextSearch.setText("")
                    startDate = null
                    endDate   = null
                    adapter.submitList(allExpenses)
                }
            } catch (e: Exception) {
                Log.e("EXPENSE_ERROR", "Failed to load: ${e.message}", e)
                runOnUiThread {
                    Toast.makeText(this@ManageExpensesActivity,
                        "Failed to load expenses", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // ─────────────────────────────────────────
    // Filtering (name + inclusive dates)
    // ─────────────────────────────────────────
    private fun filterExpenses(searchText: String) {
        val filtered = allExpenses.filter { exp ->
            val matchesSearch = exp.expenseName.contains(searchText, true) ||
                    exp.description.contains(searchText, true)

            val matchesDate = when {
                startDate != null && endDate != null -> {
                    val expDate = parseDate(exp.startDate) ?: return@filter false
                    !expDate.before(startDate) && !expDate.after(endDate)   // inclusive
                }
                startDate != null -> {
                    val expDate = parseDate(exp.startDate) ?: return@filter false
                    !expDate.before(startDate)                              // inclusive
                }
                endDate != null -> {
                    val expDate = parseDate(exp.startDate) ?: return@filter false
                    !expDate.after(endDate)                                 // inclusive
                }
                else -> true
            }

            matchesSearch && matchesDate
        }

        adapter.submitList(filtered)
        if (filtered.isEmpty()) {
            Toast.makeText(this, "No expenses match your filters", Toast.LENGTH_SHORT).show()
        }
    }

    // ─────────────────────────────────────────
    // String → Calendar helper
    // ─────────────────────────────────────────
    private fun parseDate(raw: String?): Calendar? {
        if (raw.isNullOrBlank()) return null

        val patterns = listOf("yyyy-MM-dd", "dd/MM/yyyy", "MM/dd/yyyy")
        val parsed: Date = patterns
            .map { SimpleDateFormat(it, Locale.getDefault()) }
            .firstNotNullOfOrNull { fmt -> runCatching { fmt.parse(raw) }.getOrNull() }
            ?: return null

        return Calendar.getInstance().apply {
            time = parsed
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
    }
}
