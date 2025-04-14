package com.dianca.budgettrackerapp.data

import androidx.room.*

@Dao
interface CategoryDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: CategoryEntity)

    @Query("SELECT * FROM categories")
    suspend fun getAll(): List<CategoryEntity>

    @Delete
    suspend fun delete(category: CategoryEntity)
}
