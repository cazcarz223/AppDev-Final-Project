package com.eventapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eventapp.network.UserDetails
import com.eventapp.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
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
    fun signUp(username: String, password: String, name: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            authRepository.signUp(username, password, name)
                .catch { _authState.value = AuthState.Error(it.message ?: "Sign up failed") }
                .collect { userDetails ->
                    _authState.value = AuthState.Authenticated(userDetails)
                }
        }
    }

    fun signIn(username: String, password: String, isTutor: Boolean) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                authRepository.signIn(username, isTutor)
                    .catch { _authState.value = AuthState.Error(it.message ?: "Sign in failed") }
                    .collect { serverPassword ->
                        if (password.equals(serverPassword)) {
                            _authState.value = AuthState.Authenticated(UserDetails(username, null, null, null))
                        } else {
                            _authState.value = AuthState.Error("Invalid credentials")
                        }
                    }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Sign in failed")
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
    init {
        checkAuthStatus()
    }

    private fun checkAuthStatus() {
        viewModelScope.launch {
            val currentUser = FirebaseAuth.getInstance().currentUser
            _authState.value = if (currentUser != null) {
                AuthState.Authenticated(UserDetails(currentUser.displayName ?: "", null, currentUser.email, null))
            } else {
                AuthState.Unauthenticated
            }
        }
    }


    /**
     * Sealed class to represent different authentication states
     */
    sealed class AuthState {
        object Unauthenticated : AuthState()
        object Loading : AuthState()
        data class Authenticated(val userDetails: com.eventapp.network.UserDetails) : AuthState()
        data class Error(val message: String) : AuthState()
    }

    data class UserDetails(
        val username: String,
        val name: String,
        val email: String? = null,
        val additionalDetails: Map<String, Any>? = null
    )
}
