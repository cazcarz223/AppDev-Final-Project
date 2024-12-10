package com.eventapp.network

import com.eventapp.viewmodel.AuthViewModel
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Retrofit API interface for authentication-related network operations
 * TODO: Backend team should implement these endpoints
 */
interface AuthApi {
    @POST("api/users/")
    suspend fun createUser(@Body user: UserCreateRequest): UserDetails

    @POST("api/tutor/authentication/")
    suspend fun getTutorPassword(@Body request: AuthRequest): AuthResponse

    @POST("api/student/authentication/")
    suspend fun getStudentPassword(@Body request: AuthRequest): AuthResponse

    fun signOut()
}

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

data class UserCreateRequest(
    val Username: String,
    val Password: String,
    val Name: String
)

data class AuthRequest(
    val username: String
)

data class AuthResponse(
    val password: String
)