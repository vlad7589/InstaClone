package com.example.instaclone.activities

import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.instaclone.R
import com.example.instaclone.adapters.PhotoAdapter
import com.example.instaclone.tools.Resource
import com.example.instaclone.tools.SharedPreferencesManager
import com.example.instaclone.viemodels.ImageViewModel
import com.example.instaclone.viemodels.PhotoPickerViewModel
import com.example.instaclone.viemodels.factories.ImageViewModelFactory.Companion.createImageViewModelFactory
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.net.URI

class AddPostActivity : AppCompatActivity() {
    private lateinit var photoGrid: RecyclerView
    private lateinit var selectedPhotoView: ImageView
    private lateinit var progressBar: ProgressBar
    private lateinit var submitPhotoBtn: ImageView
    private lateinit var goBackBtn: ImageView
    private lateinit var editDescView: EditText
    private lateinit var prefManager: SharedPreferencesManager
    private var selectedPhotoUri: Uri? = null
    // count used to setup submit btn (select photo/post request)
    private var count = 0

    private val photoPickerViewModel: PhotoPickerViewModel by viewModels()
    private val imageViewModel: ImageViewModel by viewModels {
        createImageViewModelFactory()
    }
    private val photoAdapter = PhotoAdapter { photoPath ->
        photoPickerViewModel.selectPhoto(photoPath)
    }
    private val requiredPermission: String
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            android.Manifest.permission.READ_MEDIA_IMAGES
        } else {
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_post)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        prefManager = SharedPreferencesManager(applicationContext)
        setupViews()
        setupObservers()

        if (checkPermissions()) {
        } else {
            requestPermissions()
        }

        goBackBtn.setOnClickListener {
            val i = count
            when(i){
                0 -> finish()
                1 -> {
                    photoGrid.visibility = View.VISIBLE
                    editDescView.visibility = View.GONE
                    count = 0
                }
            }
        }
        submitPhotoBtn.setOnClickListener {
            val i = count
            when(i){
                0 -> {
                    photoGrid.visibility = View.GONE
                    editDescView.visibility = View.VISIBLE
                    count = 1
                }
                1 -> {
                    if(selectedPhotoUri != null){
                        val file = imageViewModel.uriToFile(uri = selectedPhotoUri!!)
                        if (file == null || !file.exists()) {
                            Log.e("UploadPost", "Error: File does not exist or is null!")
                        } else {
                            Log.d("UploadPost", "File path: ${file.absolutePath}, Size: ${file.length()} bytes")

                            imageViewModel.uploadImage(
                                prefManager.getUserId()!!.toLong(),
                                file,
                                editDescView.text.toString()
                            )
                            imageViewModel.uploadStatus.observe(this){
                                if(it is Resource.Success) finish()
                            }
                        }

                    }
                    else Toast.makeText(this.applicationContext, "Select some photo", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupViews(){
        photoGrid = findViewById(R.id.photoGrid)
        selectedPhotoView = findViewById(R.id.selectedPhoto)
        progressBar = findViewById(R.id.progressBar)
        submitPhotoBtn = findViewById(R.id.submitPhoto)
        editDescView = findViewById(R.id.addDescEdit)
        goBackBtn = findViewById(R.id.backArrowBtn)

        photoGrid.apply {
            layoutManager = GridLayoutManager(this@AddPostActivity, 3)
            adapter = photoAdapter
            setHasFixedSize(true)
        }
    }

    private fun setupObservers(){
        photoPickerViewModel.uiState.observe(this) { state ->
            progressBar.visibility = if(state.isLoading) View.VISIBLE else View.GONE

            photoAdapter.submitList(state.photos)

            state.selectedPhoto?.let { photoPath ->
                Glide.with(this)
                    .load(photoPath)
                    .centerCrop()
                    .into(selectedPhotoView)
                selectedPhotoUri = Uri.fromFile(File(photoPath))
            }

            state.error?.let { error ->
                Snackbar.make(photoGrid, error, Snackbar.LENGTH_LONG)
                    .setAction("Retry") { photoPickerViewModel.retryLoading() }
                    .show()
            }
        }
    }

    private fun checkPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            requiredPermission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(requiredPermission),
            PERMISSIONS_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_CODE &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
        } else {
            Toast.makeText(
                this,
                "Permission required to access photos",
                Toast.LENGTH_LONG
            ).show()
            finish()
        }
    }

    companion object {
        private const val PERMISSIONS_REQUEST_CODE = 123
    }
}