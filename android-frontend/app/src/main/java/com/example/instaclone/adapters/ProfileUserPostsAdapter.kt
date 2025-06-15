package com.example.instaclone.adapters

import android.content.Context
import android.content.Intent
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
import com.example.instaclone.activities.PostsActivity
import com.example.instaclone.models.UserPostResponse

class ProfileUserPostsAdapter(val context: Context) : ListAdapter<UserPostResponse, ProfileUserPostsAdapter.PostsViewHolder>(UserDiffCallback()){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_photo, parent, false)
        return PostsViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostsViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
        holder.itemView.setOnClickListener {
            context.startActivity(Intent(context, PostsActivity::class.java))
        }
        Log.d("PhotoAdapter", "Binding photo at position $position: ${post.id}")
    }

    inner class PostsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val imageView: ImageView = itemView.findViewById(R.id.photoImage)
        fun bind(post: UserPostResponse){
            Log.d("PhotoAdapter", "Loading image: ${post.id}")

            val imageUrl = if (!post.data.startsWith("data:")) {
                "data:${post.contentType};base64,${post.data}"
            } else {
                post.data
            }
            Glide.with(itemView.context)
                .load(imageUrl)
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
                        Log.e("PhotoAdapter", "Failed to load image: ${post.id}", e)
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable,
                        model: Any,
                        target: Target<Drawable>,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.d("PhotoAdapter", "Successfully loaded image: ${post.id}")
                        return false
                    }
                }).into(imageView)

        }
    }
}
class PostsDiffCallback : DiffUtil.ItemCallback<UserPostResponse>() {
    override fun areItemsTheSame(oldItem: UserPostResponse, newItem: UserPostResponse): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: UserPostResponse, newItem: UserPostResponse): Boolean {
        return oldItem == newItem
    }
}