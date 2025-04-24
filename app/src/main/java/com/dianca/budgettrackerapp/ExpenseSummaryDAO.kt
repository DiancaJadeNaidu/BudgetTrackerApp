package com.dianca.budgettrackerapp

import androidx.room.Dao
import androidx.room.Query

@Dao
interface ExpenseSummaryDAO {

    //query to get the total amount spent in a category within a specific time range
    @Query("""
        SELECT SUM(amount) FROM expenses 
        WHERE categoryId = :categoryId 
        AND timestamp BETWEEN :startTime AND :endTime
    """)
    suspend fun getTotalByCategoryWithinPeriod(
        //the category of expenses
        categoryId: Int,
        //start timestamp for the period
        startTime: Long,
        //end timestamp for the period
        endTime: Long
    ): Double
}
