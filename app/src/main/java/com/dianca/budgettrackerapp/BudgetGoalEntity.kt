package com.dianca.budgettrackerapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "budget_goals")
data class BudgetGoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val categoryId: Int, // foreign key to CategoryEntity
    val goalAmount: Double,
    val month: String // e.g. "April 2025"
)
