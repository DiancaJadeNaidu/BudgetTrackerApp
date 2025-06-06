package com.dianca.budgettrackerapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.*

class SummaryViewModel : ViewModel() {

    //initialize firestore
    private val db = FirebaseFirestore.getInstance()

    //fetch totals by category within a specific time range
    suspend fun getTotalsByCategory(startDate: Calendar, endDate: Calendar): List<Pair<String, Double>> {
        return try {
            //convert dates to milliseconds
            val startMillis = startDate.timeInMillis
            val endMillis = endDate.timeInMillis

            //query expenses in date range
            val expensesSnapshot = db.collection("expenses")
                .whereGreaterThanOrEqualTo("timestamp", startMillis)
                .whereLessThanOrEqualTo("timestamp", endMillis)
                .get()
                .await()

            //store total amount for each category ID
            val categoryTotals = mutableMapOf<String, Double>()

            //loop - calculate total for each category within the time period
            for (doc in expensesSnapshot.documents) {
                val categoryId = doc.getString("categoryId") ?: continue
                val amount = doc.getDouble("amount") ?: 0.0
                categoryTotals[categoryId] = categoryTotals.getOrDefault(categoryId, 0.0) + amount
            }

            //get category names
            val categoriesSnapshot = db.collection("categories").get().await()
            val categoryMap = categoriesSnapshot.documents.associateBy(
                { it.id },
                { it.getString("name") ?: "Uncategorized" }
            )

            //match category names with totals and sort by amount
            return categoryTotals.map { (catId, total) ->
                val catName = categoryMap[catId] ?: "Uncategorized"
                Pair(catName, total)
            }.sortedByDescending { it.second }

        } catch (e: Exception) {
            Log.e("SummaryViewModel", "Error fetching summary: ${e.message}", e)
            emptyList()
        }
    }
}
