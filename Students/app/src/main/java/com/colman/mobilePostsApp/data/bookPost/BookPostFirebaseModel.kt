package com.colman.mobilePostsApp.data.bookPost

import android.graphics.Bitmap
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.io.ByteArrayOutputStream

class BookPostFirebaseModel {

    private val db = Firebase.firestore
    private var storage: FirebaseStorage

    companion object {
        const val POSTS_COLLECTION_PATH = "posts"
    }

    init {
        val settings: FirebaseFirestoreSettings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()
        db.firestoreSettings = settings
        storage = FirebaseStorage.getInstance()
    }

    fun getAllBookPosts(callback: (List<BookPost>) -> Unit) {
        db.collection(POSTS_COLLECTION_PATH)
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
    ) {
        val storageRef: StorageReference = storage.reference
        val imgRef: StorageReference = storageRef.child("books/$imageName")
        val baos = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        val uploadTask: UploadTask = imgRef.putBytes(data)
        uploadTask.addOnFailureListener { exception ->
            listener.onComplete(null.toString())
            Log.e("FirebaseUpload", "Failed to get download URL", exception)
        }
            .addOnSuccessListener {
                imgRef.downloadUrl
                    .addOnSuccessListener { uri ->
                        listener.onComplete(uri.toString()) }
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
