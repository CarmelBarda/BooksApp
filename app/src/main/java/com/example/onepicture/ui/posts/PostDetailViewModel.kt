package com.example.onepicture.ui.posts

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.onepicture.data.db.AppDatabase
import com.example.onepicture.data.model.Comment
import com.example.onepicture.data.model.Post
import com.example.onepicture.data.repository.CommentRepository
import com.example.onepicture.data.repository.PostsRepository

class PostDetailViewModel(
    application: Application,
    postId: Int
) : ViewModel() {
    private val postRepository = PostsRepository(AppDatabase.getDatabase(application).postDao())
    private val commentRepository =
        CommentRepository(AppDatabase.getDatabase(application).commentDao())
    private val _post = MutableLiveData<Post>()
    val post: LiveData<Post> get() = _post

    val comments: LiveData<List<Comment>> = commentRepository.getCommentsForPost(postId)

    init {
        loadPost(postId)
    }

    private fun loadPost(postId: Int) {
        // Fetch the post by postId
        postRepository.getPost(postId).observeForever { post ->
            _post.value = post
        }
    }

    suspend fun addComment(comment: Comment) {
        commentRepository.insertComment(comment)
        postRepository.addCommentCount(comment.postId)
    }

    companion object {
        fun provideFactory(
            application: Application,
            postId: Int,
        ): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(PostDetailViewModel::class.java)) {
                        return PostDetailViewModel(
                            application = application,
                            postId = postId
                        ) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
        }
    }
}
