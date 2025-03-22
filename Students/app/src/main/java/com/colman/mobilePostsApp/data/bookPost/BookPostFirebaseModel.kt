package com.colman.mobilePostsApp.data.bookPost

import android.graphics.Bitmap
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.io.ByteArrayOutputStream
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage

class BookPostFirebaseModel {

    private val db = Firebase.firestore
    private val storage = Firebase.storage

    companion object {
        const val POSTS_COLLECTION_PATH = "posts"
    }

    fun getAllBookPosts(since: Long, callback: (List<BookPost>) -> Unit) {
        db.collection(POSTS_COLLECTION_PATH)
            .whereGreaterThanOrEqualTo(BookPost.LAST_UPDATED_KEY, Timestamp(since, 0))
            .get().addOnCompleteListener { postTask ->
                if (postTask.isSuccessful) {
                    val bookPosts = mutableListOf<BookPost>()
                    val userIds = postTask.result?.documents?.mapNotNull { it.getString("userId") }?.toSet() ?: emptySet()

                    if (userIds.isEmpty()) {
                        callback(bookPosts)
                        return@addOnCompleteListener
                    }

                    db.collection("users")
                        .whereIn("id", userIds.toList())
                        .get()
                        .addOnCompleteListener { userTask ->
                            val userMap = userTask.result?.documents?.associate {
                                it.id to Pair(it.getString("name") ?: "Unknown User", it.getString("profileImage"))
                            } ?: emptyMap()

                            for (json in postTask.result!!) {
                                val bookPost = BookPost.fromJSON(json.data!!)
                                val userData = userMap[bookPost.userId]

                                bookPost.userName = userData?.first ?: "Unknown User"
                                bookPost.userProfile = userData?.second

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

    fun updateBookPost(postId: String, updatedFields: Map<String, Any>, callback: (Boolean) -> Unit) {
        db.collection(POSTS_COLLECTION_PATH)
            .document(postId)
            .update(updatedFields)
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener {
                Log.d("Error", "Failed to update post: ${it.message}")
                callback(false)
            }
    }

    fun addPost(bookPost: BookPost, callback: () -> Unit) {
        db.collection(POSTS_COLLECTION_PATH).document(bookPost.id).set(bookPost.json)
            .addOnSuccessListener {
                callback()
            }
    }

    fun deleteBookPost(postId: String, callback: (Boolean) -> Unit) {
        db.collection(POSTS_COLLECTION_PATH)
            .document(postId)
            .delete()
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener { callback(false) }
    }

}
