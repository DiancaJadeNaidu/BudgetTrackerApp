package com.dianca.budgettrackerapp.data

import androidx.room.*

@Dao
interface BudgetGoalDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(goal: BudgetGoalEntity)

    @Query("SELECT * FROM budget_goals WHERE month = :month")
    suspend fun getGoalsForMonth(month: String): List<BudgetGoalEntity>
}
