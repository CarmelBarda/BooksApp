package com.example.onepicture.ui.posts

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.onepicture.data.db.AppDatabase
import com.example.onepicture.data.model.Post
import com.example.onepicture.data.repository.PostsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PostsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PostsRepository =
        PostsRepository(AppDatabase.getDatabase(application).postDao())

    private val _likedPost = MutableLiveData<Post>()
    val likedPost: LiveData<Post> get() = _likedPost

    fun likePost(post: Post) {
        viewModelScope.launch(Dispatchers.IO) {
            if (!post.isLiked) {
                repository.likePost(post.id)
                post.likes += 1
                post.isLiked = true
                _likedPost.postValue(post)
            } else {
                repository.unlikePost(post.id)
                post.likes -= 1
                post.isLiked = false
            }
        }
    }
    val posts: LiveData<List<Post>> = repository.allPosts


}