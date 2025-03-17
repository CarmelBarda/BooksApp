package com.colman.mobilePostsApp.data.bookPost

import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
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

    fun getBookPostsByUserName(userName: String): LiveData<List<BookPost>> {
        return database.bookPostDao().getPostsByUserName(userName)
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

    fun getPostById(postId: String, callback: (BookPost?) -> Unit) {
        val executor = Executors.newSingleThreadExecutor()
        executor.execute {
            val post = database.bookPostDao().getPostById(postId)

            Handler(Looper.getMainLooper()).post {
                callback(post)
            }
        }
    }

    fun updatePost(postId: String, updatedFields: Map<String, Any>, callback: () -> Unit) {
        firebaseModel.updateBookPost(postId, updatedFields) { success ->
            if (success) {
                refreshAllPosts()
            }
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