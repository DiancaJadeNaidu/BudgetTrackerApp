package com.dianca.budgettrackerapp

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dianca.budgettrackerapp.data.ExpenseEntity
import com.dianca.budgettrackerapp.databinding.ItemExpenseBinding

class ExpenseListAdapter(
    private val onDeleteClick: (ExpenseEntity) -> Unit
) : RecyclerView.Adapter<ExpenseListAdapter.ExpenseViewHolder>() {

    private var expenses: List<ExpenseEntity> = emptyList()

    fun submitList(newList: List<ExpenseEntity>) {
        expenses = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val binding = ItemExpenseBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ExpenseViewHolder(binding)
    }

    override fun getItemCount(): Int = expenses.size

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = expenses[position]
        holder.bind(expense)

        // Log the IDs to verify they're being properly passed
        Log.d("ExpenseAdapter", "Binding expense ID: ${expense.id}")
        Log.d("ExpenseAdapter", "Category ID: ${expense.categoryId}")
    }

    inner class ExpenseViewHolder(private val binding: ItemExpenseBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(expense: ExpenseEntity) {
            with(binding) {
                // Set basic expense information
                txtExpenseName.text = expense.expenseName
                txtExpenseAmount.text = "Amount: R${expense.amount}"
                txtExpensePeriod.text = "Period: ${expense.startDate} - ${expense.endDate}"
                txtDescription.text = expense.description

                // Handle image loading
                if (!expense.imagePath.isNullOrEmpty()) {
                    try {
                        val uri = Uri.parse(expense.imagePath)
                        Glide.with(root.context)
                            .load(uri)
                            .into(imgExpensePreview)
                    } catch (e: Exception) {
                        Log.e("ExpenseAdapter", "Error loading image", e)
                        imgExpensePreview.setImageResource(android.R.color.transparent)
                    }
                } else {
                    imgExpensePreview.setImageResource(android.R.color.transparent)
                }

                // Set up delete button
                btnDelete.setOnClickListener { onDeleteClick(expense) }
            }
        }
    }
}