package com.example.instaclone.tools

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.test.core.app.ApplicationProvider
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val prefsManager: SharedPreferencesManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = prefsManager.getToken()
        val request = chain.request().newBuilder()

        if(!token.isNullOrEmpty()) {
            request.addHeader("Authorization", "Bearer $token")
            Log.d("AuthInterceptor", "Authorization header added")
        } else {
            Log.d("AuthInterceptor", "No token found")
            prefsManager.cleanSession()
        }

        val response = chain.proceed(request.build())

        if (response.code == 401) {
            Log.d("AuthInterceptor", "Received 401 - clearing session")
            prefsManager.cleanSession()

            val context = ApplicationProvider.getApplicationContext<Context>()
            val intent = Intent("ACTION_LOGOUT")
            context.sendBroadcast(intent)
        }

        return response
    }
}