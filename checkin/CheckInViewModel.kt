package com.valentinesgarage.ui.checkin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.valentinesgarage.data.model.ConditionRating
import com.valentinesgarage.data.model.Truck
import com.valentinesgarage.data.repository.TruckRepository
import com.valentinesgarage.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CheckInViewModel @Inject constructor(
    private val truckRepository: TruckRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    sealed class CheckInState {
        object Idle : CheckInState()
        object Loading : CheckInState()
        data class Success(val truckId: String, val jobId: String) : CheckInState()
        data class Error(val message: String) : CheckInState()
    }

    private val _state = MutableStateFlow<CheckInState>(CheckInState.Idle)
    val state: StateFlow<CheckInState> = _state

    private val _photoUris = MutableStateFlow<List<String>>(emptyList())
    val photoUris: StateFlow<List<String>> = _photoUris

    fun addPhoto(uri: String) {
        _photoUris.value = _photoUris.value + uri
    }

    fun checkInTruck(
        registration: String,
        make: String,
        model: String,
        kilometers: String,
        condition: ConditionRating,
        conditionNotes: String
    ) {
        if (registration.isBlank()) {
            _state.value = CheckInState.Error("Registration number is required")
            return
        }
        val km = kilometers.toLongOrNull()
        if (km == null || km < 0) {
            _state.value = CheckInState.Error("Please enter a valid kilometer reading")
            return
        }

        val currentUser = userRepository.currentFirebaseUser
        if (currentUser == null) {
            _state.value = CheckInState.Error("You must be logged in to check in a truck")
            return
        }

        viewModelScope.launch {
            _state.value = CheckInState.Loading
            val truck = Truck(
                registrationNumber = registration.uppercase(),
                make = make,
                model = model,
                kilometerReading = km,
                conditionRating = condition,
                conditionNotes = conditionNotes,
                checkedInBy = currentUser.uid,
                checkedInByName = currentUser.displayName ?: currentUser.email ?: "Unknown"
            )
            val result = truckRepository.checkInTruck(truck)
            _state.value = result.fold(
                onSuccess = { CheckInState.Success(it.truckId, "") },
                onFailure = { CheckInState.Error(it.message ?: "Check-in failed") }
            )
        }
    }
}
