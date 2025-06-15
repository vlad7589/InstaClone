package com.example.instaclone.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.instaclone.R
import com.example.instaclone.apiServices.ApiClient
import com.example.instaclone.apiServices.UserApiService
import com.example.instaclone.tools.SharedPreferencesManager
import com.example.instaclone.viemodels.LoginViewModel
import com.example.instaclone.viemodels.factories.LoginViewModelFactory

class SettingActivity : AppCompatActivity() {
    private val loginViewModel: LoginViewModel by viewModels {
        LoginViewModelFactory(
            userApiService = ApiClient.getClient(this).create(UserApiService::class.java),
            prefsManager = SharedPreferencesManager(this),
            application = application
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_setting)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val logoutBtn = findViewById<Button>(R.id.logoutBtn)

        loginViewModel.loginStatus.observe(this) { state ->
            Log.d("Logout", "Navigating to LoginActivity")
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        logoutBtn.setOnClickListener {
            loginViewModel.logoutUser()
        }
    }
}