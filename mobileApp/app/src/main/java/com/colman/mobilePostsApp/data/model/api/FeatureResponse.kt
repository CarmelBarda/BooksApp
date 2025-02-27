package com.example.onepicture.data.model.api

import com.google.gson.annotations.SerializedName

data class FeatureResponse(
    @SerializedName("features") val features: List<Feature>
)