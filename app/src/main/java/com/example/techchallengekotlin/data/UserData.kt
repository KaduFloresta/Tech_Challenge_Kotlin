package com.example.techchallengekotlin.data

/**
 * Data class with users info.
 * @param id User unique identifier.
 * @param name User name.
 * @param email User e-mail.
 * @param age User age.
 */
data class UserData(
    var id: Int,
    var name: String,
    var email: String,
    var age: Int
)
