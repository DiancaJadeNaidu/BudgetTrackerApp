package com.dianca.budgettrackerapp.data

data class ExpenseEntity(
    var id: String = "",
    var expenseName: String = "",
    var categoryId: String = "",
    var amount: Double = 0.0,
    var description: String = "",
    var timestamp: Long = 0,
    var startDate: String = "",
    var endDate: String = "",
    var timePeriod: String = "",
    var imagePath: String? = null
)
