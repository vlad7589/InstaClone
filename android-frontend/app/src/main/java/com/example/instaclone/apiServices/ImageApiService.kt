package com.example.instaclone.apiServices

import com.example.instaclone.models.UserPostResponse
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ImageApiService {
    @Multipart
    @POST("images/upload/{userId}/{desc}")
    suspend fun uploadImage(
        @Path("userId") userId: Long,
        @Path("desc") desc: String,
        @Part file: MultipartBody.Part
    ) : Response<ResponseBody>

    @PATCH("images/{postId}/{userId}/like")
    suspend fun likePost(
        @Path("postId") postId: Long,
        @Path("userId") userId: Long
    ) : Response<Void>

    @PATCH("images/{postId}/{userId}/unlike")
    suspend fun unlikePost(
        @Path("postId") postId: Long,
        @Path("userId") userId: Long
    ) : Response<Void>

    @GET("images/{postId}/likes/count")
    suspend fun getLikesCount(
        @Path("postId") postId: Long
    ) : Response<Int>

    @GET("images/{postId}/{userId}/likes/check")
    suspend fun isLikedByUser(
        @Path("postId") postId: Long,
        @Path("userId") userId: Long
    ) : Response<Boolean>

    @GET("images/user/{userId}")
    suspend fun getUserImage(@Path("userId") userId: Long) : Response<List<UserPostResponse>>
}