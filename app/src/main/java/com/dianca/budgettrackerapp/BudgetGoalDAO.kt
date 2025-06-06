package com.dianca.budgettrackerapp.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await

class BudgetGoalDAO {

    private val firestore = FirebaseFirestore.getInstance()
    private val collection = firestore.collection("budget_goals")

    //insert or update a budget goal
    suspend fun insert(goal: BudgetGoalEntity) {
        val docId = goal.id.ifEmpty { collection.document().id }
        collection.document(docId)
            .set(goal.copy(id = docId))
            .await()
    }

    //get all budget goals for a specific month
    suspend fun getGoalsForMonth(month: String): List<BudgetGoalEntity> {
        return try {
            val snapshot: QuerySnapshot = collection
                .whereEqualTo("month", month)
                .get()
                .await()
            snapshot.toObjects(BudgetGoalEntity::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }
}
