package com.colman.mobilePostsApp.data.book

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.colman.mobilePostsApp.utils.ApiService
import kotlinx.coroutines.Dispatchers

class BookViewModel : ViewModel() {
    private val apiService = ApiService.create()

    fun searchBooks(query: String) = liveData(Dispatchers.IO) {
        val response = apiService.getBooksByName(query)
        if (response.isSuccessful) {
            emit(response.body()?.items ?: emptyList())
        } else {
            emit(emptyList())
        }
    }
}
