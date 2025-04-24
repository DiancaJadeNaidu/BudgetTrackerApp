package com.dianca.budgettrackerapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

//represents a budget goal entry in the database
@Entity(tableName = "budget_goals")
data class BudgetGoalEntity(
    //budget goal entry attributes
    //auto-generates ID
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val categoryId: Int,
    val minGoalAmount: Double,
    val maxGoalAmount: Double,
    val month: String
)
