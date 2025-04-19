package com.dianca.budgettrackerapp

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch

class ViewExpensesActivity : BaseActivity() {

    private lateinit var lvExpenses: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_expenses)

        setupBottomNav()

        lvExpenses = findViewById(R.id.lvExpenses)

        // Load expenses
        loadExpenses()
    }

    private fun loadExpenses() {
        lifecycleScope.launch {
            // Fetch all expenses from the database
            val expenses = AppDatabase.getInstance(applicationContext).expenseDao().getAll()

            // Set up an adapter to display the expenses
            val adapter = ExpenseAdapter(expenses)
            lvExpenses.adapter = adapter
        }
    }

    inner class ExpenseAdapter(private val expenses: List<ExpenseEntity>) :
        ArrayAdapter<ExpenseEntity>(this, R.layout.expense_item, expenses) {

        override fun getView(
            position: Int,
            convertView: android.view.View?,
            parent: android.view.ViewGroup
        ): android.view.View {
            val view = convertView ?: LayoutInflater.from(context)
                .inflate(R.layout.expense_item, parent, false)

            val expense = expenses[position]

            val tvExpenseName = view.findViewById<TextView>(R.id.tvExpenseName)
            val tvCategory = view.findViewById<TextView>(R.id.tvCategory)
            val tvAmount = view.findViewById<TextView>(R.id.tvAmount)
            val tvDateRange = view.findViewById<TextView>(R.id.tvDateRange)
            val ivExpenseImage = view.findViewById<ImageView>(R.id.ivExpenseImage)

            // Set expense details
            tvExpenseName.text = expense.expenseName
            tvCategory.text =
                "Category: ${expense.categoryId}" // Assuming categoryId can be replaced by actual category name
            tvAmount.text = "Amount: R${expense.amount}"
            tvDateRange.text = "${expense.startDate} - ${expense.endDate}"

            // Load image if available
            if (!expense.imagePath.isNullOrEmpty()) {
                val uri = Uri.parse(expense.imagePath)
                Log.d("ExpenseAdapter", "Parsed URI: $uri")

                Glide.with(context)
                    .load(uri) // load the actual image
                    .into(ivExpenseImage)

                ivExpenseImage.visibility =
                    android.view.View.VISIBLE  // Make the ImageView visible when you have an image
            } else {
                Log.d("ExpenseAdapter", "No image path found")

                Glide.with(context)
                    .load(R.drawable.ic_placeholder_image) // Load the placeholder image
                    .into(ivExpenseImage)

                ivExpenseImage.visibility =
                    android.view.View.VISIBLE  // Make the ImageView visible, but with the placeholder image
            }

            return view
        }
    }
}
