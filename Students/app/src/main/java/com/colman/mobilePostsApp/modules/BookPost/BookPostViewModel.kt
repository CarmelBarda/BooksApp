package com.colman.mobilePostsApp.modules.BookPost

import androidx.lifecycle.LiveData
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

    fun refreshPosts(updatedOnly: Boolean = true) {
        bookPostModel.refreshAllPosts(updatedOnly)
    }
}
