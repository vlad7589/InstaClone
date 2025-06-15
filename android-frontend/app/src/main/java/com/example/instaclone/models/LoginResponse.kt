package com.example.instaclone.models

data class LoginResponse(
    val token: String,
    val id: String,
    val username: String,
    val fullName: String,
    val phoneNumber: String,
    val email: String
)
