package ru.soft.med.feature_calendar.di

import androidx.lifecycle.*
import dagger.*
import retrofit2.*
import ru.soft.med.feature_calendar.data.*
import ru.soft.med.feature_calendar.presentation.*

@Module
abstract class CalendarModule {

    @Binds
    abstract fun bindsCalendarViewModelFactory(
        viewModelFactory: CalendarViewModelFactory
    ): ViewModelProvider.Factory


    @Module
    companion object {

        @JvmStatic
        @Provides
        fun provideCalendarService(retrofit: Retrofit): CalendarApiService {
            return retrofit.create(CalendarApiService::class.java)
        }

        @JvmStatic
        @Provides
        fun provideCalendarRepository(calendarService: CalendarApiService): CalendarRepository {
            return CalendarRepositoryImpl(calendarService)
        }

    }
}