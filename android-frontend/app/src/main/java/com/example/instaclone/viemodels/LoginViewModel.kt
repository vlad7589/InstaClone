package com.example.instaclone.viemodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.instaclone.apiServices.UserApiService
import com.example.instaclone.tools.SharedPreferencesManager
import com.example.instaclone.models.LoginRequest
import com.example.instaclone.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel(
    private val userApiService: UserApiService,
    private val prefsManager: SharedPreferencesManager,
    application: Application
) : AndroidViewModel(application) {

    val loginStatus = MutableLiveData<String>()
    val isLoggedIn = MutableLiveData<Boolean>()

    init {
        isLoggedIn.value = prefsManager.isLoggerIn()
    }

    fun loginUser(identifier: String, password: String){
        val loginRequest = LoginRequest(identifier, password)

        viewModelScope.launch {
            try {
                val response = userApiService.login(loginRequest)
                if(response.isSuccessful) {
                    response.body()?.let { loginResponse ->
                        val user = User(
                            userId = loginResponse.id.toLong(),
                            username = loginResponse.username,
                            email = loginResponse.email,
                            fullName = loginResponse.fullName,
                            phoneNumber = loginResponse.phoneNumber
                        )
                        prefsManager.saveUserSession(user, loginResponse.token)
                        loginStatus.postValue("Login successful")
                        isLoggedIn.postValue(true)
                    }
                } else {
                    loginStatus.postValue("Login failed")
                    isLoggedIn.postValue(false)
                }
            } catch (e: Exception){
                loginStatus.postValue("Error: ${e.message}")
                isLoggedIn.postValue(false)
            }
        }
    }

    fun logoutUser() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = userApiService.logout()
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        prefsManager.deleteUserSession()
                        loginStatus.postValue("Logout")
                        isLoggedIn.postValue(false)
                        Log.d("Logout", "Logout successful")
                    } else {
                        Log.e("Logout", "Logout failed: ${response.errorBody()?.string()}")
                        prefsManager.deleteUserSession()
                        loginStatus.postValue("Logout")
                        isLoggedIn.postValue(false)
                    }
                }
            } catch (e: Exception) {
                Log.e("Logout", "Error during logout: ${e.message}")
                withContext(Dispatchers.Main) {
                    prefsManager.deleteUserSession()
                    loginStatus.postValue("Logout")
                    isLoggedIn.postValue(false)
                }
            }
        }
    }
}