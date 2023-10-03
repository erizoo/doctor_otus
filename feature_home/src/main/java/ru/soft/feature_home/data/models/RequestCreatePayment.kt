package ru.soft.feature_home.data.models


import com.google.gson.annotations.SerializedName

data class RequestCreatePayment(
    @SerializedName("RequestGUID")
    val requestGUID: String? = null,
    @SerializedName("PaymentType")
    val paymentType: String? = null,
    @SerializedName("PaymentGUID")
    val paymentGUID: String? = null,
    @SerializedName("Sum")
    val sum: String? = null,
    @SerializedName("Token")
    val token: String? = null
)