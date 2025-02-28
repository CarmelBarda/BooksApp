package com.example.onepicture.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.onepicture.data.model.User

@Dao
interface UserDao {
    @Insert
    suspend fun insertUser(user: User): Long

    @Update
    suspend fun updateUser(user: User)

    @Query("SELECT * FROM users_table WHERE id = :userId")
    fun getUserById(userId: Int): LiveData<User>

    @Query("SELECT * FROM users_table WHERE email = :email")
    fun getUserEmail(email: String): LiveData<User>

    @Query("SELECT * FROM users_table")
    fun getAllUsers(): LiveData<List<User>>
}