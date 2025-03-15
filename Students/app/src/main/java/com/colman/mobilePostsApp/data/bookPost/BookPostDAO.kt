package com.colman.mobilePostsApp.data.bookPost

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface BookPostDAO {
    @Query("SELECT * FROM bookPost ORDER BY lastUpdated DESC")
    fun getAllPosts(): LiveData<List<BookPost>>

    @Query("SELECT * FROM bookPost WHERE id = :postId")
    fun getPostById(postId: String): BookPost?

    @Update
    fun update(post: BookPost)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(bookPost: BookPost)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(bookPosts: List<BookPost>)

    @Delete
    fun delete(bookPost: BookPost)
}