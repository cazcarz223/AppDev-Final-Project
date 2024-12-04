package com.eventapp.di

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * This is the AppModule. This handles dependency injection with Dagger Hilt for you, so you do not
 * need to modify this file.
 *
 * Essentially what it does is provide a Singleton instance of a Moshi adapter and a Singleton
 * instance of MyApi so they can be used throughout the app.
 */
object RetrofitClient {
    private const val BASE_URL = "https://api.youreventapp.com/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }

    annotation class ApiService

}