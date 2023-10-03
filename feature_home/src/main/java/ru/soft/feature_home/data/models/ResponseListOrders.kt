package ru.soft.feature_home.data.models


import com.google.gson.annotations.SerializedName

data class ResponseListOrders(
    @SerializedName("AssistanceType")
    val assistanceType: String? = "",
    @SerializedName("item")
    val item: Int? = 0,
    @SerializedName("Number")
    val number: String? = "",
    @SerializedName("Patient")
    val patient: String? = "",
    @SerializedName("RequestAdress")
    val requestAdress: String? = "",
    @SerializedName("RequestDateTime")
    val requestDateTime: String? = "",
    @SerializedName("RequestGUID")
    val requestGUID: String? = "",
    @SerializedName("Status")
    val status: String? = "",
    @SerializedName("StatusActiv")
    val statusActiv: Boolean? = false,
    @SerializedName("VIPStatus")
    val vIPStatus: String? = ""
)