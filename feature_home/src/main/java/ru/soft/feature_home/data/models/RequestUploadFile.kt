package ru.soft.feature_home.data.models


import com.google.gson.annotations.SerializedName

data class RequestUploadFile(
    @SerializedName("FileBase64")
    val fileBase64: String? = null,
    @SerializedName("FileName")
    val fileName: String? = null,
    @SerializedName("FileTypeDescription")
    val fileTypeDescription: String? = null,
    @SerializedName("FileTypeGUID")
    val fileTypeGUID: String? = null,
    @SerializedName("RequestGUID")
    val requestGUID: String? = null,
    @SerializedName("Token")
    val token: String? = null
)