package ru.soft.med.feature_calendar.data

import kotlinx.coroutines.flow.*
import ru.soft.base_arch.utils.*
import ru.soft.med.feature_calendar.data.models.*
import javax.inject.*

class CalendarRepositoryImpl @Inject constructor(private val calendarApiService: CalendarApiService) :
    CalendarRepository {

    override suspend fun getSchedule(
        token: String,
        date: String
    ): Flow<Resource<List<ResponseSchedule>>> =
        flow {
            val response = calendarApiService.getSchedule(
                token = token,
                date = date
            )
            emit(handleResponse(response))
        }
}