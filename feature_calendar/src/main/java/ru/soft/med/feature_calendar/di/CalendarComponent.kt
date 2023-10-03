package ru.soft.med.feature_calendar.di

import dagger.*
import ru.soft.core_api.mediator.*
import ru.soft.med.feature_calendar.presentation.*
import javax.inject.*

@Singleton
@Component(
    modules = [CalendarModule::class],
    dependencies = [ProvidersFacade::class]
)
interface CalendarComponent {

    companion object {

        fun create(providersFacade: ProvidersFacade): CalendarComponent {
            return DaggerCalendarComponent
                .builder()
                .providersFacade(providersFacade)
                .build()
        }
    }

    fun inject(calendarFragment: CalendarFragment)

}