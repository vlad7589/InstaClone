package com.example.instaclone.activities

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.instaclone.R
import com.example.instaclone.adapters.UserPostsAdapter
import com.example.instaclone.tools.Resource
import com.example.instaclone.tools.SharedPreferencesManager
import com.example.instaclone.viemodels.ImageViewModel
import com.example.instaclone.viemodels.factories.ImageViewModelFactory.Companion.createImageViewModelFactory

class PostsActivity : AppCompatActivity() {
    private lateinit var prefManager: SharedPreferencesManager
    private val imageViewModel: ImageViewModel by viewModels{
        createImageViewModelFactory()
    }
    private lateinit var postsAdapter: UserPostsAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_posts)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        prefManager = SharedPreferencesManager(this)
        postsAdapter = UserPostsAdapter(
                prefManager,
                imageViewModel,
            this
        )
        val backArrowBtn = findViewById<ImageView>(R.id.backArrowBtn)
        backArrowBtn.setOnClickListener {
            finish()
        }
        val posts = findViewById<RecyclerView>(R.id.postsRecyclerView)
        posts.apply {
            layoutManager = GridLayoutManager(context, 1)
            adapter = postsAdapter
        }
        setupObservers()
    }

    private fun setupObservers() {
        val userId = prefManager.getUserId()
        if (userId == null) {
            Log.e("PostActivity", "User ID is null")
            return
        }
        Log.d("PostActivity", "Start setupObserve")
        imageViewModel.getUserImages(userId.toLong())
        imageViewModel.userImages.observe(this) {
            if(it is Resource.Success) postsAdapter.submitList(it.data.reversed())
        }
    }
}