package com.example.onepicture.ui.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.onepicture.data.db.AppDatabase
import com.example.onepicture.data.model.User
import com.example.onepicture.data.repository.UserRepository

class ProfileViewModel(application: Application, userId: Int) : AndroidViewModel(application) {

    private val userRepository: UserRepository =
        UserRepository(AppDatabase.getDatabase(application).userDao())
    val user: LiveData<User> = userRepository.getUserById(userId)

    suspend fun updateUser(user: User) {
        userRepository.updateUser(user)
    }

    companion object {
        fun provideFactory(
            application: Application,
            userId: Int,
        ): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
                        return ProfileViewModel(
                            application = application,
                            userId = userId
                        ) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
        }
    }
}