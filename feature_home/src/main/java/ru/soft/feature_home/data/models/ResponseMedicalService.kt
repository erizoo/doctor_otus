package ru.soft.feature_home.data.models


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.*
import kotlinx.parcelize.Parcelize

@Parcelize
data class ResponseMedicalService(
    @SerializedName("GUID")
    val gUID: String? = null,
    @SerializedName("Name")
    val name: String? = null,
    @SerializedName("Price")
    val price: Int? = null,
    @SerializedName("PaymentByThePatient")
    var paymentByThePatient: Boolean? = null
) : Parcelable