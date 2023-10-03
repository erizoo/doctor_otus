package ru.soft.feature_home.data.models


import com.google.gson.annotations.SerializedName

data class ResponseInsuranceCompanies(
    @SerializedName("GUID")
    val gUID: String? = null,
    @SerializedName("INN")
    val iNN: String? = null,
    @SerializedName("KPP")
    val kPP: String? = null,
    @SerializedName("Name")
    val name: String? = null,
    @SerializedName("OGRN")
    val oGRN: String? = null,
    @SerializedName("Phone")
    val phone: String? = null
)