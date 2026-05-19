package com.example.valentinesgarage.data.model

/**
 * Represents a repair task assigned to a truck during servicing.
 */
data class RepairTask(
    val id: String = "",
    val truckId: String = "",
    val description: String = "",
    val isDone: Boolean = false,
    val notes: String = "",
    val doneBy: String = "",
    val doneByName: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
