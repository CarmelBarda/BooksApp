package com.colman.mobilePostsApp.data.user

import android.net.Uri
import androidx.lifecycle.LiveData
import com.colman.mobilePostsApp.data.AppLocalDatabase
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import java.util.concurrent.Executors

class UserModel private constructor() {

    private val database = AppLocalDatabase.db
    private var usersExecutor = Executors.newSingleThreadExecutor()
    private val firebaseModel = UserFirebaseModel()

    companion object {
        val instance: UserModel = UserModel()
    }


    fun refreshAllUsers() {
        val lastUpdated: Long = User.lastUpdated

        firebaseModel.getAllUsers(lastUpdated) { list ->
            var time = lastUpdated
            for (user in list) {
                firebaseModel.getImage(user.id) { uri ->
                    usersExecutor.execute {
                        user.profileImage = uri.toString()
                        database.userDao().insert(user)
                    }
                }

                user.lastUpdated?.let {
                    if (time < it)
                        time = user.lastUpdated ?: System.currentTimeMillis()
                }
                User.lastUpdated = time
            }
        }
    }

    fun updateUser(user: User, selectedImageUri: Uri, success: () -> Unit, failure: () -> Unit) {
        firebaseModel.updateUser(user) { isSuccess ->
            if (isSuccess) {
                firebaseModel.addUserImage(user.id, selectedImageUri) { downloadUrl ->
                    user.profileImage = downloadUrl
                    refreshAllUsers()
                    success()
                }
            } else {
                failure()
            }
        }
    }

    fun addUser(user: User, selectedImageUri: Uri, callback: () -> Unit) {
        firebaseModel.addUser(user) {
            firebaseModel.addUserImage(user.id, selectedImageUri) { downloadUrl ->
                user.profileImage = downloadUrl
                refreshAllUsers()
                callback()
            }
        }
    }
}