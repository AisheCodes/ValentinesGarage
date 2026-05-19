package com.example.valentinesgarage.data.repository

import com.example.valentinesgarage.data.model.RepairTask
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Repository for repair task Firebase operations.
 */
class RepairTaskRepository {

    private val db = FirebaseFirestore.getInstance()

    /** Add a repair task to a truck */
    suspend fun addTask(task: RepairTask): Result<String> {
        return try {
            val docRef = db.collection("trucks")
                .document(task.truckId)
                .collection("tasks")
                .add(task).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /** Update task as done with notes */
    suspend fun updateTask(task: RepairTask): Result<Unit> {
        return try {
            db.collection("trucks")
                .document(task.truckId)
                .collection("tasks")
                .document(task.id)
                .set(task).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /** Get all tasks for a specific truck */
    suspend fun getTasksForTruck(truckId: String): Result<List<RepairTask>> {
        return try {
            val snapshot = db.collection("trucks")
                .document(truckId)
                .collection("tasks")
                .get().await()
            val tasks = snapshot.documents.map { doc ->
                doc.toObject(RepairTask::class.java)!!.copy(id = doc.id)
            }
            Result.success(tasks)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
