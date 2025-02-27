package com.example.onepicture.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.onepicture.data.db.CommentDao
import com.example.onepicture.data.model.Comment
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class CommentRepository(private val commentDao: CommentDao) {

    private val firestore = FirebaseFirestore.getInstance()
    private val commentsCollection = firestore.collection("comments")

    // Insert comment locally and in Firebase
    suspend fun insertComment(comment: Comment) {
        // Insert into local database
        val commentId = commentDao.insertComment(comment)

        // Insert into Firebase
        val commentMap = hashMapOf(
            "id" to commentId,
            "postId" to comment.postId,
            "content" to comment.content,
            "userName" to comment.userName,
            "timestamp" to comment.timestamp
        )

        try {
            commentsCollection.add(commentMap)
        } catch (e: Exception) {
            Log.d("CommentRepository", "error saving comment in firebase")
        }
    }

    // Get comments for a specific post from both local database and Firebase
    @OptIn(DelicateCoroutinesApi::class)
    fun getCommentsForPost(postId: Int): LiveData<List<Comment>> {
        val localComments = commentDao.getCommentsForPost(postId)

        // Fetch from Firebase and update local database if new comments exist
        commentsCollection.whereEqualTo("postId", postId)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val firebaseComment = Comment(
                        id = document.getLong("id")!!.toInt(),
                        postId = document.getLong("postId")!!.toInt(),
                        content = document.getString("content")!!,
                        userName = document.getString("userName")!!,
                        timestamp = document.getLong("timestamp")!!
                    )
                    // Insert Firebase comment into the local database
                    GlobalScope.launch {
                        commentDao.insertComment(firebaseComment)
                    }
                }
            }
            .addOnFailureListener { exception ->
                // Handle failure if needed
            }

        return localComments
    }
}