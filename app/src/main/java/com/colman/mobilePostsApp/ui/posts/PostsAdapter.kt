package com.example.onepicture.ui.posts

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.onepicture.R
import com.example.onepicture.data.model.Post
import java.io.File
import kotlin.random.Random

class PostsAdapter(
    private val onPostClick: (Post) -> Unit,
    private val onLikeClick: (Post) -> Unit
) :
    ListAdapter<Post, PostsAdapter.PostViewHolder>(PostDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post, onPostClick, onLikeClick)
    }

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val postImageView: ImageView = itemView.findViewById(R.id.post_image)
        private val likesTextView: TextView = itemView.findViewById(R.id.like_count)
        private val commentsCountTextView: TextView = itemView.findViewById(R.id.post_comment_count)
        private val postDescriptionTextView: TextView = itemView.findViewById(R.id.post_description)
        private val postLocationTextView: TextView = itemView.findViewById(R.id.post_location)
        private val likeButton: ImageView = itemView.findViewById(R.id.like_button)
        private val likeCountTextView: TextView = itemView.findViewById(R.id.like_count)

        fun bind(post: Post, onPostClick: (Post) -> Unit, onLikeClick: (Post) -> Unit) {
            val random: Random = Random (10)
            val file = File(post.imageUrl)
            val uri = Uri.fromFile(file)
            postImageView.setImageURI(uri)

            postDescriptionTextView.text = "${post.postDescription}"
            likesTextView.text = "${post.likes} likes"
            commentsCountTextView.text = "${post.comments} comments"
            postLocationTextView.text = "${post.location}"


            likeCountTextView.text = random.nextInt().toString()

            if (post.isLiked) {
                likeButton.setImageResource(R.drawable.ic_like_red)
            } else {
                likeButton.setImageResource(R.drawable.ic_like)
            }

            // Handle like button click
            likeButton.setOnClickListener {
                onLikeClick(post)
            }

            itemView.setOnClickListener {
                onPostClick(post)
            }
        }
    }

    class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem == newItem
        }
    }
}
