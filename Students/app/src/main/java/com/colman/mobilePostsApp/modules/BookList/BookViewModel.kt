package com.colman.mobilePostsApp.modules.BookList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.colman.mobilePostsApp.utils.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.util.Log

class BookViewModel : ViewModel() {
    private val apiService = ApiService.create()
    private val _bookTitles = MutableLiveData<List<String>>()
    val bookTitles: LiveData<List<String>> get() = _bookTitles

    fun searchBooks(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = apiService.getBooksByName(query)
            if (response.isSuccessful) {
                val titles = response.body()?.docs?.map { it.title } ?: emptyList()
                Log.d("BookViewModel", "Fetched books: $titles")
                _bookTitles.postValue(titles)
            } else {
                Log.e("BookViewModel", "API Error: ${response.errorBody()?.string()}")
                _bookTitles.postValue(emptyList())
            }
        }
    }
}