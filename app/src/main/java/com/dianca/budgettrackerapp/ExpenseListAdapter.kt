package com.dianca.budgettrackerapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dianca.budgettrackerapp.databinding.ItemExpenseBinding

class ExpenseListAdapter(
    private val onEditClick: (ExpenseEntity) -> Unit,
    private val onDeleteClick: (ExpenseEntity) -> Unit
) : RecyclerView.Adapter<ExpenseListAdapter.ExpenseViewHolder>() {

    private var expenses: List<ExpenseEntity> = emptyList()

    fun submitList(newList: List<ExpenseEntity>) {
        expenses = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val binding = ItemExpenseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExpenseViewHolder(binding)
    }

    override fun getItemCount() = expenses.size

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        holder.bind(expenses[position])
    }

    inner class ExpenseViewHolder(private val binding: ItemExpenseBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(expense: ExpenseEntity) {
            binding.txtExpenseName.text = expense.expenseName
            binding.txtExpenseAmount.text = "Amount: R${expense.amount}"
            binding.txtExpensePeriod.text = "Period: ${expense.startDate} - ${expense.endDate}"
            binding.txtCategoryId.text = "Category: ${expense.categoryId}"

            binding.btnDelete.setOnClickListener { onDeleteClick(expense) }
            binding.btnEdit.setOnClickListener { onEditClick(expense) }
        }
    }
}
