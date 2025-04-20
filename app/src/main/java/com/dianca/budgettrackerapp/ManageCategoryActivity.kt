package com.dianca.budgettrackerapp

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.dianca.budgettrackerapp.data.CategoryEntity
import kotlinx.coroutines.launch

class ManageCategoryActivity : BaseActivity() {

    private lateinit var categoryDao: CategoryDAO
    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var btnReset: Button
    private lateinit var btnBack: Button
    private val categoryList = mutableListOf<CategoryEntity>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_category)

        setupBottomNav()
        // UI elements
        listView = findViewById(R.id.lvCategories)
        btnReset = findViewById(R.id.btnResetAll)
        btnBack = findViewById(R.id.btnBackToAdd)

        // Adapter setup
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, mutableListOf())
        listView.adapter = adapter

        // Database setup
        val db = AppDatabase.getInstance(this)
        categoryDao = db.categoryDao()

        // Observe categories and update ListView
        categoryDao.getAllCategories().observe(this, Observer { categories ->
            categoryList.clear()
            categoryList.addAll(categories)
            adapter.clear()
            adapter.addAll(categories.map { it.name })
            adapter.notifyDataSetChanged()
        })

        // Short click: show a Toast (for now)
        listView.setOnItemClickListener { _, _, position, _ ->
            val selected = categoryList[position]
            Toast.makeText(this, "Clicked: ${selected.name}", Toast.LENGTH_SHORT).show()

            // 🔜 Later: Navigate to expenses for this category
        }

        // Long click: show edit/delete options
        listView.setOnItemLongClickListener { _, _, position, _ ->
            val selected = categoryList[position]
            showOptionsDialog(selected)
            true
        }

        // Reset all categories
        btnReset.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Confirm Reset")
                .setMessage("Are you sure you want to delete all categories?")
                .setPositiveButton("Yes") { _, _ ->
                    lifecycleScope.launch {
                        categoryDao.deleteAllCategories()
                        runOnUiThread {
                            Toast.makeText(this@ManageCategoryActivity, "All categories deleted", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        // Back to AddCategory screen
        btnBack.setOnClickListener {
            val intent = Intent(this, AddCategoryActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun showOptionsDialog(category: CategoryEntity) {
        val options = arrayOf("Edit", "Delete")
        AlertDialog.Builder(this)
            .setTitle("Manage Category")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> showEditDialog(category)
                    1 -> lifecycleScope.launch {
                        categoryDao.deleteCategory(category)
                    }
                }
            }
            .show()
    }

    private fun showEditDialog(category: CategoryEntity) {
        val input = EditText(this)
        input.setText(category.name)

        AlertDialog.Builder(this)
            .setTitle("Edit Category")
            .setView(input)
            .setPositiveButton("Save") { _, _ ->
                val newName = input.text.toString().trim()
                if (newName.isNotEmpty()) {
                    lifecycleScope.launch {
                        categoryDao.insertCategory(category.copy(name = newName))
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
