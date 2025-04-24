package com.dianca.budgettrackerapp

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dianca.budgettrackerapp.BaseActivity
import com.dianca.budgettrackerapp.data.CategoryEntity
import kotlinx.coroutines.launch

class ManageCategoryActivity : BaseActivity() {

    private lateinit var categoryDao: CategoryDAO
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CategoryAdapter
    private val categoryList = mutableListOf<CategoryEntity>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_category)

        //setup bottom navigation bar
        setupBottomNav()

        //button to navigate to SummaryActivity
        val btnSummary: Button = findViewById(R.id.btnSummary)
        btnSummary.setOnClickListener {
            val intent = Intent(this, SummaryActivity::class.java)
            startActivity(intent)
        }

        //UI elements
        recyclerView = findViewById(R.id.recyclerViewCategories)
        recyclerView.layoutManager = LinearLayoutManager(this)

        //database setup
        val db = AppDatabase.getInstance(this)
        categoryDao = db.categoryDao()

        //adapter setup
        adapter = CategoryAdapter(categoryList)
        recyclerView.adapter = adapter

        //observe categories and update RecyclerView
        categoryDao.getAllCategories().observe(this, Observer { categories ->
            categoryList.clear()
            categoryList.addAll(categories)
            adapter.notifyDataSetChanged()
        })

        //short click -> show a Toast (for now)
        recyclerView.addOnItemTouchListener(object : RecyclerView.SimpleOnItemTouchListener() {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                val view = rv.findChildViewUnder(e.x, e.y)
                val position = rv.getChildAdapterPosition(view!!)
                val selected = categoryList[position]
                Toast.makeText(this@ManageCategoryActivity, "Clicked: ${selected.name}", Toast.LENGTH_SHORT).show()
                return super.onInterceptTouchEvent(rv, e)
            }
        })

        //long click -> show edit/delete options
        recyclerView.addOnItemTouchListener(object : RecyclerView.SimpleOnItemTouchListener() {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                val view = rv.findChildViewUnder(e.x, e.y)
                val position = rv.getChildAdapterPosition(view!!)
                val selected = categoryList[position]
                showOptionsDialog(selected)
                return true
            }
        })

        //reset/delete all categories
        val btnReset: Button = findViewById(R.id.btnResetAll)
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

        //button to go back to AddCategory screen
        val btnBack: Button = findViewById(R.id.btnBackToAdd)
        btnBack.setOnClickListener {
            val intent = Intent(this, AddCategoryActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    //dialog to choose between editing or deleting a category
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

    //dialog to edit a category's name
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