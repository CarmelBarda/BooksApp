package com.colman.mobilePostsApp.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.colman.mobilePostsApp.data.bookPost.BookPost
import com.colman.mobilePostsApp.data.bookPost.BookPostModel

class BookPostViewModel : ViewModel() {
    private val bookPostModel = BookPostModel.instance

    // Fetch posts from Firebase via the model
    val bookPosts: LiveData<List<BookPost>> = bookPostModel.getAllBookPosts()

    fun refreshPosts() {
        bookPostModel.refreshAllPosts() // Fetch fresh data
    }

//    fun addPost(bookPost: BookPost) {
//        bookPostModel.addPost(bookPost) { refreshPosts() }
//    }
}
