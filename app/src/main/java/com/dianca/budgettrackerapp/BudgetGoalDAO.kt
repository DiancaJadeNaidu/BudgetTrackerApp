package com.dianca.budgettrackerapp.data

import androidx.room.*

//DAO for accessing budget goals in the database
@Dao
interface BudgetGoalDAO {
    //insert or update a budget goal
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(goal: BudgetGoalEntity)

    //get all budget goals for a specific month
    @Query("SELECT * FROM budget_goals WHERE month = :month")
    suspend fun getGoalsForMonth(month: String): List<BudgetGoalEntity>
}
