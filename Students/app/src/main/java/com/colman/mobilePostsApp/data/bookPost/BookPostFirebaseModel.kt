package com.colman.mobilePostsApp.data.bookPost

import android.graphics.Bitmap
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.firestoreSettings
import com.google.firebase.firestore.memoryCacheSettings
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.storage
import java.io.ByteArrayOutputStream

class BookPostFirebaseModel {

    private val db = Firebase.firestore
    private val storage = Firebase.storage

    companion object {
        const val POSTS_COLLECTION_PATH = "posts"
    }

    init {
        val settings = firestoreSettings {
            setLocalCacheSettings(memoryCacheSettings { })
        }
        db.firestoreSettings = settings
    }

    fun getAllBookPosts(since: Long, callback: (List<BookPost>) -> Unit) {
        db.collection(POSTS_COLLECTION_PATH)
            .whereGreaterThanOrEqualTo(BookPost.LAST_UPDATED_KEY, Timestamp(since, 0))
            .get().addOnCompleteListener {
                when (it.isSuccessful) {
                    true -> {
                        val bookPosts: MutableList<BookPost> = mutableListOf()
                        for (json in it.result) {
                            val bookPost = BookPost.fromJSON(json.data)
                            bookPosts.add(bookPost)
                        }
                        callback(bookPosts)
                    }

                    false -> callback(listOf())
                }
            }
    }

    fun addBookImage(
        imageBitmap: Bitmap,
        imageName: String,
        listener: BookPostModel.SaveImageListener,
        directory: String
    ) {
        val storageRef: StorageReference = storage.reference
        val imgRef: StorageReference = storageRef.child("$directory/$imageName")
        val baos = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        val uploadTask: UploadTask = imgRef.putBytes(data)
        uploadTask.addOnFailureListener { listener.onComplete(null.toString()) }
            .addOnSuccessListener {
                imgRef.downloadUrl
                    .addOnSuccessListener { uri -> listener.onComplete(uri.toString()) }
            }
    }


    fun updateBookPost(bookPost: BookPost?, callback: () -> Unit) {
        db.collection(POSTS_COLLECTION_PATH)
            .document(bookPost!!.id).update(bookPost.updateJson)
            .addOnSuccessListener {
                callback()
            }.addOnFailureListener {
                Log.d("Error", "Can't update this post document: " + it.message)
            }
    }

    fun addPost(bookPost: BookPost, callback: () -> Unit) {
        db.collection(POSTS_COLLECTION_PATH).document(bookPost.id).set(bookPost.json)
            .addOnSuccessListener {
                callback()
            }
    }
}
