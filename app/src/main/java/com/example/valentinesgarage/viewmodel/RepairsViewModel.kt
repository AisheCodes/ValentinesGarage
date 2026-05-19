package com.example.valentinesgarage.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.valentinesgarage.data.model.RepairTask
import com.example.valentinesgarage.data.repository.RepairTaskRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * ViewModel for RepairsActivity.
 */
class RepairsViewModel : ViewModel() {

    private val repository = RepairTaskRepository()
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _tasks = MutableLiveData<List<RepairTask>>(emptyList())
    val tasks: LiveData<List<RepairTask>> = _tasks

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData("")
    val errorMessage: LiveData<String> = _errorMessage

    fun loadTasks(truckId: String) {
        _isLoading.value = true
        viewModelScope.launch {
            val result = repository.getTasksForTruck(truckId)
            result.onSuccess { _tasks.value = it }
            result.onFailure { _errorMessage.value = it.message ?: "Error loading tasks" }
            _isLoading.value = false
        }
    }

    fun addTask(truckId: String, description: String) {
        viewModelScope.launch {
            val task = RepairTask(truckId = truckId, description = description)
            val result = repository.addTask(task)
            result.onSuccess { loadTasks(truckId) }
            result.onFailure { _errorMessage.value = it.message ?: "Error adding task" }
        }
    }

    fun updateTask(task: RepairTask) {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser!!.uid
                val employeeDoc = db.collection("employees").document(userId).get().await()
                val employeeName = employeeDoc.getString("name") ?: "Unknown"

                val updatedTask = task.copy(
                    doneBy = userId,
                    doneByName = employeeName,
                    timestamp = System.currentTimeMillis()
                )
                val result = repository.updateTask(updatedTask)
                result.onSuccess { loadTasks(task.truckId) }
                result.onFailure { _errorMessage.value = it.message ?: "Error updating task" }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error"
            }
        }
    }
}
