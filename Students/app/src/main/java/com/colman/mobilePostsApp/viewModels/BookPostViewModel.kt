package com.colman.mobilePostsApp.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.ViewModel
import com.colman.mobilePostsApp.data.bookPost.BookPost
import com.colman.mobilePostsApp.data.bookPost.BookPostModel

class BookPostViewModel : ViewModel() {
    private val bookPostModel = BookPostModel.instance

    fun getPosts(userName: String?): LiveData<List<BookPost>> {
        val allPosts = bookPostModel.getAllBookPosts()

        return if (userName == null) {
            allPosts
        } else {
            liveData {
                val allPosts = bookPostModel.getAllBookPosts()
                emit(allPosts.value?.filter { it.userName == userName } ?: emptyList())
            }
        }
    }

    fun refreshPosts() {
        bookPostModel.refreshAllPosts()
    }
}
