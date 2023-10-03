package ru.soft.med.feature_calendar.navigation

import dagger.*
import dagger.multibindings.*
import ru.soft.med.feature_calendar_api.*

@Module
interface CalendarExtraModule {

    @Binds
    @IntoMap
    @ClassKey(CalendarMediator::class)
    fun bindMediator(mediator: CalendarMediatorImpl): Any

}