package ru.soft.feature_home.data.models


import com.google.gson.annotations.SerializedName

data class ResponseListTypeFiles(
    @SerializedName("Description")
    val description: String? = null,
    @SerializedName("DescriptionRequired")
    val descriptionRequired: Boolean? = null,
    @SerializedName("GUID")
    val gUID: String? = null,
    @SerializedName("Name")
    val name: String? = null,
    var isHaveFile: Boolean = false
)