package com.example.onepicture.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.onepicture.data.model.Comment

@Dao
interface CommentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: Comment) : Long

    @Query("SELECT * FROM comments_table WHERE postId = :postId ORDER BY timestamp ASC")
    fun getCommentsForPost(postId: Int): LiveData<List<Comment>>
}