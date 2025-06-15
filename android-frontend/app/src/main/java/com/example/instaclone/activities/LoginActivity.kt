package com.example.instaclone.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.example.instaclone.R
import com.example.instaclone.apiServices.ApiClient
import com.example.instaclone.apiServices.UserApiService
import com.example.instaclone.tools.SharedPreferencesManager
import com.example.instaclone.databinding.ActivityLoginBinding
import com.example.instaclone.viemodels.LoginViewModel
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.example.instaclone.viemodels.factories.LoginViewModelFactory

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
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
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        loginViewModel.isLoggedIn.observe(this, Observer { isLoggedIn ->
            if(isLoggedIn) goToMain()
        })

        loginViewModel.loginStatus.observe(this, Observer { status ->
            Toast.makeText(this, status, Toast.LENGTH_SHORT).show()
        })

        binding.refToSignUp.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent);
            finish();
        }

        binding.logInBtn.setOnClickListener{
            val identifier = binding.indefiredEdit.text.toString()
            val password = binding.passwordEdit.text.toString()
            loginViewModel.loginUser(identifier, password)
        }
    }

    private fun goToMain(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}