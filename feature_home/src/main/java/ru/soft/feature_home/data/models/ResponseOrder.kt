package ru.soft.feature_home.data.models


import com.google.gson.annotations.SerializedName

data class ResponseOrder(
    @SerializedName("Token")
    var token: String? = null,
    @SerializedName("Comment")
    val comment: String? = null,
    @SerializedName("CommentInspection")
    val commentInspection: String? = null,
    @SerializedName("CommentPayment")
    val commentPayment: String? = null,
    @SerializedName("CommentPhoto")
    val commentPhoto: String? = null,
    @SerializedName("Complaints")
    val complaints: String? = null,
    @SerializedName("Date")
    val date: String? = null,
    @SerializedName("GUIDLaboratory")
    val gUIDLaboratory: String? = null,
    @SerializedName("ICDDiagnosis")
    val iCDDiagnosis: ICDDiagnosis? = ICDDiagnosis(),
    @SerializedName("InshuranceCompany")
    val inshuranceCompany: String? = null,
    @SerializedName("InshuranceCompanyPhone")
    val inshuranceCompanyPhone: String? = null,
    @SerializedName("InspectionDetails")
    val inspectionDetails: List<InspectionDetail?>? = listOf(),
    @SerializedName("MedicalServices")
    val medicalServices: List<ResponseMedicalService>? = listOf(),
    @SerializedName("Number")
    val number: String? = null,
    @SerializedName("Patient")
    val patient: Patient? = Patient(),
    @SerializedName("PaymentGUID")
    val paymentGUID: String? = null,
    @SerializedName("Phone")
    val phone: String? = null,
    @SerializedName("PolicyNumber")
    val policyNumber: String? = null,
    @SerializedName("PreviousSickLeave")
    val previousSickLeave: PreviousSickLeave? = PreviousSickLeave(),
    @SerializedName("RequestAdress")
    val requestAdress: String? = null,
    @SerializedName("RequestGUID")
    val requestGUID: String? = null,
    @SerializedName("SickLeave")
    val sickLeave: SickLeave? = SickLeave(),
    @SerializedName("Status")
    val status: String? = null,
    @SerializedName("StatusActive")
    val statusActive: Boolean? = false,
    @SerializedName("VIP")
    val vIP: String? = null
) {
    data class ICDDiagnosis(
        @SerializedName("Appointment")
        val appointment: String? = null,
        @SerializedName("Code")
        val code: String? = null,
        @SerializedName("GUID")
        val gUID: String? = null,
        @SerializedName("Name")
        val name: String? = null,
        @SerializedName("ReadmissionDate")
        val readmissionDate: String? = null
    )

    data class InspectionDetail(
        @SerializedName("Name")
        val name: String? = null,
        @SerializedName("Value")
        var value: String? = null
    )

    data class Patient(
        @SerializedName("Birthday")
        val birthday: String? = null,
        @SerializedName("FIO")
        val fIO: String? = null,
        @SerializedName("GUID")
        val gUID: String? = null,
        @SerializedName("PassportDateOfIssue")
        val passportDateOfIssue: String? = null,
        @SerializedName("PassportIssuedBy")
        val passportIssuedBy: String? = null,
        @SerializedName("PassportNumber")
        val passportNumber: String? = null,
        @SerializedName("PatientComment")
        val patientComment: String? = null,
        @SerializedName("RegistrationAddress")
        val registrationAddress: String? = null,
        @SerializedName("SNILS")
        val sNILS: String? = null
    )

    data class PreviousSickLeave(
        @SerializedName("CommentSickLeave")
        val commentSickLeave: String? = null,
        @SerializedName("PreviousSickLeaveEndDate")
        val sickLeaveEndDate: String? = null,
        @SerializedName("SickLeaveNotRequired")
        val sickLeaveNotRequired: Boolean? = false,
        @SerializedName("PreviousSickLeaveNumber")
        val sickLeaveNumber: String? = null,
        @SerializedName("PreviousSickLeaveStartDate")
        val sickLeaveStartDate: String? = null,
        @SerializedName("PreviousSickLeaveStatus")
        val sickLeaveStatus: String? = null,
        @SerializedName("SickLeaveStatusToRequest")
        val sickLeaveStatusToRequest: String? = null,
        @SerializedName("SNILS")
        val sNILS: String? = null
    )

    data class SickLeave(
        @SerializedName("CommentSickLeave")
        val commentSickLeave: String? = null,
        @SerializedName("SickLeaveEndDate")
        val sickLeaveEndDate: String? = null,
        @SerializedName("SickLeaveNotRequired")
        val sickLeaveNotRequired: Boolean? = false,
        @SerializedName("SickLeaveNumber")
        val sickLeaveNumber: String? = null,
        @SerializedName("SickLeaveStartDate")
        val sickLeaveStartDate: String? = null,
        @SerializedName("SickLeaveStatus")
        val sickLeaveStatus: String? = null,
        @SerializedName("SickLeaveStatusToRequest")
        val sickLeaveStatusToRequest: String? = null,
        @SerializedName("SNILS")
        val sNILS: String? = null
    )
}