package com.example.instaclone.adapters

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.instaclone.R

class PhotoAdapter(
    private val onPhotoSelected: (String) -> Unit
) : ListAdapter<String, PhotoAdapter.PhotoViewHolder>(PhotoDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_photo, parent, false)
        return PhotoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val photoPath = getItem(position)
        holder.bind(photoPath)

        Log.d("PhotoAdapter", "Binding photo at position $position: $photoPath")
    }

    inner class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.photoImage)

        fun bind(photoPath: String) {

            Log.d("PhotoAdapter", "Loading image: $photoPath")

            Glide.with(itemView.context)
                .load(photoPath)
                .centerCrop()
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.e("PhotoAdapter", "Failed to load image: $photoPath", e)
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable,
                        model: Any,
                        target: Target<Drawable>,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.d("PhotoAdapter", "Successfully loaded image: $photoPath")
                        return false
                    }
                })

                .into(imageView)

            itemView.setOnClickListener {
                onPhotoSelected(photoPath)
            }
        }
    }
}

class PhotoDiffCallback : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }
}