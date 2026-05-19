package com.example.valentinesgarage.data.model

/**
 * Represents a truck that has been checked in at Valentine's Garage.
 */
data class Truck(
    val id: String = "",
    val plateNumber: String = "",
    val kmOnArrival: Int = 0,
    val condition: String = "",
    val checkedInBy: String = "",
    val checkedInByName: String = "",
    val photoUrl: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
