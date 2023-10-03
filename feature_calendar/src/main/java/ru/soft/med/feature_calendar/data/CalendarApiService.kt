package ru.soft.med.feature_calendar.data

import retrofit2.*
import retrofit2.http.*
import ru.soft.med.feature_calendar.data.models.*

interface CalendarApiService {

    @GET("DoctorsCallRequest/GetSchedule")
    suspend fun getSchedule(
        @Query(
            "Token",
        ) token: String,
        @Query(
            "Date",
        ) date: String
    ): Response<List<ResponseSchedule>>
}