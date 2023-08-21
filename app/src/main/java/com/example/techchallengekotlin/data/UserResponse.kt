package com.example.techchallengekotlin.data

/**
 * Data class - API response containing a user list.
 * @param users List of Objects (UserData) - Users response by the API.
 */
data class UserResponse(
    val users: List<UserData>
)