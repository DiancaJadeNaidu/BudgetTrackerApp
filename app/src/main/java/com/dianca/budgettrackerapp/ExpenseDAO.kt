package com.dianca.budgettrackerapp

import androidx.room.*

//DAO for handling expense related DB operations
@Dao
interface ExpenseDAO {

    //insert or replace expense
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(expense: ExpenseEntity)

    //get all expenses
    @Query("SELECT * FROM expenses ORDER BY timestamp DESC")
    suspend fun getAll(): List<ExpenseEntity>

    //get expenses by category ID
    @Query("SELECT * FROM expenses WHERE categoryId = :categoryId")
    suspend fun getExpensesByCategoryId(categoryId: Int): List<ExpenseEntity>

    //another method to get by category
    @Query("SELECT * FROM expenses WHERE categoryId = :categoryId")
    suspend fun getByCategory(categoryId: Int): List<ExpenseEntity>

    //get total amount spent for a category
    @Query("SELECT SUM(amount) FROM expenses WHERE categoryId = :categoryId")
    suspend fun getTotalByCategory(categoryId: Int): Double

    //delete an expense
    @Delete
    suspend fun delete(expense: ExpenseEntity)
}
