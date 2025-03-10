package com.colman.mobilePostsApp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "users")
data class User(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val email: String = "",
    val profilePicture: ByteArray? = null,
    val lastUpdated: Long = System.currentTimeMillis()
) {
    // ✅ No-argument constructor required for Firestore
    constructor() : this("", "", "", null, 0L)
}
