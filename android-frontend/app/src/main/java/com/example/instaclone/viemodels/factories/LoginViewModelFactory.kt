package com.example.instaclone.viemodels.factories

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.instaclone.apiServices.UserApiService
import com.example.instaclone.tools.SharedPreferencesManager
import com.example.instaclone.viemodels.LoginViewModel

class LoginViewModelFactory(
    private val userApiService: UserApiService,
    private val prefsManager: SharedPreferencesManager,
    private val application: Application
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LoginViewModel(userApiService, prefsManager, application) as T
    }
}