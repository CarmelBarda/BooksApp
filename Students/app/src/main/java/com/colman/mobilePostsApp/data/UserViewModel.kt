package com.colman.mobilePostsApp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.colman.mobilePostsApp.data.db.AppDatabase
import com.colman.mobilePostsApp.data.model.User
import com.colman.mobilePostsApp.data.sync.FirestoreSync
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val userDao = AppDatabase.getDatabase(application).userDao()
    private val firestoreSync = FirestoreSync(application)

    val allUsers: LiveData<List<User>> = userDao.getAllUsers()

    // ✅ Add user locally (Room) and push to Firestore
    fun addUser(user: User) {
        viewModelScope.launch {
            userDao.insertUser(user)
            firestoreSync.syncUsersToFirestore(listOf(user))  // Push to Firestore
        }
    }

    // ✅ Fetch user by ID
    fun getUserById(userId: Int): LiveData<User> {
        return userDao.getUserById(userId)
    }
}
