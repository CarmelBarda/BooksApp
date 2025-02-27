package com.example.onepicture.ui.login

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.onepicture.data.db.AppDatabase
import com.example.onepicture.data.model.User
import com.example.onepicture.data.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class LoginViewModel(
    application: Application,
    private val sharedPreferences: SharedPreferences
) :
    AndroidViewModel(application) {
    private val userRepository: UserRepository =
        UserRepository(AppDatabase.getDatabase(application).userDao())

    fun loginUser(
        email: String,
        password: String,
        rememberMe: Boolean,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = FirebaseAuth.getInstance().currentUser
                    user?.let {
                        // Query the user from the repository
                        val existingUserValue = userRepository.getUserEmail(email)

                        existingUserValue.observeForever { existingUser ->
                            if (existingUser != null) {
                                saveUserToPreferences(existingUser.id, existingUser.name)
                            } else {
                                viewModelScope.launch {
                                    val newUserId = userRepository.insertUser(
                                        User(
                                            name = "",
                                            email = email,
                                            profileImageUrl = ""
                                        )
                                    )

                                    saveUserToPreferences(newUserId.toInt(), "")
                                }

                            }

                            saveRememberMePreferences(email, password, rememberMe)
                            onSuccess()
                        }
                    }
                } else {
                    onFailure(task.exception?.message ?: "Unknown error")
                }
            }
    }

    private fun saveUserToPreferences(userId: Int, userName: String) {
        sharedPreferences.edit().apply {
            putInt("userId", userId)
            putString("userName", userName)
            apply()
        }
    }

    private fun saveRememberMePreferences(email: String, password: String, rememberMe: Boolean) {
        sharedPreferences.edit().apply {
            if (rememberMe) {
                putString("email", email)
                putString("password", password)
                putBoolean("rememberMe", true)
            } else {
                remove("email")
                remove("password")
                remove("rememberMe")
            }
            apply()
        }
    }

    companion object {
        fun provideFactory(
            application: Application,
            sharedPreferences: SharedPreferences,
        ): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
                        return LoginViewModel(
                            application = application,
                            sharedPreferences = sharedPreferences
                        ) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
        }
    }
}