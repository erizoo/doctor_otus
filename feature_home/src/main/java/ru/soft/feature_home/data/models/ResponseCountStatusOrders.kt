package ru.soft.feature_home.data.models


import com.google.gson.annotations.SerializedName

data class ResponseCountStatusOrders(
    @SerializedName("Count")
    val count: Int? = 0,
    @SerializedName("StatusName")
    val statusName: String? = "",
    @SerializedName("StatusNameToRequest")
    val statusNameForServer: String? = ""
)