package com.colman.mobilePostsApp.data.bookPost

import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import com.colman.mobilePostsApp.data.AppLocalDatabase
import java.text.SimpleDateFormat
import java.util.Locale
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

    fun getBookPostsByUserId(userId: String): LiveData<List<BookPost>> {
        return database.bookPostDao().getPostsByUserId(userId)
    }

    fun refreshAllPosts(updatedOnly: Boolean = true) {
        // if you want to get all books no matter what
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val initlalDate = formatter.parse("01/01/2025")
        val initalDateTimeStamp = (initlalDate?.time ?: 0) / 1000

        val lastUpdated: Long = if (updatedOnly) BookPost.lastUpdated else initalDateTimeStamp

        firebaseModel.getAllBookPosts(lastUpdated) { list ->
            var latestUpdateTime = lastUpdated

            if (list.isNotEmpty()) {
                postsExecutor.execute {
                    database.bookPostDao().insertAll(list)

                    for (post in list) {
                        post.lastUpdated?.let {
                            if (latestUpdateTime < it) {
                                latestUpdateTime = it
                            }
                        }
                    }
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

    fun deletePost(postId: String, callback: (Boolean) -> Unit) {
        firebaseModel.deleteBookPost(postId) { success ->
            if (success) {
                postsExecutor.execute {
                    database.bookPostDao().deletePostById(postId)
                }
            }
            callback(success)
        }
    }

}