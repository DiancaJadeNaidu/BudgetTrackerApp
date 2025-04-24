package com.dianca.budgettrackerapp

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dianca.budgettrackerapp.databinding.ItemExpenseBinding

//adapter for displaying a list of expenses
class ExpenseListAdapter(
    private val onDeleteClick: (ExpenseEntity) -> Unit
) : RecyclerView.Adapter<ExpenseListAdapter.ExpenseViewHolder>() {

    //holds the list of expenses
    private var expenses: List<ExpenseEntity> = emptyList()

    //updates the list of expenses and notifies adapter
    fun submitList(newList: List<ExpenseEntity>) {
        expenses = newList
        notifyDataSetChanged()
    }

    //creates a view holder for each expense
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val binding = ItemExpenseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExpenseViewHolder(binding)
    }
    //returns the size of the expense list
    override fun getItemCount() = expenses.size

    //binds data to the view holder
    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        holder.bind(expenses[position])
    }
    //view holder for each expense
    inner class ExpenseViewHolder(private val binding: ItemExpenseBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(expense: ExpenseEntity) {
            Log.d("ExpenseAdapter", "Binding expense: ${expense.expenseName}")
            Log.d("ExpenseAdapter", "Image path: ${expense.imagePath}")

            //set the expense data to the views
            binding.txtExpenseName.text = expense.expenseName
            binding.txtExpenseAmount.text = "Amount: R${expense.amount}"
            binding.txtExpensePeriod.text = "Period: ${expense.startDate} - ${expense.endDate}"
            binding.txtDescription.text = "Description: ${expense.description}"

            //load image if available
            if (!expense.imagePath.isNullOrEmpty()) {
                val uri = Uri.parse(expense.imagePath)
                Log.d("ExpenseAdapter", "Parsed URI: $uri")

                Glide.with(binding.root.context)
                    .load(uri)
                    .into(binding.imgExpensePreview)

            } else {
                Log.d("ExpenseAdapter", "No image path found")

                Glide.with(binding.root.context)
            }

            //set up delete button click listener
            binding.btnDelete.setOnClickListener { onDeleteClick(expense) }
        }
    }
}
