package com.example.instaclone.viemodels

import android.annotation.SuppressLint
import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.instaclone.apiServices.ImageApiService
import com.example.instaclone.apiServices.ProfileImageApiService
import com.example.instaclone.models.UserPostResponse
import com.example.instaclone.tools.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Dispatcher
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException
import java.io.InputStream
import kotlin.coroutines.cancellation.CancellationException

class ImageViewModel(
    private val imageApiService: ImageApiService,
    private val profileImageApiService: ProfileImageApiService,
    private val application: Application
): AndroidViewModel(application) {

    private val _uploadStatus = MutableLiveData<Resource<String>>()
    val uploadStatus: LiveData<Resource<String>> = _uploadStatus

    private val _userImages = MutableLiveData<Resource<List<UserPostResponse>>>()
    val userImages: LiveData<Resource<List<UserPostResponse>>> = _userImages

    private val _profileImage = MutableLiveData<Resource<ByteArray>>()
    val profileImage: LiveData<Resource<ByteArray>> = _profileImage

    private val likeStatusMap = mutableMapOf<Long, MutableLiveData<Resource<Boolean>>>()
    private val likesCountMap = mutableMapOf<Long, MutableLiveData<Resource<Int>>>()

    fun uploadImage(userId: Long, file: File, desc: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _uploadStatus.postValue(Resource.Loading())

                val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
                val multipartBody = MultipartBody.Part.createFormData("file", file.name, requestBody)

                val response = imageApiService.uploadImage(userId, desc, multipartBody)
                if (response.isSuccessful) {
                    _uploadStatus.postValue(Resource.Success("Upload successful"))
                } else {
                    _uploadStatus.postValue(Resource.Error(response.errorBody()?.string() ?: "Upload failed"))
                }
            } catch (e: Exception) {
                _uploadStatus.postValue(Resource.Error(e.message ?: "Unknown error"))
            }
        }
    }

    fun getUserImages(userId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d("GetUserImages", "Start getting img")
                _userImages.postValue(Resource.Loading())

                val response = imageApiService.getUserImage(userId)
                if (response.isSuccessful) {
                    response.body()?.let {
                        _userImages.postValue(Resource.Success(it))
                    } ?: _userImages.postValue(Resource.Error("No images found"))
                } else {
                    _userImages.postValue(Resource.Error(response.errorBody()?.string() ?: "Failed to load images"))
                }
            } catch (e: Exception) {
                _userImages.postValue(Resource.Error(e.message ?: "Unknown error"))
            }
        }
    }

    fun getProfileImage(userId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _profileImage.postValue(Resource.Loading())

                val response = profileImageApiService.getProfileImage(userId)
                if (response.isSuccessful) {
                    val imageResponse = response.body()
                    if (imageResponse != null) {
                        val imageData = Base64.decode(imageResponse.data, Base64.DEFAULT)
                        _profileImage.postValue(Resource.Success(imageData))
                    } else {
                        _profileImage.postValue(Resource.Error("No profile image found"))
                    }
                } else {
                    _profileImage.postValue(Resource.Error("Failed to load profile image"))
                }
            } catch (e: Exception) {
                _profileImage.postValue(Resource.Error(e.message ?: "Unknown error"))
            }
        }
    }

    fun uploadProfileImage(userId: Long, file: File) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _uploadStatus.postValue(Resource.Loading())

                val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
                val multipartBody = MultipartBody.Part.createFormData("file", file.name, requestBody)

                val response = profileImageApiService.uploadProfileImage(userId, multipartBody)
                if (response.isSuccessful) {
                    _uploadStatus.postValue(Resource.Success("Profile image uploaded"))
                    Log.d("ProfileImageUpload", "Upload Successfully")
                } else {
                    _uploadStatus.postValue(Resource.Error("Failed to upload profile image"))
                    Log.e("ProfileImageUpload", "Upload Failed ${response.errorBody()}")
                }
            } catch (e: Exception) {
                _uploadStatus.postValue(Resource.Error(e.message ?: "Unknown error"))
                Log.e("ProfileImageUpload", "Upload Fail ${e.message}")
            }
        }
    }

    fun getLikeStatus(postId: Long): LiveData<Resource<Boolean>> {
        return likeStatusMap.getOrPut(postId) { MutableLiveData() }
    }

    fun getLikesCount(postId: Long): LiveData<Resource<Int>> {
        return likesCountMap.getOrPut(postId) { MutableLiveData() }
    }


    fun likePost(postId: Long, userId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = imageApiService.likePost(postId, userId)
                if(response.isSuccessful) {
                    likeStatusMap[postId]?.postValue(Resource.Success(true))
                    // Refresh like count after successful like
                    getCountLikes(postId)
                    Log.d("Likes", "Like post $postId was successful")
                } else {
                    likeStatusMap[postId]?.postValue(Resource.Error("Failed to like post"))
                }
            } catch (e: Exception) {
                Log.e("Likes", "Error liking post: ${e.message}")
                likeStatusMap[postId]?.postValue(Resource.Error(e.message ?: "Unknown error"))
            }
        }
    }

    fun unlikePost(postId: Long, userId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = imageApiService.unlikePost(postId, userId)
                if(response.isSuccessful) {
                    likeStatusMap[postId]?.postValue(Resource.Success(false))
                    // Refresh like count after successful unlike
                    getCountLikes(postId)
                    Log.d("Unlikes", "Unlike post $postId was successful")
                } else {
                    likeStatusMap[postId]?.postValue(Resource.Error("Failed to unlike post"))
                }
            } catch (e: Exception) {
                Log.e("Unlikes", "Error unliking post: ${e.message}")
                likeStatusMap[postId]?.postValue(Resource.Error(e.message ?: "Unknown error"))
            }
        }
    }

    fun getCountLikes(postId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = imageApiService.getLikesCount(postId)
                if(response.isSuccessful) {
                    response.body()?.let {
                        likesCountMap[postId]?.postValue(Resource.Success(it))
                    } ?: likesCountMap[postId]?.postValue(Resource.Error("No count received"))
                } else {
                    likesCountMap[postId]?.postValue(Resource.Error("Failed to get like count"))
                }
            } catch (e: Exception) {
                Log.e("CountLikes", "Error getting like count: ${e.message}")
                likesCountMap[postId]?.postValue(Resource.Error(e.message ?: "Unknown error"))
            }
        }
    }

    fun isLikedByUser(postId: Long, userId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = imageApiService.isLikedByUser(postId, userId)
                if(response.isSuccessful) {
                    response.body()?.let {
                        likeStatusMap[postId]?.postValue(Resource.Success(it))
                    }
                } else {
                    likeStatusMap[postId]?.postValue(Resource.Error("Failed to check like status"))
                }
            } catch (e: Exception) {
                Log.e("IsLiked", "Error checking like status: ${e.message}")
                likeStatusMap[postId]?.postValue(Resource.Error(e.message ?: "Unknown error"))
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        likeStatusMap.clear()
        likesCountMap.clear()
    }

    fun uriToFile(uri: Uri): File? {
        val fileName = getFileName(uri)
        val file = File(application.cacheDir, fileName)

        return try {
            application.contentResolver.openInputStream(uri)?.use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            file
        } catch (e: Exception) {
            null
        }
    }

    private fun getFileName(uri: Uri): String =
        application.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val index = cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)
                if (index != -1) cursor.getString(index) else null
            } else null
        } ?: "image_${System.currentTimeMillis()}.jpg"
}