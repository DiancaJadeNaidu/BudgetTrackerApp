package com.dianca.budgettrackerapp

import androidx.room.Entity
import androidx.room.PrimaryKey

//entity for expense records
@Entity(tableName = "expenses")
data class ExpenseEntity(
    //expense attributes
    //auto-generates ID
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
