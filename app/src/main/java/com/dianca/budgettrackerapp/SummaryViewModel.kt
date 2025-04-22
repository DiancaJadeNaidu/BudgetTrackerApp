package com.dianca.budgettrackerapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.room.Room

class SummaryViewModel(application: Application) : AndroidViewModel(application) {

    private val db = Room.databaseBuilder(
        application,
        AppDatabase::class.java,
        "budget_tracker_db"
    ).build()

    private val categoryDao = db.categoryDao()
    private val summaryDao = db.expenseSummaryDao()

    suspend fun getTotalsByCategory(startTime: Long, endTime: Long): List<Pair<String, Double>> {
        val result = mutableListOf<Pair<String, Double>>()
        val categories = categoryDao.getAll()

        for (category in categories) {
            val totalNumber: Number? = summaryDao.getTotalByCategoryWithinPeriod(category.id, startTime, endTime)
            val total = totalNumber?.toDouble() ?: 0.0
            result.add(category.name to total)
        }


        return result
        }
}