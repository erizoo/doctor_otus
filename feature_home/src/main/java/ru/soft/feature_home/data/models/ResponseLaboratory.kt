package ru.soft.feature_home.data.models


import com.google.gson.annotations.SerializedName

data class ResponseLaboratory(
    @SerializedName("GUID")
    val gUID: String? = null,
    @SerializedName("Name")
    val name: String? = null
)