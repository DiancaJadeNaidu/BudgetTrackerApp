package com.dianca.budgettrackerapp

import com.dianca.budgettrackerapp.data.CategoryEntity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class CategoryDAO @Inject constructor() {
    private val db = FirebaseFirestore.getInstance()
    private val categoryCollection = db.collection("categories")


    //insert a new category and return its generated ID
    suspend fun insertCategory(category: CategoryEntity): String {
        return try {
            val document = hashMapOf(
                "name" to category.name
            )

            val docRef = categoryCollection.add(document).await()
            docRef.id
        } catch (e: Exception) {
            throw Exception("Failed to add category: ${e.message}")
        }
    }


     //update an existing category
    suspend fun updateCategory(category: CategoryEntity) {
        try {
            require(category.id.isNotEmpty()) { "Category ID cannot be empty" }

            val updates = hashMapOf<String, Any>(
                "name" to category.name
            )

            categoryCollection.document(category.id)
                .update(updates)
                .await()
        } catch (e: Exception) {
            throw Exception("Failed to update category: ${e.message}")
        }
    }

    //delete category by ID
    suspend fun deleteCategory(categoryId: String) {
        try {
            require(categoryId.isNotEmpty()) { "Category ID cannot be empty" }

            categoryCollection.document(categoryId)
                .delete()
                .await()
        } catch (e: Exception) {
            throw Exception("Failed to delete category: ${e.message}")
        }
    }

    //get category by ID
    suspend fun getCategoryById(categoryId: String): CategoryEntity? {
        return try {
            require(categoryId.isNotEmpty()) { "Category ID cannot be empty" }

            val snapshot = categoryCollection.document(categoryId)
                .get()
                .await()

            if (snapshot.exists()) {
                CategoryEntity(
                    id = snapshot.id,
                    name = snapshot.getString("name") ?: ""
                )
            } else {
                null
            }
        } catch (e: Exception) {
            throw Exception("Failed to get category: ${e.message}")
        }
    }

    //get all category real time
    fun getAllCategoriesLive(
        onSuccess: (List<CategoryEntity>) -> Unit,
        onError: (Exception) -> Unit
    ): ListenerRegistration {
        return categoryCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                onError(Exception("Listen failed: ${error.message}"))
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val categories = snapshot.documents.mapNotNull { doc ->
                    CategoryEntity(
                        id = doc.id,
                        name = doc.getString("name") ?: ""
                    )
                }
                onSuccess(categories)
            }
        }
    }

    //get all category by ID w/o real time
    suspend fun getAllCategories(): List<CategoryEntity> {
        return try {
            val snapshot = categoryCollection.get().await()
            snapshot.documents.mapNotNull { doc ->
                CategoryEntity(
                    id = doc.id,
                    name = doc.getString("name") ?: ""
                )
            }
        } catch (e: Exception) {
            throw Exception("Failed to get categories: ${e.message}")
        }
    }

    //delete ALL category
    suspend fun deleteAllCategories() {
        try {
            val snapshot = categoryCollection.get().await()
            val batch = db.batch()

            snapshot.documents.forEach { doc ->
                batch.delete(doc.reference)
            }

            batch.commit().await()
        } catch (e: Exception) {
            throw Exception("Failed to delete all categories: ${e.message}")
        }
    }
}