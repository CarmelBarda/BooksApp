package com.colman.mobilePostsApp.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.colman.mobilePostsApp.data.bookPost.BookPost
import com.colman.mobilePostsApp.data.bookPost.BookPostModel

class BookPostViewModel : ViewModel() {
    private val bookPostModel = BookPostModel.instance

    fun getPosts(userName: String?): LiveData<List<BookPost>> {
        return if (userName == null) {
            bookPostModel.getAllBookPosts()
        } else {
            bookPostModel.getBookPostsByUserName(userName)
        }
    }

    fun refreshPosts() {
        bookPostModel.refreshAllPosts()
    }
}
