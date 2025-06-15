package com.example.instaclone.adapters

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.instaclone.R
import com.example.instaclone.models.UserPostResponse
import com.example.instaclone.tools.Resource
import com.example.instaclone.tools.SharedPreferencesManager
import com.example.instaclone.viemodels.ImageViewModel
import kotlinx.coroutines.coroutineScope

class UserPostsAdapter(
    private val prefManager: SharedPreferencesManager,
    private val imageViewModel: ImageViewModel,
    private val lifecycleOwner: LifecycleOwner
) : ListAdapter<UserPostResponse, UserPostsAdapter.PostsViewHolder>(UserDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user_posts, parent, false)
        return PostsViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostsViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
        Log.d("PhotoAdapter", "Binding photo at position $position: ${post.id}")
    }

    inner class PostsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.photoImage)
        private val countLikes: TextView = itemView.findViewById(R.id.countLikes)
        private val countComments: TextView = itemView.findViewById(R.id.countComments)
        private val likesBtn: ImageView = itemView.findViewById(R.id.likesBtn)


        private var currentPostId: Long? = null
        private var likeStatusObserver: Observer<Resource<Boolean>>? = null
        private var likesCountObserver: Observer<Resource<Int>>? = null

        fun bind(post: UserPostResponse) {
            currentPostId = post.id

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

            countComments.text = post.comments.size.toString()

            val userId = prefManager.getUserId()?.toLong() ?: return

            removeObservers()

            likesCountObserver = Observer { state ->
                if (state is Resource.Success) {
                    countLikes.text = state.data.toString()
                    Log.d("PhotoAdapter", "Updated likes count for post ${post.id}: ${state.data}")
                }
            }

            likeStatusObserver = Observer { state ->
                if (state is Resource.Success) {
                    val newDrawable = if (state.data) {
                        R.drawable.rounded_heart_check_24
                    } else {
                        R.drawable.rounded_heart_plus_24
                    }
                    likesBtn.setImageResource(newDrawable)
                    Log.d("PhotoAdapter", "Updated like status for post ${post.id}: ${state.data}")
                }
            }

            val postLikeStatus = imageViewModel.getLikeStatus(post.id)
            val postLikesCount = imageViewModel.getLikesCount(post.id)

            postLikeStatus.observe(lifecycleOwner, likeStatusObserver!!)
            postLikesCount.observe(lifecycleOwner, likesCountObserver!!)


            imageViewModel.getCountLikes(post.id)
            imageViewModel.isLikedByUser(post.id, userId)

            likesBtn.setOnClickListener {
                val currentState = postLikeStatus.value
                if (currentState is Resource.Success) {
                    if (currentState.data) {
                        Log.d("PhotoAdapter", "Unliking post ${post.id}")
                        imageViewModel.unlikePost(post.id, userId)
                    } else {
                        Log.d("PhotoAdapter", "Liking post ${post.id}")
                        imageViewModel.likePost(post.id, userId)
                    }
                }
            }
        }

        fun removeObservers() {
            currentPostId?.let { postId ->
                likeStatusObserver?.let { observer ->
                    imageViewModel.getLikeStatus(postId).removeObserver(observer)
                }

                likesCountObserver?.let { observer ->
                    imageViewModel.getLikesCount(postId).removeObserver(observer)
                }
            }

            likeStatusObserver = null
            likesCountObserver = null
        }
    }

    override fun onViewRecycled(holder: PostsViewHolder) {
        super.onViewRecycled(holder)
        holder.removeObservers()
    }
}
class UserDiffCallback : DiffUtil.ItemCallback<UserPostResponse>() {
    override fun areItemsTheSame(oldItem: UserPostResponse, newItem: UserPostResponse): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: UserPostResponse, newItem: UserPostResponse): Boolean {
        return oldItem == newItem
    }
}