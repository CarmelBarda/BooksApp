package com.colman.mobilePostsApp.utils

import com.colman.mobilePostsApp.data.book.BookResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("search.json")
    suspend fun getBooksByName(
        @Query("title") query: String,
        @Query("limit") maxResults: Int = 10
    ): Response<BookResponse>

    companion object {
        fun create(): ApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://openlibrary.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(ApiService::class.java)
        }
    }
}
