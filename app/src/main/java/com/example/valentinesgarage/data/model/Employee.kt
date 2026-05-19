package com.example.valentinesgarage.data.model

/**
 * Represents an employee (mechanic or admin) at Valentine's Garage.
 */
data class Employee(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val role: String = "mechanic" // "mechanic" or "admin"
)
