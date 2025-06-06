package com.dianca.budgettrackerapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dianca.budgettrackerapp.data.CategoryEntity

class CategoryAdapter(
    private val categoryList: List<CategoryEntity>
) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    private var onItemClick: ((CategoryEntity) -> Unit)? = null
    private var onItemLongClick: ((CategoryEntity) -> Unit)? = null

    fun setOnItemClickListener(listener: (CategoryEntity) -> Unit) {
        onItemClick = listener
    }

    fun setOnItemLongClickListener(listener: (CategoryEntity) -> Unit) {
        onItemLongClick = listener
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryName: TextView = itemView.findViewById(R.id.txtCategoryName)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick?.invoke(categoryList[position])
                }
            }

            itemView.setOnLongClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemLongClick?.invoke(categoryList[position])
                    true
                } else {
                    false
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = categoryList[position]
        holder.categoryName.text = category.name
    }

    override fun getItemCount(): Int = categoryList.size
}
