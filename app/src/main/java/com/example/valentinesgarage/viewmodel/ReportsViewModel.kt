package com.example.valentinesgarage.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.valentinesgarage.ui.reports.ReportItem
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * ViewModel for ReportsActivity. Loads all tasks across all trucks.
 */
class ReportsViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private val _reports = MutableLiveData<List<ReportItem>>(emptyList())
    val reports: LiveData<List<ReportItem>> = _reports

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData("")
    val errorMessage: LiveData<String> = _errorMessage

    fun loadReports() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val reportList = mutableListOf<ReportItem>()
                val trucksSnapshot = db.collection("trucks").get().await()

                for (truckDoc in trucksSnapshot.documents) {
                    val plateNumber = truckDoc.getString("plateNumber") ?: ""
                    val tasksSnapshot = db.collection("trucks")
                        .document(truckDoc.id)
                        .collection("tasks")
                        .get().await()

                    for (taskDoc in tasksSnapshot.documents) {
                        reportList.add(
                            ReportItem(
                                id = taskDoc.id,
                                plateNumber = plateNumber,
                                employeeName = taskDoc.getString("doneByName") ?: "Unassigned",
                                taskDescription = taskDoc.getString("description") ?: "",
                                notes = taskDoc.getString("notes") ?: "",
                                isDone = taskDoc.getBoolean("isDone") ?: false,
                                timestamp = taskDoc.getLong("timestamp") ?: 0L
                            )
                        )
                    }
                }

                _reports.value = reportList.sortedByDescending { it.timestamp }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error loading reports"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
