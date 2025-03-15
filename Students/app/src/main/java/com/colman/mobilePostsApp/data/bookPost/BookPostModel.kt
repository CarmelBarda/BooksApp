package com.colman.mobilePostsApp.data.bookPost

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import com.colman.mobilePostsApp.data.AppLocalDatabase
import java.util.concurrent.Executors

class BookPostModel private constructor() {

    private val database = AppLocalDatabase.db
    private var postsExecutor = Executors.newSingleThreadExecutor()
    private val firebaseModel = BookPostFirebaseModel()
    private val posts: LiveData<List<BookPost>> = database.bookPostDao().getAllPosts()


    companion object {
        val instance: BookPostModel = BookPostModel()
    }

    fun interface SaveImageListener {
        fun onComplete(url: String)
    }

    fun getAllBookPosts(): LiveData<List<BookPost>> {
        if (posts.value == null) {
            refreshAllPosts()
        }
        return posts
    }

    fun refreshAllPosts() {
        val lastUpdated: Long = BookPost.lastUpdated

        firebaseModel.getAllBookPosts() { list ->
            if (list.isNotEmpty()) {
                postsExecutor.execute {
                    database.bookPostDao().insertAll(list)

                    val latestUpdateTime = list.maxOfOrNull { it.lastUpdated ?: 0 } ?: lastUpdated
                    BookPost.lastUpdated = latestUpdateTime
                }
            }
        }
    }


    fun updatePost(bookPost: BookPost?, callback: () -> Unit) {
        firebaseModel.updateBookPost(bookPost) {
            refreshAllPosts()
            callback()
        }
    }

    fun saveBookImage(imageBitmap: Bitmap, imageName: String, listener: SaveImageListener) {
        firebaseModel.addBookImage(imageBitmap, imageName, listener)
    }

    fun addPost(bookPost: BookPost, callback: () -> Unit) {
        firebaseModel.addPost(bookPost) {
            refreshAllPosts()
            callback()
        }
    }
}