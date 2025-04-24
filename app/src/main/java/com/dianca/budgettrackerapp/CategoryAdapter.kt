package com.dianca.budgettrackerapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dianca.budgettrackerapp.data.CategoryEntity
import com.dianca.budgettrackerapp.databinding.ItemCategoryBinding

//adapter for displaying categories in RecyclerView
class CategoryAdapter(private val categories: List<CategoryEntity>) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    //creates view holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    //returns list size
    override fun getItemCount() = categories.size

    //binds data to view
    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.bind(category)
    }

    //ViewHolder for category item
    inner class CategoryViewHolder(private val binding: ItemCategoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(category: CategoryEntity) {
            //set name text
            binding.txtCategoryName.text = category.name
        }
    }
}
