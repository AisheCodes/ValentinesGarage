package com.example.valentinesgarage.data.repository

import com.example.valentinesgarage.data.model.Truck
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Repository for truck-related Firebase operations.
 */
class TruckRepository {

    private val db = FirebaseFirestore.getInstance()
    private val trucksCollection = db.collection("trucks")

    /** Add a new truck check-in to Firestore */
    suspend fun checkInTruck(truck: Truck): Result<String> {
        return try {
            val docRef = trucksCollection.add(truck).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /** Get all checked-in trucks */
    suspend fun getAllTrucks(): Result<List<Truck>> {
        return try {
            val snapshot = trucksCollection.get().await()
            val trucks = snapshot.documents.map { doc ->
                doc.toObject(Truck::class.java)!!.copy(id = doc.id)
            }
            Result.success(trucks)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
