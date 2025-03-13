package com.colman.mobilePostsApp.utils

import com.colman.mobilePostsApp.data.book.BookResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("search")
    suspend fun getBooksByName(
        @Query("q") query: String, // The partial book name
        @Query("maxResults") maxResults: Int = 10,
        @Query("printType") printType: String = "books"
    ): Response<BookResponse>

    companion object {
        fun create(): ApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://www.googleapis.com/books/v1/volumes/") // Example API: Google Books API
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(ApiService::class.java)
        }
    }
}
