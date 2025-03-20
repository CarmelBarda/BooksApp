package com.colman.mobilePostsApp.viewModels

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.colman.mobilePostsApp.data.user.User
import com.colman.mobilePostsApp.data.user.UserModel
import com.google.firebase.auth.FirebaseAuth

class UserViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val userModel = UserModel.instance

    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> get() = _currentUser

    init {
        loadCurrentUser()
    }

    fun loadCurrentUser() {
        val firebaseUser = auth.currentUser
        firebaseUser?.let {
            _currentUser.value = User(it.uid, it.displayName ?: "", it.photoUrl?.toString())
        }
    }

    fun register(name: String, email: String, password: String, selectedImageUri: Uri?, callback: (Boolean) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val user = result.user
                if (user != null) {
                    val userId = user.uid
                    if (selectedImageUri != null) {
                        userModel.addUser(User(userId, name), selectedImageUri) {
                            loadCurrentUser()
                            callback(true)
                        }
                    } else {
                        userModel.addUser(User(userId, name), Uri.EMPTY) {
                            loadCurrentUser()
                            callback(true)
                        }
                    }
                } else {
                    callback(false)
                }
            }
            .addOnFailureListener {
                callback(false)
            }
    }

    fun login(email: String, password: String, callback: (Boolean) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                loadCurrentUser()
                callback(true)
            }
            .addOnFailureListener {
                callback(false)
            }
    }

    fun updateProfile(name: String, selectedImageUri: Uri?, callback: (Boolean) -> Unit) {
        val user = _currentUser.value ?: return

        if (selectedImageUri != null) {
            userModel.updateUserImage(user.id, selectedImageUri) {
                val updatedUser = User(user.id, name, selectedImageUri.toString())
                userModel.updateUser(updatedUser) {
                    loadCurrentUser()
                    callback(true)
                }
            }
        } else {
            val updatedUser = User(user.id, name, user.profileImage)
            userModel.updateUser(updatedUser) {
                loadCurrentUser()
                callback(true)
            }
        }
    }

    fun logout() {
        auth.signOut()
        _currentUser.value = null
    }
}
