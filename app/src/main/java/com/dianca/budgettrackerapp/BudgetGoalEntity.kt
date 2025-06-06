package com.dianca.budgettrackerapp.data

data class BudgetGoalEntity(
    //budget goal entry attributes
    val id: String = "",
    val categoryId: String = "",
    val minGoalAmount: Double = 0.0,
    val maxGoalAmount: Double = 0.0,
    val month: String = ""
)
