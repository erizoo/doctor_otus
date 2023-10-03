package ru.soft.feature_home.data.models


import com.google.gson.annotations.*

data class ResponseCreatePayment(
    @SerializedName("PaymentGUID")
    val paymentGUID: String? = null,
    @SerializedName("QRCode")
    val qr: String? = null,
    @SerializedName("QRCodeStatus")
    val qrStatus: String? = null,
    @SerializedName("PaymentLink")
    val paymentLink: String? = null,
    @SerializedName("PaymentLinkStatus")
    val paymentLinkStatus: String? = null
)