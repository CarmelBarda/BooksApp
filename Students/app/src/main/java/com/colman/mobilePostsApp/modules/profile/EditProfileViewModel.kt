package com.colman.mobilePostsApp.modules.profile

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.colman.mobilePostsApp.data.user.User
import com.colman.mobilePostsApp.data.user.UserModel
import com.google.firebase.Firebase
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.auth

class EditProfileViewModel : ViewModel() {
    val userId = Firebase.auth.currentUser!!.uid
    var imageChanged = false
    var selectedImageURI: MutableLiveData<Uri> = MutableLiveData()
    var user: LiveData<User> = UserModel.instance.getCurrentUser()

    var name: String? = null
    var nameError = MutableLiveData("")

    fun loadUser() {
        user = UserModel.instance.getCurrentUser()

        UserModel.instance.getUserImage(userId) {
            selectedImageURI.postValue(it)
        }
    }

    fun updateUser(
        updatedUserCallback: () -> Unit
    ) {
        if (validateUserUpdate()) {
            val updatedUser = User(
                userId,
                name!!
            )

            UserModel.instance.updateUser(updatedUser) {
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setPhotoUri(selectedImageURI.value!!)
                    .setDisplayName("$name")
                    .build()

                Firebase.auth.currentUser!!.updateProfile(profileUpdates).addOnSuccessListener {
                    if (imageChanged) {
                        UserModel.instance.updateUserImage(userId, selectedImageURI.value!!) {
                            updatedUserCallback()
                        }
                    } else {
                        updatedUserCallback()
                    }
                }
            }
        }
    }

    private fun validateUserUpdate(
    ): Boolean {
        if (name.isNullOrEmpty()) {
            nameError.postValue("Name cannot be empty")
            return false
        }
        return true
    }
}
