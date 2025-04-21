package com.dianca.budgettrackerapp

import androidx.room.Dao
import androidx.room.Query

@Dao
interface ExpenseSummaryDAO {

    @Query("""
        SELECT SUM(amount) FROM expenses 
        WHERE categoryId = :categoryId 
        AND timestamp BETWEEN :startTime AND :endTime
    """)
    suspend fun getTotalByCategoryWithinPeriod(
        categoryId: Int,
        startTime: Long,
        endTime: Long
    ): Double
}
