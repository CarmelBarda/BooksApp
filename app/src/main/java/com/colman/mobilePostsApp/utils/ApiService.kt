package com.example.onepicture.utils

import com.example.onepicture.data.model.api.FeatureResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("query")
    suspend fun getCities(
        @Query("where") where: String = "1=1",
        @Query("outFields") outFields: String = "City",
        @Query("outSR") outSR: Int = 4326,
        @Query("f") format: String = "json"
    ): Response<FeatureResponse>

    companion object {
        fun create(): ApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://services2.arcgis.com/5I7u4SJE1vUr79JC/arcgis/rest/services/UniversityChapters_Public/FeatureServer/0/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(ApiService::class.java)
        }
    }
}