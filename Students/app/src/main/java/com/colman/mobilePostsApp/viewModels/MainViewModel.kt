package com.colman.mobilePostsApp.viewModels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.colman.mobilePostsApp.data.user.User
import com.colman.mobilePostsApp.data.user.UserModel

class MainViewModel @RequiresApi(api = Build.VERSION_CODES.N) constructor() : ViewModel() {
    var data: LiveData<User>

    init {
        data = UserModel.instance.getCurrentUser()
    }

    fun getUserData(): LiveData<User> {
        return data
    }
}