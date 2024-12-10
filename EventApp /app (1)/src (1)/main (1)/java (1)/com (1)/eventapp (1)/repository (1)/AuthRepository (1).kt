package com.eventapp.repository

import com.eventapp.network.AuthApi
import com.eventapp.network.AuthRequest
import com.eventapp.network.UserCreateRequest
import com.eventapp.network.UserDetails
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Repository for handling authentication-related operations
 * Mediates between the ViewModel and the network/local data sources
 */
class AuthRepository @Inject constructor(
    private val authApi: AuthApi
) {
    /**
     * Sign up a new user
     * @param email User's email
     * @param password User's password
     * @param additionalDetails Optional additional user details
     */
    suspend fun signUp(username: String, password: String, name: String): Flow<UserDetails> = flow {
        try {
            val userDetails = authApi.createUser(UserCreateRequest(username, password, name))
            emit(userDetails)
        } catch (e: Exception) {
            // Handle error
        }
    }

    suspend fun signIn(username: String, isTutor: Boolean): Flow<String> = flow {
        try {
            val response = if (isTutor) {
                authApi.getTutorPassword(AuthRequest(username))
            } else {
                authApi.getStudentPassword(AuthRequest(username))
            }
            emit(response.password)
        } catch (e: Exception) {
            // Handle error
        }
    }


    /**
     * Sign out current user
     */
    suspend fun signOut() {
        try {
            // Network call to invalidate token
            authApi.signOut()

        } catch (e: Exception) {
            // Handle sign-out errors
        }
    }
}


    /**
     * Local data source for authentication-related operations
     * TODO: Implement using Room or SharedPreferences
     */