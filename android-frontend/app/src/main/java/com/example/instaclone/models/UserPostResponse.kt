package com.example.instaclone.models

data class UserPostResponse(
    val id: Long,
    val name: String,
    val desc: String,
    val comments: List<String>,
    val data: String,
    val contentType: String
)
