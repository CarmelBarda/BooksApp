package com.example.onepicture.data.repository

import androidx.lifecycle.LiveData
import com.example.onepicture.data.db.UserDao
import com.example.onepicture.data.model.User

class UserRepository(private val userDao: UserDao) {
    suspend fun insertUser(user: User): Long {
        return userDao.insertUser(user)
    }

    suspend fun updateUser(user: User) {
        userDao.updateUser(user)
    }

    fun getUserById(userId: Int): LiveData<User> {
        return userDao.getUserById(userId)
    }

    fun getUserEmail(email: String): LiveData<User> {
        return userDao.getUserEmail(email)
    }

    fun getAllUsers(): LiveData<List<User>> {
        return userDao.getAllUsers()
    }
}