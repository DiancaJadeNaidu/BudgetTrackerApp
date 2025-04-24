package com.dianca.budgettrackerapp

import androidx.lifecycle.LiveData
import androidx.room.*
import com.dianca.budgettrackerapp.data.CategoryEntity

@Dao
interface CategoryDAO {

    //insert or update a category
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity)

    //delete a specific category
    @Delete
    suspend fun deleteCategory(category: CategoryEntity)

    //get all categories in alphabetical order
    @Query("SELECT * FROM categories ORDER BY name ASC")
    fun getAllCategories(): LiveData<List<CategoryEntity>>

    //delete all categories from the table
    @Query("DELETE FROM categories")
    suspend fun deleteAllCategories()

    @Query("SELECT * FROM categories ORDER BY name ASC")
    suspend fun getAll(): List<CategoryEntity>

}

