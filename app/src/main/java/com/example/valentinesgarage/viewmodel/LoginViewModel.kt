package com.example.valentinesgarage.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * ViewModel for LoginActivity. Handles authentication logic.
 */
class LoginViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _loginSuccess = MutableLiveData(false)
    val loginSuccess: LiveData<Boolean> = _loginSuccess

    private val _errorMessage = MutableLiveData("")
    val errorMessage: LiveData<String> = _errorMessage

    /** Check if user is already logged in */
    fun isLoggedIn(): Boolean = auth.currentUser != null

    /** Login with email and password */
    fun login(email: String, password: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                auth.signInWithEmailAndPassword(email, password).await()
                _loginSuccess.value = true
            } catch (e: Exception) {
                _errorMessage.value = "Login failed: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /** Register a new user and save their profile */
    fun register(email: String, password: String, name: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                val userId = result.user!!.uid

                // Save employee profile to Firestore
                val employee = hashMapOf(
                    "id" to userId,
                    "name" to name,
                    "email" to email,
                    "role" to "mechanic"
                )
                db.collection("employees").document(userId).set(employee).await()
                _loginSuccess.value = true
            } catch (e: Exception) {
                _errorMessage.value = "Registration failed: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
