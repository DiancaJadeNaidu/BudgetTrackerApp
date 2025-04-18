package com.dianca.budgettrackerapp

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dianca.budgettrackerapp.data.BudgetGoalDAO
import com.dianca.budgettrackerapp.data.BudgetGoalEntity
import com.dianca.budgettrackerapp.CategoryDAO
import com.dianca.budgettrackerapp.data.CategoryEntity

@Database(
    entities = [CategoryEntity::class, BudgetGoalEntity::class, ExpenseEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun categoryDao(): CategoryDAO
    abstract fun expenseDao(): ExpenseDAO
    abstract fun budgetGoalDAO(): BudgetGoalDAO

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "budget_tracker_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
