package com.example.instaclone.tools

import android.content.Context
import com.example.instaclone.models.User

class SharedPreferencesManager(context: Context) {

    private val prefs = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

    companion object{
        const val KEY_TOKEN = "token"
        const val KEY_USER_ID = "userId"
        const val KEY_USERNAME = "username"
    }

    fun saveUserSession(user: User, token: String){
        prefs.edit().apply {
            putString(KEY_TOKEN, token)
            putString(KEY_USER_ID, user.userId.toString())
            putString(KEY_USERNAME, user.username)
            apply()
        }
    }

    fun deleteUserSession(){
        prefs.edit().apply {
            remove(KEY_TOKEN)
            remove(KEY_USER_ID)
            remove(KEY_USERNAME)
            apply()
        }
    }

    fun cleanSession() { prefs.edit().clear().apply() }
    fun isLoggerIn() = prefs.getString(KEY_TOKEN, null) != null
    fun getToken() = prefs.getString(KEY_TOKEN, null)
    fun getUsername() = prefs.getString(KEY_USERNAME, null)
    fun getUserId() = prefs.getString(KEY_USER_ID, null)
}