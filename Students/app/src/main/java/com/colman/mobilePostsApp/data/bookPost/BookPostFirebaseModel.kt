package com.colman.mobilePostsApp.data.bookPost

import android.graphics.Bitmap
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.io.ByteArrayOutputStream

class BookPostFirebaseModel {

    private val db: FirebaseFirestore
    private var storage: FirebaseStorage

    companion object {
        const val POSTS_COLLECTION_PATH = "posts"

        private val firestoreInstance: FirebaseFirestore by lazy {
            FirebaseFirestore.getInstance().apply {
                try {
                    val settings = FirebaseFirestoreSettings.Builder()
                        .setPersistenceEnabled(true)
                        .build()
                    firestoreSettings = settings
                } catch (e: IllegalStateException) {
                    Log.w("Firestore", "Firestore settings already set. Skipping update.")
                }
            }
        }
    }

    init {
        db = firestoreInstance
        storage = FirebaseStorage.getInstance()
    }

    fun getAllBookPosts(callback: (List<BookPost>) -> Unit) {
        db.collection(POSTS_COLLECTION_PATH)
            .get().addOnCompleteListener { postTask ->
                if (postTask.isSuccessful) {
                    val bookPosts = mutableListOf<BookPost>()
                    val userIds = postTask.result?.documents?.mapNotNull { it.getString("userId") }?.toSet() ?: emptySet()

                    db.collection("users")
                        .whereIn("id", userIds.toList())
                        .get()
                        .addOnCompleteListener { userTask ->
                            val userMap = userTask.result?.documents?.associate { it.id to it.getString("userName") } ?: emptyMap()

                            for (json in postTask.result!!) {
                                val bookPost = BookPost.fromJSON(json.data!!)
                                bookPost.userName = userMap[bookPost.userId] ?: "Unknown User" // Assign dynamically
                                bookPosts.add(bookPost)
                            }
                            callback(bookPosts)
                        }
                } else {
                    callback(emptyList())
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
