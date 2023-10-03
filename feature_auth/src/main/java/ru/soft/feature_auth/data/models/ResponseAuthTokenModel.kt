package ru.soft.feature_auth.data.models

import com.google.gson.annotations.*

data class ResponseAuthTokenModel(
    @SerializedName("Doctor")
    val doctor: String? = null,
    @SerializedName("Token")
    val token: String? = null
)
