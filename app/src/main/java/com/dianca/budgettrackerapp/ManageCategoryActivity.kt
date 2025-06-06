package com.dianca.budgettrackerapp

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dianca.budgettrackerapp.data.CategoryEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ManageCategoryActivity : BaseActivity() {

    private lateinit var categoryDao: CategoryDAO
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CategoryAdapter
    private val categoryList = mutableListOf<CategoryEntity>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_category)
        categoryDao = CategoryDAO()

        //setup bottom navigation bar
        setupBottomNav()
        setupUI()
        loadCategories()
    }

    private fun setupUI() {
        recyclerView = findViewById(R.id.recyclerViewCategories)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = CategoryAdapter(categoryList)
        recyclerView.adapter = adapter

        //short click -> show a Toast (for now)
        adapter.setOnItemClickListener { category ->
            Toast.makeText(this, "Clicked: ${category.name}", Toast.LENGTH_SHORT).show()
        }

        //long click -> show edit/delete options
        adapter.setOnItemLongClickListener { category ->
            showOptionsDialog(category)
        }

        //button to navigate to SummaryActivity
        findViewById<Button>(R.id.btnSummary).setOnClickListener {
            startActivity(Intent(this, SummaryActivity::class.java))
        }

        //reset/delete all categories
        findViewById<Button>(R.id.btnResetAll).setOnClickListener {
            showResetConfirmationDialog()
        }

        //button to go back to AddCategory screen
        findViewById<Button>(R.id.btnBackToAdd).setOnClickListener {
            startActivity(Intent(this, AddCategoryActivity::class.java))
            finish()
        }

    }

    private fun loadCategories() {
        //get all categories from firestore
        val db = FirebaseFirestore.getInstance()

        db.collection("categories")
            .get()
            .addOnSuccessListener { result ->
                categoryList.clear()
                for (document in result) {
                    val name = document.getString("name") ?: ""
                    val category = CategoryEntity(id = document.id, name = name)
                    categoryList.add(category)

                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    this@ManageCategoryActivity,
                    "Error loading categories: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    //dialog to choose between editing or deleting a category
    private fun showOptionsDialog(category: CategoryEntity) {
        AlertDialog.Builder(this)
            .setTitle("Manage Category")
            .setItems(arrayOf("Edit", "Delete")) { _, which ->
                when (which) {
                    0 -> showEditDialog(category)
                    1 -> deleteCategory(category)
                }
            }
            .show()
    }

    //dialog to edit a category's name
    private fun showEditDialog(category: CategoryEntity) {
        val input = EditText(this).apply {
            setText(category.name)
        }

        AlertDialog.Builder(this)
            .setTitle("Edit Category")
            .setView(input)
            .setPositiveButton("Save") { _, _ ->
                val newName = input.text.toString().trim()
                if (newName.isNotEmpty()) {
                    updateCategory(category.copy(name = newName))
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    //method to confirm categories deletion
    private fun showResetConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Confirm Reset")
            .setMessage("Are you sure you want to delete all categories?")
            .setPositiveButton("Yes") { _, _ ->
                deleteAllCategories()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    //method to update category new name
    private fun updateCategory(category: CategoryEntity) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                categoryDao.updateCategory(category)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ManageCategoryActivity, "Category updated", Toast.LENGTH_SHORT).show()
                    //refresh after update
                    loadCategories()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ManageCategoryActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    //method to delete a specific category
    private fun deleteCategory(category: CategoryEntity) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                categoryDao.deleteCategory(category.id)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ManageCategoryActivity, "Category deleted", Toast.LENGTH_SHORT).show()
                    //refresh after delete
                    loadCategories()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ManageCategoryActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    //method to delete all categories
    private fun deleteAllCategories() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                categoryDao.deleteAllCategories()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ManageCategoryActivity, "All categories deleted", Toast.LENGTH_SHORT).show()
                    //refresh after delete all
                    loadCategories()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ManageCategoryActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}