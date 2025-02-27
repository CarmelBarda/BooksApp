package com.example.onepicture.data.model.api

import com.google.gson.annotations.SerializedName

data class Feature(
    @SerializedName("attributes") val attributes: Attributes
)
