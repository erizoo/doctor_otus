package ru.soft.feature_auth.data.models

import com.google.gson.annotations.*

data class ResponseSentPhoneModelItem(
    @SerializedName("Doctor")
    val doctor: String? = ""
)