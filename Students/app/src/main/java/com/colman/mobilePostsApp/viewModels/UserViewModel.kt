package com.colman.mobilePostsApp.viewModels

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.colman.mobilePostsApp.data.user.User
import com.colman.mobilePostsApp.data.user.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class UserViewModel : ViewModel() {
    private val userModel = UserModel.instance
    private val auth = FirebaseAuth.getInstance()

    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> get() = _currentUser

    init {
        loadCurrentUser()
    }

    private fun loadCurrentUser() {
        val firebaseUser = auth.currentUser
        firebaseUser?.let {
            _currentUser.value = User(it.uid, it.displayName ?: "", it.photoUrl?.toString())
        }
    }

    fun login(email: String, password: String, callback: (Boolean) -> Unit) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                loadCurrentUser()
                callback(true)
            } else {
                callback(false)
            }
        }
    }

    fun register(name: String, email: String, password: String, imageUri: Uri?, callback: (Boolean) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener { result ->
            val user = result.user
            if (user != null) {
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .setPhotoUri(imageUri)
                    .build()

                user.updateProfile(profileUpdates).addOnCompleteListener {
                    userModel.addUser(User(user.uid, name), imageUri ?: Uri.EMPTY) {
                        loadCurrentUser()
                        callback(true)
                    }
                }
            }
        }.addOnFailureListener {
            callback(false)
        }
    }

    fun updateProfile(name: String, imageUri: Uri?, callback: (Boolean) -> Unit) {
        val user = auth.currentUser
        if (user != null) {
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .setPhotoUri(imageUri)
                .build()

            user.updateProfile(profileUpdates).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    userModel.updateUser(User(user.uid, name, imageUri?.toString())) {
                        loadCurrentUser()
                        callback(true)
                    }
                } else {
                    callback(false)
                }
            }
        }
    }

    fun logout() {
        auth.signOut()
        _currentUser.value = null
    }
}
