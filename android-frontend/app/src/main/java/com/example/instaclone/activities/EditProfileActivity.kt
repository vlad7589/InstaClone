package com.example.instaclone.activities

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.viewmodel.viewModelFactory
import com.bumptech.glide.Glide
import com.example.instaclone.R
import com.example.instaclone.apiServices.ImageApiService
import com.example.instaclone.apiServices.ProfileImageApiService
import com.example.instaclone.databinding.ActivityEditProfileBinding
import com.example.instaclone.databinding.FragmentProfileBinding
import com.example.instaclone.tools.SharedPreferencesManager
import com.example.instaclone.viemodels.ImageViewModel
import com.example.instaclone.viemodels.factories.ImageViewModelFactory
import com.example.instaclone.viemodels.factories.ImageViewModelFactory.Companion.createImageViewModelFactory
import java.io.File
import java.io.IOException
import java.io.InputStream

class EditProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var prefManager: SharedPreferencesManager
    private val imageViewModel: ImageViewModel by viewModels {
        createImageViewModelFactory()
    }
    private lateinit var pickImageLauncher: ActivityResultLauncher<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        prefManager = SharedPreferencesManager(this)
        pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                val bitmap = uriToBitmap(uri)
                binding.profileImage.setImageBitmap(bitmap)
                imageViewModel.uploadProfileImage(prefManager.getUserId()!!.toLong(), uriToFile(uri) as File)
            }
        }
        Log.d("UserId", prefManager.getUserId()!!.toLong().toString())
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            viewModel = this@EditProfileActivity.imageViewModel
            userId = prefManager.getUserId()!!.toLong()
            lifecycleOwner = this@EditProfileActivity
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.backArrowBtn.setOnClickListener{
            finish()
        }

        binding.changeProfilePhotoBtn.setOnClickListener {
            pickImage()
        }
    }

    private fun pickImage() {
        pickImageLauncher.launch("image/*")
    }

    private fun uriToBitmap(uri: Uri): Bitmap? {
        return try {
            val inputStream = contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception){
            null
        }
    }

    private fun uriToFile(uri: Uri): File? {
        val contentResolver = contentResolver
        val fileName = getFileName(uri)
        val file = File(cacheDir, fileName)

        try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            inputStream?.use {
                file.outputStream().use { outputStream ->
                    it.copyTo(outputStream)
                }
            }
        } catch (e: IOException){
            e.printStackTrace()
            return null
        }
        Log.d("FileConversion", "File saved at: ${file.absolutePath}")
        return file
    }

    private fun getFileName(uri: Uri): String {
        var name = ""
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if(it.moveToFirst()){
                val columnIndex = it.getColumnIndex(android.provider.MediaStore.Images.Media.DISPLAY_NAME)
                name = it.getString(columnIndex)
            }
        }
        return name.ifEmpty { "image_${System.currentTimeMillis()}.jpg" }
    }
}