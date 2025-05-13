package com.dianca.budgettrackerapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

//represents a category in the database
@Entity(tableName = "categories")
data class CategoryEntity(
    //auto-generates ID
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String
)