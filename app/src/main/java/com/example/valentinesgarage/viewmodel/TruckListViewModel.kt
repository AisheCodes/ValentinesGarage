package com.example.valentinesgarage.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.valentinesgarage.data.model.Truck
import com.example.valentinesgarage.data.repository.TruckRepository
import kotlinx.coroutines.launch

/**
 * ViewModel for TruckListActivity.
 */
class TruckListViewModel : ViewModel() {

    private val repository = TruckRepository()

    private val _trucks = MutableLiveData<List<Truck>>(emptyList())
    val trucks: LiveData<List<Truck>> = _trucks

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData("")
    val errorMessage: LiveData<String> = _errorMessage

    fun loadTrucks() {
        _isLoading.value = true
        viewModelScope.launch {
            val result = repository.getAllTrucks()
            result.onSuccess { _trucks.value = it }
            result.onFailure { _errorMessage.value = it.message ?: "Error loading trucks" }
            _isLoading.value = false
        }
    }
}
