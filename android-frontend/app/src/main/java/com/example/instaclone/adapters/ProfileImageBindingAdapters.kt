package com.example.instaclone.adapters

import android.graphics.BitmapFactory
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.findViewTreeLifecycleOwner
import com.bumptech.glide.Glide
import com.example.instaclone.R
import com.example.instaclone.tools.Resource
import com.example.instaclone.viemodels.ImageViewModel

object ProfileImageBindingAdapters {
    @JvmStatic
    @BindingAdapter("profileImageViewModel", "userId")
    fun ImageView.bindProfileImage(viewModel: ImageViewModel, userId: Long){
        viewModel.getProfileImage(userId)
        viewModel.profileImage.observe(this.findViewTreeLifecycleOwner()!!){ profileImage ->
            if(profileImage is Resource.Success){
                val bitmap = BitmapFactory.decodeByteArray(profileImage.data, 0, profileImage.data.size)
                Glide.with(this.context)
                    .load(bitmap)
                    .circleCrop()
                    .placeholder(R.drawable.github_logo)
                    .error(R.drawable.github_logo)
                    .into(this)
            } else {
                setImageResource(R.drawable.github_logo)
            }
        }
    }
}