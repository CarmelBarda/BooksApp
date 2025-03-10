package com.colman.mobilePostsApp.data.repository

import androidx.lifecycle.LiveData
import com.colman.mobilePostsApp.data.db.UserDao
import com.colman.mobilePostsApp.data.model.User

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