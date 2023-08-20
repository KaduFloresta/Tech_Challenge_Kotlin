package com.example.techchallengekotlin.api

import com.example.techchallengekotlin.data.UserData
import com.example.techchallengekotlin.data.UserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @GET("ce47ee53-6531-4821-a6f6-71a188eaaee0")
    suspend fun getUsers(): Response<UserResponse>

    @PUT("users/{id}")
    suspend fun updateUser(@Path("id") userId: Int, @Body userData: UserData): Response<UserResponse>
}
