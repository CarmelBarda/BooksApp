package com.colman.mobilePostsApp.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.colman.mobilePostsApp.data.user.User
import com.colman.mobilePostsApp.data.user.UserModel

class UserProfileViewModel : ViewModel() {
    private val userModel = UserModel.instance

    fun getCurrentUser(): LiveData<User> {
        val user = userModel.getCurrentUser()
        return user
    }

    fun refreshUserData() {
        userModel.refreshAllUsers()
    }

    fun updateUser(user: User, callback: () -> Unit) {
        userModel.updateUser(user, callback)
    }
}
