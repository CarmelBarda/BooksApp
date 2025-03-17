package com.colman.mobilePostsApp.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.ViewModel
import com.colman.mobilePostsApp.data.bookPost.BookPost
import com.colman.mobilePostsApp.data.bookPost.BookPostModel

class BookPostViewModel : ViewModel() {
    private val bookPostModel = BookPostModel.instance

    fun getPosts(userId: String?): LiveData<List<BookPost>> {
        return if (userId == null) {
            bookPostModel.getAllBookPosts()
        } else {
            bookPostModel.getBookPostsByUserId(userId)
        }
    }

    fun refreshPosts() {
        bookPostModel.refreshAllPosts()
    }
}
