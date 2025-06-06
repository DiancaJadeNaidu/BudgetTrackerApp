package com.dianca.budgettrackerapp

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import com.dianca.budgettrackerapp.data.CategoryEntity
import com.dianca.budgettrackerapp.data.ExpenseEntity
import com.dianca.budgettrackerapp.databinding.ActivityAddexpenseBinding
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*
import android.graphics.Bitmap
import android.provider.MediaStore
import android.util.Base64
import java.io.ByteArrayOutputStream


class AddExpenseActivity : BaseActivity() {

    private lateinit var binding: ActivityAddexpenseBinding
    private var selectedImageUri: Uri? = null
    private val PICK_IMAGE_REQUEST = 1001
    private var selectedCategoryId: String = ""
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddexpenseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomNav()
        setupDatePickers()
        setupCategorySpinner()
        setupUploadPhoto()
        setupSaveButton()
    }

    private fun setupDatePickers() {
        val calendar = Calendar.getInstance()
        val showPicker: (EditText) -> Unit = { editText ->
            DatePickerDialog(this, { _, year, month, day ->
                editText.setText("%04d-%02d-%02d".format(year, month + 1, day))
            },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        binding.edtStartDate.setOnClickListener { showPicker(binding.edtStartDate) }
        binding.edtEndDate.setOnClickListener { showPicker(binding.edtEndDate) }
        binding.edtExpenseDate.setOnClickListener { showPicker(binding.edtExpenseDate) }
    }

    private fun setupCategorySpinner() {
        CoroutineScope(Dispatchers.Main).launch {
            val snapshot = db.collection("categories").get().await()
            val categories = snapshot.documents.map {
                CategoryEntity(id = it.id, name = it.getString("name") ?: "")
            }

            val adapter = ArrayAdapter(this@AddExpenseActivity, android.R.layout.simple_spinner_item, categories.map { it.name })
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerCategory.adapter = adapter

            binding.spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                    selectedCategoryId = categories[position].id
                }
                override fun onNothingSelected(parent: AdapterView<*>) {}
            }
        }
    }

    private fun setupUploadPhoto() {
        binding.btnUploadPhoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply { type = "image/*" }
            startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            binding.imagePreview.setImageURI(selectedImageUri)
        }
    }

    private fun setupSaveButton() {
        binding.btnSaveExpense.setOnClickListener {
            val name = binding.edtExpenseName.text.toString().trim()
            val description = binding.edtExpenseDescription.text.toString().trim()
            val amount = binding.edtExpenseAmount.text.toString().toDoubleOrNull()
            val startDate = binding.edtStartDate.text.toString()
            val endDate = binding.edtEndDate.text.toString()
            val timePeriod = binding.spinnerTimePeriod.selectedItem.toString()

            val imageBase64 = selectedImageUri?.let {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, it)
                val outputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                val byteArray = outputStream.toByteArray()
                Base64.encodeToString(byteArray, Base64.DEFAULT)
            }


            if (name.isEmpty() || amount == null || startDate.isEmpty() || endDate.isEmpty() || description.isEmpty()) {
                Toast.makeText(this, "Please complete all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            CoroutineScope(Dispatchers.Main).launch {
                val docRef = db.collection("expenses").document()
                val expense = ExpenseEntity(
                    id = docRef.id,
                    expenseName = name,
                    categoryId = selectedCategoryId,
                    amount = amount,
                    description = description,
                    timestamp = System.currentTimeMillis(),
                    startDate = startDate,
                    endDate = endDate,
                    timePeriod = timePeriod,
                    imagePath = imageBase64
                )
                docRef.set(expense).await()
                Toast.makeText(this@AddExpenseActivity, "Expense saved!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        binding.btnManageExpenses.setOnClickListener {
            startActivity(Intent(this, ManageExpensesActivity::class.java))
        }
    }
}
