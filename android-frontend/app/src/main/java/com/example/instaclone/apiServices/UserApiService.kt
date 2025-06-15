package com.example.instaclone.apiServices

import com.example.instaclone.models.LoginRequest
import com.example.instaclone.models.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface UserApiService {
    @POST("user/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @POST("user/logout")
    suspend fun logout(): Response<String>
}