package com.example.onepicture.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "post_table")
data class Post(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val imageUrl: String,
    val location: String,
    var likes: Int,
    val comments: Int,
    val timestamp: Long,
    val postDescription: String,
    var isLiked: Boolean
)