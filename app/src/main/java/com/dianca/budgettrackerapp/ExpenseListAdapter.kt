package com.dianca.budgettrackerapp

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dianca.budgettrackerapp.data.ExpenseEntity
import com.dianca.budgettrackerapp.databinding.ItemExpenseBinding
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.provider.MediaStore
import android.util.Base64
import java.io.ByteArrayOutputStream

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

        //log the IDs to verify they're being properly passed
        Log.d("ExpenseAdapter", "Binding expense ID: ${expense.id}")
        Log.d("ExpenseAdapter", "Category ID: ${expense.categoryId}")
    }

    inner class ExpenseViewHolder(private val binding: ItemExpenseBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(expense: ExpenseEntity) {
            with(binding) {
                //set basic expense information
                txtExpenseName.text = expense.expenseName
                txtExpenseAmount.text = "Amount: R${expense.amount}"
                txtExpensePeriod.text = "Period: ${expense.startDate} - ${expense.endDate}"
                txtDescription.text = expense.description

                //handle image loading - to firestore
                if (!expense.imagePath.isNullOrEmpty()) {
                    try {
                        val decodedBytes = Base64.decode(expense.imagePath, Base64.DEFAULT)
                        val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                        imgExpensePreview.setImageBitmap(bitmap)
                    } catch (e: Exception) {
                        Log.e("ExpenseAdapter", "Error decoding image", e)
                        imgExpensePreview.setImageResource(android.R.color.transparent)
                    }
                }
                else {
                    imgExpensePreview.setImageResource(android.R.color.transparent)
                }

                //set up delete button
                btnDelete.setOnClickListener { onDeleteClick(expense) }
            }
        }
    }
}
