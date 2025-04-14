package com.dianca.budgettrackerapp

import androidx.room.*
import com.dianca.budgettrackerapp.data.CategoryEntity

@Dao
interface CategoryDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: CategoryEntity)

    @Query("SELECT * FROM categories")
    suspend fun getAll(): List<CategoryEntity>

    @Delete
    suspend fun delete(category: CategoryEntity)
}
