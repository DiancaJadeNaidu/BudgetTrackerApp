package com.dianca.budgettrackerapp

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dianca.budgettrackerapp.data.BudgetGoalDAO
import com.dianca.budgettrackerapp.data.BudgetGoalEntity
import com.dianca.budgettrackerapp.CategoryDAO
import com.dianca.budgettrackerapp.data.CategoryEntity

//define room database and list all entities (tables)
@Database(
    entities = [CategoryEntity::class, BudgetGoalEntity::class, ExpenseEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    //DAO for category table, expense table, budget goal table, summarized expense data
    abstract fun categoryDao(): CategoryDAO
    abstract fun expenseDao(): ExpenseDAO
    abstract fun budgetGoalDAO(): BudgetGoalDAO
    abstract fun expenseSummaryDao(): ExpenseSummaryDAO

    companion object {
        //singleton instance -> prevents multiple database instances
        @Volatile
        private var INSTANCE: AppDatabase? = null

        //get the singleton database instance
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                //build database if it doesn't exist
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "budget_tracker_db"
                )
                    //rebuild database on schema changes
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
