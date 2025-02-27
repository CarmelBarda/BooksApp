package com.example.onepicture.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.onepicture.data.db.PostDao
import com.example.onepicture.data.model.Post
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class PostsRepository(private val postDao: PostDao) {

    private val firestore = FirebaseFirestore.getInstance()
    private val postsCollection = firestore.collection("posts")

    val allPosts: LiveData<List<Post>> = postDao.getPosts()

    init {
        // Initialize the repository by syncing all posts from Firebase
        syncAllPostsFromFirebase()
    }

    suspend fun insert(post: Post) {
        // Insert into the local database
        val newPostId = postDao.insert(post)

        // Save the post to Firebase
        savePostToFirebase(post, newPostId)
    }

    fun getPost(postId: Int): LiveData<Post> {
        mergePostFromFirebase(postId)
        return postDao.getPostById(postId)
    }

    suspend fun likePost(postId: Int) {
        // Increment likes locally
        postDao.incrementLikes(postId)

        // Update the likes in Firebase
        updatePostValueInFirebase(postId, "likes", increment = true)
    }

    suspend fun unlikePost(postId: Int) {
        // Decrement likes locally
        postDao.decrementLikes(postId)

        // Update the likes in Firebase
        updatePostValueInFirebase(postId, "likes", increment = false)
    }

    suspend fun addCommentCount(postId: Int) {
        // Increment comments locally
        postDao.incrementComments(postId)

        // Update the comment count in Firebase
        updatePostValueInFirebase(postId, "comments", increment = true)
    }

    // Save a post to Firebase
    private fun savePostToFirebase(post: Post, newPostId: Long) {
        val postMap = hashMapOf(
            "id" to newPostId,
            "imageUrl" to post.imageUrl,
            "location" to post.location,
            "likes" to post.likes,
            "comments" to post.comments,
            "timestamp" to post.timestamp,
            "postDescription" to post.postDescription,
            "isLiked" to post.isLiked
        )

        postsCollection.document(post.id.toString()).set(postMap)
            .addOnSuccessListener {
                Log.d(TAG, "Post successfully saved to Firebase!")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error saving post to Firebase: ${e.message}")
            }
    }

    // Update a specific value in Firebase
    private fun updatePostValueInFirebase(postId: Int, field: String, increment: Boolean) {
        val postRef = postsCollection.document(postId.toString())

        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(postRef)
            val newValue = if (increment) {
                snapshot.getLong(field)!! + 1
            } else {
                snapshot.getLong(field)!! - 1
            }
            transaction.update(postRef, field, newValue)
        }.addOnSuccessListener {
            Log.d(TAG, "Post $field successfully updated in Firebase!")
        }.addOnFailureListener { e ->
            Log.e(TAG, "Error updating post $field in Firebase: ${e.message}")
        }
    }

    private fun mergePostFromFirebase(postId: Int) {
        postsCollection.document(postId.toString()).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val firebasePost = Post(
                        id = document.getLong("id")!!.toInt(),
                        imageUrl = document.getString("imageUrl")!!,
                        location = document.getString("location")!!,
                        likes = document.getLong("likes")!!.toInt(),
                        comments = document.getLong("comments")!!.toInt(),
                        timestamp = document.getLong("timestamp")!!,
                        postDescription = document.getString("postDescription")!!,
                        isLiked = document.getBoolean("isLiked")!!
                    )
                    // Insert or update the post in the local database
                    GlobalScope.launch {
                        postDao.insert(firebasePost)
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error fetching post from Firebase: ${e.message}")
            }
    }

    // Sync all posts from Firebase with the local database
    @OptIn(DelicateCoroutinesApi::class)
    private fun syncAllPostsFromFirebase() {
        postsCollection.get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val firebasePost = Post(
                        id = document.getLong("id")!!.toInt(),
                        imageUrl = document.getString("imageUrl")!!,
                        location = document.getString("location")!!,
                        likes = document.getLong("likes")!!.toInt(),
                        comments = document.getLong("comments")!!.toInt(),
                        timestamp = document.getLong("timestamp")!!,
                        postDescription = document.getString("postDescription")!!,
                        isLiked = document.getBoolean("isLiked")!!
                    )
                    // Insert or update the post in the local database
                    GlobalScope.launch {
                        postDao.insert(firebasePost)
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error fetching posts from Firebase: ${e.message}")
            }
    }

    companion object {
        private const val TAG = "PostsRepository"
    }
}
