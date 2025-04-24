package com.dianca.budgettrackerapp

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.dianca.budgettrackerapp.data.CategoryEntity


class AddCategoryActivity : BaseActivity() {

    //declare UI components and DAO
    private lateinit var categoryDAO: CategoryDAO
    private lateinit var edtCategory: EditText
    private lateinit var btnAdd: Button
    private lateinit var btnGoToManage: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_category)

        //set up bottom navigation from base activity
        setupBottomNav()

        //link UI components with layout views
        edtCategory = findViewById(R.id.edtCategoryName)
        btnAdd = findViewById(R.id.btnAddCategory)
        btnGoToManage = findViewById(R.id.btnGoToManage)

        //initialize database and DAO
        val db = AppDatabase.getInstance(this)
        categoryDAO = db.categoryDao()

        //handle add category button click
        btnAdd.setOnClickListener {
            val name = edtCategory.text.toString().trim()
            if (name.isNotEmpty()) {
                lifecycleScope.launch {
                    try {
                        //insert new category into database
                        println("Inserting category: $name")
                        categoryDAO.insertCategory(CategoryEntity(name = name))
                        runOnUiThread {
                            Toast.makeText(this@AddCategoryActivity, "Category added!", Toast.LENGTH_SHORT).show()
                            edtCategory.setText("")
                        }
                    } catch (e: Exception) {

                        e.printStackTrace()
                        runOnUiThread {
                            Toast.makeText(this@AddCategoryActivity, "Error adding category: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                //display message if input is empty
                Toast.makeText(this, "Please enter a category name", Toast.LENGTH_SHORT).show()
            }
        }

        //redirect to manage category screen
        btnGoToManage.setOnClickListener {
            startActivity(Intent(this, ManageCategoryActivity::class.java))
        }
    }
}
