package com.dianca.budgettrackerapp

import android.content.Intent
import android.os.Bundle
import android.widget.*
import com.dianca.budgettrackerapp.data.CategoryEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddCategoryActivity : BaseActivity() {

    private lateinit var edtCategory: EditText
    private lateinit var btnAdd: Button
    private lateinit var btnGoToManage: Button
    private val categoryDAO = CategoryDAO()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_category)

        setupBottomNav()

        edtCategory = findViewById(R.id.edtCategoryName)
        btnAdd = findViewById(R.id.btnAddCategory)
        btnGoToManage = findViewById(R.id.btnGoToManage)

        btnAdd.setOnClickListener {
            val name = edtCategory.text.toString().trim()
            if (name.isNotEmpty()) {
                CoroutineScope(Dispatchers.Main).launch {
                    try {
                        val id = categoryDAO.insertCategory(CategoryEntity(name = name))
                        Toast.makeText(this@AddCategoryActivity, "Category added with ID: $id", Toast.LENGTH_SHORT).show()
                        edtCategory.setText("")
                    } catch (e: Exception) {
                        Toast.makeText(this@AddCategoryActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Please enter a category name", Toast.LENGTH_SHORT).show()
            }
        }

        btnGoToManage.setOnClickListener {
            startActivity(Intent(this, ManageCategoryActivity::class.java))
        }
    }
}
