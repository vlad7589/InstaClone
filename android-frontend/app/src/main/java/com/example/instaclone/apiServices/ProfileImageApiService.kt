package com.example.instaclone.apiServices

import com.example.instaclone.models.ProfileImageResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ProfileImageApiService {
    @Multipart
    @POST("images/upload/profile-image/{userId}")
    suspend fun uploadProfileImage(
        @Path("userId") userId: Long,
        @Part file: MultipartBody.Part
    ) : Response<String>

    @GET("images/user/profile-image/{userId}")
    suspend fun getProfileImage(@Path("userId") userId: Long) : Response<ProfileImageResponse>
}