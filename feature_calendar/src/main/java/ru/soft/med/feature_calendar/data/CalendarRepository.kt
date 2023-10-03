package ru.soft.med.feature_calendar.data

import kotlinx.coroutines.flow.*
import ru.soft.base_arch.utils.*
import ru.soft.med.feature_calendar.data.models.*

interface CalendarRepository {

    suspend fun getSchedule(
        token: String,
        date: String
    ): Flow<Resource<List<ResponseSchedule>>>
}