package com.eventapp.network

import com.eventapp.viewmodel.AuthViewModel
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Retrofit API interface for authentication-related network operations
 * TODO: Backend team should implement these endpoints
 */
interface AuthApi {
    /**
     * Sign up a new user
     * @param email User's email
     * @param password User's password
     * @param additionalDetails Optional additional user details
     * @return User details after successful registration
     */
    @POST("auth/signup")
    suspend fun signUp(
        @Body email: String,
        @Body password: String,
        @Body additionalDetails: Map<String, String> = emptyMap()
    ): AuthViewModel.UserDetails

    /**
     * Sign in an existing user
     * @param email User's email
     * @param password User's password
     * @return User details after successful authentication
     */
    @POST("auth/signin")
    suspend fun signIn(
        @Body email: String,
        @Body password: String
    ): AuthViewModel.UserDetails

    /**
     * Sign in with phone number
     * @param phoneNumber User's phone number
     * @param verificationCode Verification code
     * @return User details after successful phone authentication
     */
    @POST("auth/signin/phone")
    suspend fun signInWithPhone(
        @Body phoneNumber: String,
        @Body verificationCode: String
    ): AuthViewModel.UserDetails

    /**
     * Request phone verification code
     * @param phoneNumber Phone number to verify
     */
    @POST("auth/phone/request-verification")
    suspend fun requestPhoneVerification(
        @Body phoneNumber: String
    ): PhoneVerificationResponse

    /**
     * Sign out current user
     */
    @POST("auth/signout")
    suspend fun signOut()

    /**
     * Reset user password
     * @param email User's email
     */
    @POST("auth/reset-password")
    suspend fun resetPassword(
        @Body email: String
    )
}

/**
 * Data class to represent phone verification response
 */
data class PhoneVerificationResponse(
    val verificationId: String,
    val expiresAt: Long
)

/**
 * Enum to represent different authentication providers
 */
enum class AuthProvider {
    EMAIL,
    PHONE,
    GOOGLE,
    FACEBOOK,
    APPLE
}