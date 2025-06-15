package com.example.instaclone.apiServices

import android.content.Context
import com.example.instaclone.tools.AuthInterceptor
import com.example.instaclone.tools.SharedPreferencesManager
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    private const val BASE_URL= "http://localhost/api/v1/"
    private var retrofit: Retrofit? = null

    fun getClient(context: Context): Retrofit{
        if(retrofit == null){
            val prefsManager = SharedPreferencesManager(context)

            val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(AuthInterceptor(prefsManager))
                .build()

            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit!!
    }
}