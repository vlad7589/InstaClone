package com.example.instaclone.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.instaclone.R
import com.example.instaclone.fragments.HomeFragment
import com.example.instaclone.fragments.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {

    private lateinit var bottomMenu: BottomNavigationView

    private val logoutReceiver = object : BroadcastReceiver(){
        override fun onReceive(p0: Context?, p1: Intent?) {
            if (intent.action == "ACTION_LOGOUT") {
                val loginIntent = Intent(this@MainActivity, LoginActivity::class.java)
                loginIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(loginIntent)
                finish()
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        bottomMenu = findViewById(R.id.bottomMenu)
        hideBottonNav()
        setupMenu()

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragmentContainerView, HomeFragment())
                .commit()
        }

        registerReceiver(logoutReceiver, IntentFilter("ACTION_LOGOUT"), RECEIVER_NOT_EXPORTED)
    }

    private fun hideBottonNav(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

            window.setDecorFitsSystemWindows(false)
            window.insetsController?.let { controller ->
                controller.hide(WindowInsets.Type.navigationBars())
                controller.systemBarsBehavior =
                    WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }
    }

    private fun setupMenu(){
        bottomMenu.setOnItemSelectedListener { item ->
            when (item.itemId){
                R.id.accountBtn -> switchFragment(ProfileFragment())
                R.id.addPostBtn -> startActivity(Intent(this, AddPostActivity::class.java))
            }
            true
        }
    }

    private fun switchFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragmentContainerView, fragment)
            addToBackStack(null)
            commit()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(logoutReceiver)
    }
}