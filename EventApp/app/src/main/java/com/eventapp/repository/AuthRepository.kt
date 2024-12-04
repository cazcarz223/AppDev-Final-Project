package com.eventapp.repository

import com.eventapp.network.AuthApi
import com.eventapp.viewmodel.AuthViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Repository for handling authentication-related operations
 * Mediates between the ViewModel and the network/local data sources
 */
class AuthRepository @Inject constructor(
    private val authApi: AuthApi,
    private val localDataSource: AuthLocalDataSource
) {
    /**
     * Sign up a new user
     * @param email User's email
     * @param password User's password
     * @param additionalDetails Optional additional user details
     */
    suspend fun signUp(
        email: String,
        password: String,
        additionalDetails: Map<String, String> = emptyMap()
    ): AuthViewModel.UserDetails {
        return try {
            // Network call to sign up
            val userDetails = authApi.signUp(email, password, additionalDetails)

            // Cache user details locally
            localDataSource.saveUserDetails(userDetails)

            userDetails
        } catch (e: Exception) {
            // Handle sign-up errors
            throw e
        }
    }

    /**
     * Sign in an existing user
     * @param email User's email
     * @param password User's password
     */
    suspend fun signIn(email: String, password: String): AuthViewModel.UserDetails {
        return try {
            // Network call to sign in
            val userDetails = authApi.signIn(email, password)

            // Cache user details locally
            localDataSource.saveUserDetails(userDetails)

            userDetails
        } catch (e: Exception) {
            // Handle sign-in errors
            throw e
        }
    }

    /**
     * Sign in with phone number
     * @param phoneNumber User's phone number
     * @param verificationCode Verification code
     */
    suspend fun signInWithPhone(
        phoneNumber: String,
        verificationCode: String
    ): AuthViewModel.UserDetails {
        return try {
            // Network call to sign in with phone
            val userDetails = authApi.signInWithPhone(phoneNumber, verificationCode)

            // Cache user details locally
            localDataSource.saveUserDetails(userDetails)

            userDetails
        } catch (e: Exception) {
            // Handle phone sign-in errors
            throw e
        }
    }

    /**
     * Sign out current user
     */
    suspend fun signOut() {
        try {
            // Network call to invalidate token
            authApi.signOut()

            // Clear local user data
            localDataSource.clearUserDetails()
        } catch (e: Exception) {
            // Handle sign-out errors
            // Typically, we still want to clear local data even if network call fails
            localDataSource.clearUserDetails()
        }
    }

    /**
     * Check if user is currently authenticated
     */
    fun isAuthenticated(): Flow<Boolean> = flow {
        val userDetails = localDataSource.getUserDetails()
        emit(userDetails != null)
    }
}

/**
 * Local data source for authentication-related operations
 * TODO: Implement using Room or SharedPreferences
 */
interface AuthLocalDataSource {
    /**
     * Save user details after successful authentication
     */
    suspend fun saveUserDetails(userDetails: AuthViewModel.UserDetails)

    /**
     * Retrieve locally stored user details
     */
    suspend fun getUserDetails(): AuthViewModel.UserDetails?

    /**
     * Clear stored user details (used during sign out)
     */
    suspend fun clearUserDetails()
}