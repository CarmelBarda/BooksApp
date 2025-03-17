package com.colman.mobilePostsApp.data.bookPost

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface BookPostDAO {
    @Query("SELECT * FROM bookPost WHERE id = :postId")
    fun getPostById(postId: String): LiveData<BookPost>

    @Query("SELECT * FROM bookPost WHERE userId = :userId")
    fun getPostsByUserId(userId: String): LiveData<List<BookPost>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(bookPost: BookPost)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(bookPosts: List<BookPost>)

    @Delete
    fun delete(bookPost: BookPost)
}
