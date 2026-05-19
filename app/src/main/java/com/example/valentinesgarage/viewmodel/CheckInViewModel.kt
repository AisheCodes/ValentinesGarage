package com.example.valentinesgarage.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.valentinesgarage.data.model.Truck
import com.example.valentinesgarage.data.repository.TruckRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * ViewModel for CheckInActivity.
 */
class CheckInViewModel : ViewModel() {

    private val repository = TruckRepository()
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _checkInSuccess = MutableLiveData(false)
    val checkInSuccess: LiveData<Boolean> = _checkInSuccess

    private val _errorMessage = MutableLiveData("")
    val errorMessage: LiveData<String> = _errorMessage

    fun checkInTruck(plateNumber: String, km: Int, condition: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val userId = auth.currentUser!!.uid
                // Get employee name from Firestore
                val employeeDoc = db.collection("employees").document(userId).get().await()
                val employeeName = employeeDoc.getString("name") ?: "Unknown"

                val truck = Truck(
                    plateNumber = plateNumber,
                    kmOnArrival = km,
                    condition = condition,
                    checkedInBy = userId,
                    checkedInByName = employeeName
                )

                val result = repository.checkInTruck(truck)
                result.onSuccess { _checkInSuccess.value = true }
                result.onFailure { _errorMessage.value = it.message ?: "Check-in failed" }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "An error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
