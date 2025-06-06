package com.dianca.budgettrackerapp.data

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ExpenseSummaryDAO(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    suspend fun getTotalByCategoryWithinPeriod(
        categoryId: String,
        startTime: Long,
        endTime: Long
    ): Double {
        return try {
            val snapshot = db.collection("expenses")
                .whereEqualTo("categoryId", categoryId)
                .whereGreaterThanOrEqualTo("timestamp", startTime)
                .whereLessThanOrEqualTo("timestamp", endTime)
                .get()
                .await()

            val total = snapshot.documents.sumOf {
                val amount = it.getDouble("amount") ?: 0.0
                amount
            }

            total
        } catch (e: Exception) {
            e.printStackTrace()
            0.0
        }
    }
}
