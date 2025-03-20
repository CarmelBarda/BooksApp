package com.colman.mobilePostsApp.data.user

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.colman.mobilePostsApp.data.AppLocalDatabase
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import java.util.concurrent.Executors

class UserModel private constructor() {

    private val database = AppLocalDatabase.db
    private var usersExecutor = Executors.newSingleThreadExecutor()
    private val firebaseModel = UserFirebaseModel()
    val loggedUser: MutableLiveData<User> = MutableLiveData()
    val mAuth: FirebaseAuth = FirebaseAuth.getInstance()


    interface GetLoggedUserListener {
        fun onComplete(user: User)
    }

    companion object {
        val instance: UserModel = UserModel()
    }

    fun getLoggedUser(): LiveData<User> {
        if (loggedUser.value == null) {
            refreshLoggedUser()
        }
        return loggedUser
    }

    private fun refreshLoggedUser() {
        return firebaseModel.getUser(mAuth.uid!!, object : GetLoggedUserListener {
            override fun onComplete(user: User) {
                loggedUser.postValue(user)
            }
        })
    }

    fun getCurrentUser(): LiveData<User> {
        return database.userDao().getUserById(Firebase.auth.currentUser?.uid!!)
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


    fun updateUser(user: User?, callback: () -> Unit) {
        firebaseModel.updateUser(user) {
            refreshLoggedUser()
            callback()
        }
    }

    fun updateUserImage(userId: String, selectedImageUri: Uri, callback: () -> Unit) {
        firebaseModel.addUserImage(userId, selectedImageUri) {
            refreshLoggedUser()
            callback()
        }
    }

    fun getUserImage(imageId: String, callback: (Uri) -> Unit) {
        firebaseModel.getImage(imageId, callback);
    }

    fun addUser(user: User, selectedImageUri: Uri, callback: () -> Unit) {
        firebaseModel.addUser(user) {
            firebaseModel.addUserImage(user.id, selectedImageUri) { downloadUrl ->
                user.profileImage = downloadUrl
                refreshLoggedUser()
                callback()
            }
        }
    }
}