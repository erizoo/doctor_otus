package ru.soft.med.feature_calendar.data.models


import com.google.gson.annotations.SerializedName

data class ResponseSchedule(
    @SerializedName("Date")
    val date: String? = null,
    @SerializedName("EndTime")
    val endTime: String? = null,
    @SerializedName("StartTime")
    val startTime: String? = null,
    @SerializedName("TimeType")
    val timeType: String? = null
)