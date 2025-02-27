package com.colman.mobilePostsApp.ui.preview

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onepicture.utils.ApiService
import kotlinx.coroutines.launch

class LocationViewModel : ViewModel() {

    private val _cities = MutableLiveData<List<String>>()
    val cities: LiveData<List<String>> get() = _cities

    private val apiService = ApiService.create()

    fun fetchCities() {
        viewModelScope.launch {
            try {
                val response = apiService.getCities()
                if (response.isSuccessful) {
                    val cities = response.body()?.features?.map { it.attributes.city } ?: emptyList()
                    _cities.value = cities
                    Log.d("LocationViewModel", "Fetched cities: $cities")
                } else {
                    Log.e("LocationViewModel", "Error fetching cities: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("LocationViewModel", "Exception fetching cities", e)
            }
        }
    }
}
