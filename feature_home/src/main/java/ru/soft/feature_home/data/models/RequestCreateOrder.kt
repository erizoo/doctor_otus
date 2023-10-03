package ru.soft.feature_home.data.models


import com.google.gson.annotations.SerializedName

data class RequestCreateOrder(
    @SerializedName("Comment")
    val comment: String? = null,
    @SerializedName("InsuranceGUID")
    val insuranceGUID: String? = null,
    @SerializedName("InsurancePolicyNumber")
    val insurancePolicyNumber: String? = null,
    @SerializedName("PatientEmail")
    val patientEmail: String? = null,
    @SerializedName("PatientFIO")
    val patientFIO: String? = null,
    @SerializedName("PatientPhone")
    val patientPhone: String? = null,
    @SerializedName("Token")
    val token: String? = null,
    @SerializedName("Сomplaints")
    val сomplaints: String? = null
)