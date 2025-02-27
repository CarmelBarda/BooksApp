package com.example.onepicture.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.onepicture.data.model.Post

@Dao
interface PostDao {
    @Query("SELECT * FROM post_table ORDER BY timestamp DESC")
    fun getPosts(): LiveData<List<Post>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(post: Post): Long

    @Query("SELECT * FROM post_table WHERE id = :postId")
    fun getPostById(postId: Int): LiveData<Post>


    @Query("UPDATE post_table SET comments = comments + 1 WHERE id = :postId")
    suspend fun incrementComments(postId: Int)

    @Query("UPDATE post_table SET likes = likes + 1 WHERE id = :postId")
    suspend fun incrementLikes(postId: Int)

    @Query("UPDATE post_table SET comments = comments - 1 WHERE id = :postId")
    suspend fun decrementComments(postId: Int)

    @Query("UPDATE post_table SET likes = likes - 1 WHERE id = :postId")
    suspend fun decrementLikes(postId: Int)
}