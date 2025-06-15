package com.example.instaclone.viemodels.factories

import android.app.Application
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.instaclone.apiServices.ApiClient
import com.example.instaclone.apiServices.ImageApiService
import com.example.instaclone.apiServices.ProfileImageApiService
import com.example.instaclone.viemodels.ImageViewModel

class ImageViewModelFactory(
    private val context: Context,
    private val application: Application,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ImageViewModel::class.java)) {
            val imageApiService = ApiClient.getClient(context).create(ImageApiService::class.java)
            val profileImageApiService = ApiClient.getClient(context).create(ProfileImageApiService::class.java)

            return ImageViewModel(imageApiService, profileImageApiService, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

    companion object {
        fun Fragment.createImageViewModelFactory(): ImageViewModelFactory {
            return ImageViewModelFactory(requireContext().applicationContext, requireActivity().application)
        }

        fun ComponentActivity.createImageViewModelFactory(): ImageViewModelFactory {
            return ImageViewModelFactory(applicationContext, application)
        }
    }
}