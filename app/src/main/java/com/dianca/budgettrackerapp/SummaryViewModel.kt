package com.dianca.budgettrackerapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.room.Room

class SummaryViewModel(application: Application) : AndroidViewModel(application) {

    //initialize the database instance
    private val db = Room.databaseBuilder(
        application,
        AppDatabase::class.java,
        "budget_tracker_db"
    ).build()

    //get the DAOs for accessing data
    private val categoryDao = db.categoryDao()
    private val summaryDao = db.expenseSummaryDao()

    //fetch totals by category within a specific time range
    suspend fun getTotalsByCategory(startTime: Long, endTime: Long): List<Pair<String, Double>> {
        //list to hold the category totals
        val result = mutableListOf<Pair<String, Double>>()
        //get all categories from the database
        val categories = categoryDao.getAll()

        //loop - calculate total for each category within the time period
        for (category in categories) {
            val totalNumber: Number? = summaryDao.getTotalByCategoryWithinPeriod(category.id, startTime, endTime)
            val total = totalNumber?.toDouble() ?: 0.0
            //add the category and its total to the result list
            result.add(category.name to total)
        }
        return result
    }
}