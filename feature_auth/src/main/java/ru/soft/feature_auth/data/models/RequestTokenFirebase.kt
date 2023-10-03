package ru.soft.feature_auth.data.models


import com.google.gson.annotations.SerializedName

data class RequestTokenFirebase(
    @SerializedName("Token")
    val token: String? = null,
    @SerializedName("TokenFairBase")
    val tokenFairBase: String? = null
)