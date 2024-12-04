package com.eventapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eventapp.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for handling authentication-related operations
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    // Authentication state
    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    /**
     * Sign up with email and password
     * @param email User's email
     * @param password User's password
     * @param additionalDetails Optional additional user details
     */
    fun signUp(
        email: String,
        password: String,
        additionalDetails: Map<String, String> = emptyMap()
    ) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val result = authRepository.signUp(email, password, additionalDetails)
                _authState.value = AuthState.Authenticated(result)
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Sign up failed")
            }
        }
    }

    /**
     * Sign in with email and password
     * @param email User's email
     * @param password User's password
     */
    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val result = authRepository.signIn(email, password)
                _authState.value = AuthState.Authenticated(result)
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Sign in failed")
            }
        }
    }

    /**
     * Sign in with phone number
     * @param phoneNumber User's phone number
     * @param verificationCode Verification code for phone authentication
     */
    fun signInWithPhone(phoneNumber: String, verificationCode: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val result = authRepository.signInWithPhone(phoneNumber, verificationCode)
                _authState.value = AuthState.Authenticated(result)
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Phone sign in failed")
            }
        }
    }

    /**
     * Sign out current user
     */
    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
            _authState.value = AuthState.Unauthenticated
        }
    }

    /**
     * Sealed class to represent different authentication states
     */
    sealed class AuthState {
        object Unauthenticated : AuthState()
        object Loading : AuthState()
        data class Authenticated(val userDetails: UserDetails) : AuthState()
        data class Error(val message: String) : AuthState()
    }

    /**
     * Data class to represent user details after authentication
     * TODO: Backend team to define exact user details structure
     */
    data class UserDetails(
        val userId: String,
        val email: String?,
        val phoneNumber: String?,
        val displayName: String?,
        val profilePictureUrl: String? = null
    )
}