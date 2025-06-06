package com.dianca.budgettrackerapp

import com.dianca.budgettrackerapp.data.ExpenseEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ExpenseDAO {
    private val db = FirebaseFirestore.getInstance()
    private val expenses = db.collection("expenses")

    suspend fun getAll(): List<ExpenseEntity> {
        return expenses.get().await().toObjects(ExpenseEntity::class.java)
    }

    suspend fun insert(expense: ExpenseEntity) {
        expenses.document(expense.id).set(expense).await()
    }

    suspend fun delete(expense: ExpenseEntity) {
        expenses.document(expense.id).delete().await()
    }
}