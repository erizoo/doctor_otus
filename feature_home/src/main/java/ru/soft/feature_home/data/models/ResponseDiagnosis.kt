package ru.soft.feature_home.data.models


import com.google.gson.annotations.SerializedName

data class ResponseDiagnosis(
    @SerializedName("Code")
    val code: String? = null,
    @SerializedName("GUID")
    val gUID: String? = null,
    @SerializedName("Name")
    val name: String? = null
)