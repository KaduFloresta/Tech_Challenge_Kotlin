package com.example.techchallengekotlin.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Responsible class to create Ui APiService instance.
 */
object ApiServiceFactory {
    // API base URL
    private const val BASE_URL = "https://run.mocky.io/v3/"

    /**
     * Method to create an ApiService instance.
     * @return ApiService instance configured with Retrofit.
     */
    fun create(): ApiService {
        // Retrofit configuration
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // GsonConverterFactory to convert JSON to objects
            .build()

        // Create and return API instance
        return retrofit.create(ApiService::class.java)
    }
}
