package com.example.instaclone.viemodels

import android.app.Application
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

data class PhotoUiState(
    val photos: List<String> = emptyList(),
    val selectedPhoto: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class PhotoPickerViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState = MutableLiveData(PhotoUiState())
    val uiState: LiveData<PhotoUiState> = _uiState

    init {
        loadPhotos()
    }

    private fun loadPhotos() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _uiState.postValue(_uiState.value?.copy(isLoading = true))

                val photos = mutableListOf<String>()
                val projection = arrayOf(MediaStore.Images.Media.DATA)

                getApplication<Application>().contentResolver.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    null,
                    null,
                    "${MediaStore.Images.Media.DATE_ADDED} DESC"
                )?.use { cursor ->
                    val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                    while (cursor.moveToNext()) {
                        photos.add(cursor.getString(columnIndex))
                    }
                }

                _uiState.postValue(
                    _uiState.value?.copy(
                        photos = photos,
                        isLoading = false,
                        error = null
                    )
                )
            } catch (e: Exception) {
                _uiState.postValue(
                    _uiState.value?.copy(
                        isLoading = false,
                        error = "Failed to load photos: ${e.message}"
                    )
                )
            }
        }
    }

    fun selectPhoto(photoPath: String) {
        _uiState.value = _uiState.value?.copy(selectedPhoto = photoPath)
    }

    fun retryLoading() {
        loadPhotos()
    }
}