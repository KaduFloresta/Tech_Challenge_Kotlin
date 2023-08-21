package com.example.techchallengekotlin.api

import com.example.techchallengekotlin.data.UserData
import com.example.techchallengekotlin.data.UserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

/**
 * Endpoints da API.
 */
interface ApiService {
    /**
     * Endpoint to get users list.
     * @return Api response with users list.
     */
    @GET("ce47ee53-6531-4821-a6f6-71a188eaaee0")
    suspend fun getUsers(): Response<UserResponse>

    /**
     * Endpoint to update user data.
     * @param userId ID of the user to be updated.
     * @param userData New user data to update.
     * @return Api response after updating user.
     */
    @PUT("ce47ee53-6531-4821-a6f6-71a188eaaee0/{userId}")
    suspend fun updateUser(@Path("userId") userId: Int, @Body userData: UserData): Response<UserResponse>
}
