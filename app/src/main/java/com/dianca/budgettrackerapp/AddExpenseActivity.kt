package com.dianca.budgettrackerapp

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dianca.budgettrackerapp.databinding.ActivityAddexpenseBinding
import kotlinx.coroutines.launch
import java.util.*

class AddExpenseActivity : BaseActivity() {

    private lateinit var binding: ActivityAddexpenseBinding
    private var selectedImageUri: Uri? = null
    private val PICK_IMAGE_REQUEST = 1001
    private var selectedCategoryId: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddexpenseBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_addexpense)

        setupBottomNav()

        setupDatePickers()
        setupCategorySpinner()
        setupUploadPhoto()
        setupSaveButton()



    }

    private fun setupDatePickers() {
        val calendar = Calendar.getInstance()

        val showDatePicker: (EditText) -> Unit = { editText ->
            DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    editText.setText(String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        binding.edtExpenseDate.setOnClickListener { showDatePicker(binding.edtExpenseDate) }
        binding.edtStartDate.setOnClickListener { showDatePicker(binding.edtStartDate) }
        binding.edtEndDate.setOnClickListener { showDatePicker(binding.edtEndDate) }
    }

    private fun setupCategorySpinner() {
        lifecycleScope.launch {
            val categories = AppDatabase.getInstance(applicationContext)
                .categoryDao()
                .getAll()

            if (categories.isEmpty()) {
                Toast.makeText(this@AddExpenseActivity, "No categories found", Toast.LENGTH_SHORT)
                    .show()
                return@launch
            }

            val adapter = ArrayAdapter(
                this@AddExpenseActivity,
                android.R.layout.simple_spinner_item,
                categories.map { it.name }
            ).also {
                it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }

            binding.spinnerCategory.adapter = adapter

            binding.spinnerCategory.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: android.view.View?,
                        position: Int,
                        id: Long
                    ) {
                        selectedCategoryId = categories[position].id
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {}
                }
        }
    }

    private fun setupUploadPhoto() {
        binding.btnUploadPhoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            selectedImageUri = data?.data
            binding.imagePreview.setImageURI(selectedImageUri)
        }
    }

    private fun setupSaveButton() {
        binding.btnSaveExpense.setOnClickListener {
            val description = binding.edtExpenseDescription.text.toString().trim()
            val expenseName = binding.edtExpenseName.text.toString().trim()
            val amount = binding.edtExpenseAmount.text.toString().toDoubleOrNull()
            val dateStr = binding.edtExpenseDate.text.toString().trim()
            val startDateStr = binding.edtStartDate.text.toString().trim()
            val endDateStr = binding.edtEndDate.text.toString().trim()
            val timePeriod = binding.spinnerTimePeriod.selectedItem.toString()

            if (description.isEmpty() || expenseName.isEmpty() || amount == null || dateStr.isEmpty() || startDateStr.isEmpty() || endDateStr.isEmpty()) {
                Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            val expense = ExpenseEntity(
                categoryId = selectedCategoryId,
                amount = amount,
                description = description,
                timestamp = System.currentTimeMillis(),
                startDate = startDateStr,
                endDate = endDateStr,
                expenseName = expenseName,
                timePeriod = timePeriod,
                imagePath = selectedImageUri?.toString()
            )

            lifecycleScope.launch {
                AppDatabase.getInstance(applicationContext)
                    .expenseDao()
                    .insert(expense)
                Toast.makeText(this@AddExpenseActivity, "Expense saved!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        binding.btnManageExpenses.setOnClickListener {
            val intent = Intent(this, ManageExpensesActivity::class.java)
            startActivity(intent)
        }
    }
}
