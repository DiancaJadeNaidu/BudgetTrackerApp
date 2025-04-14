package com.dianca.budgettrackerapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val expenseName: String,
    val categoryId: Int,
    val amount: Double,
    val description: String,
    val timestamp: Long,
    val startDate: String,
    val endDate: String,
    val timePeriod: String,
    val imagePath: String? = null
)
