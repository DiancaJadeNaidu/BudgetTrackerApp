package com.dianca.budgettrackerapp

import androidx.room.*

@Dao
interface ExpenseDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(expense: ExpenseEntity)

    @Query("SELECT * FROM expenses ORDER BY timestamp DESC")
    suspend fun getAll(): List<ExpenseEntity>

    @Query("SELECT * FROM expenses WHERE categoryId = :categoryId")
    suspend fun getByCategory(categoryId: Int): List<ExpenseEntity>

    @Query("SELECT SUM(amount) FROM expenses WHERE categoryId = :categoryId")
    suspend fun getTotalByCategory(categoryId: Int): Double
}
