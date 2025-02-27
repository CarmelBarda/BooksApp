package com.example.onepicture.ui.preview

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.onepicture.data.db.AppDatabase
import com.example.onepicture.data.model.Post
import com.example.onepicture.data.repository.PostsRepository
import kotlinx.coroutines.launch

class PreviewViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PostsRepository =
        PostsRepository(AppDatabase.getDatabase(application).postDao())
    private val _postSuccess = MutableLiveData<Boolean>()
    val postSuccess: LiveData<Boolean> get() = _postSuccess


    fun postImageWithDescription(
        imagePath: String,
        description: String,
        location: String
    ) {
        viewModelScope.launch {
            try {
                // Create a Post object
                val post = Post(
                    imageUrl = imagePath,
                    likes = 0,
                    comments = 0,
                    postDescription = description,
                    timestamp = System.currentTimeMillis(),
                    isLiked = false,
                    location = location
                )

                // Call the repository to insert the post
                repository.insert(post)

                _postSuccess.value = true
                // Optionally notify the UI about the success
            } catch (e: Exception) {
                _postSuccess.value = false
                // Handle the error
                Log.e("PreviewViewModel", "Error posting image", e)
            }
        }
    }
}