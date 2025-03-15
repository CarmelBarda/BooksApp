package com.colman.mobilePostsApp.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.colman.mobilePostsApp.data.bookPost.BookPost
import com.colman.mobilePostsApp.data.bookPost.BookPostModel

class BookPostViewModel : ViewModel() {
    private val bookPostModel = BookPostModel.instance

    val bookPosts: LiveData<List<BookPost>> = BookPostModel.instance.getAllBookPosts()

    fun refreshPosts() {
        bookPostModel.refreshAllPosts()
    }

//    fun addPost(bookPost: BookPost) {
//        bookPostModel.addPost(bookPost) { refreshPosts() }
//    }
}
