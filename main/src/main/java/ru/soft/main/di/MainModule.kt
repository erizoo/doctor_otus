package ru.soft.main.di

import dagger.*
import ru.soft.*
import ru.soft.feature_home.*
import ru.soft.med.feature_calendar_api.*
import javax.inject.*

@Module
abstract class MainModule {

    @Module
    companion object {

        @JvmStatic
        @Provides
        fun provideMediator(map: Map<Class<*>, @JvmSuppressWildcards Provider<Any>>): AuthMediator {
            return map[AuthMediator::class.java]!!.get() as AuthMediator
        }

        @JvmStatic
        @Provides
        fun provideHomeMediator(map: Map<Class<*>, @JvmSuppressWildcards Provider<Any>>): HomeMediator {
            return map[HomeMediator::class.java]!!.get() as HomeMediator
        }

        @JvmStatic
        @Provides
        fun provideOrderMediator(map: Map<Class<*>, @JvmSuppressWildcards Provider<Any>>): OrderMediator {
            return map[OrderMediator::class.java]!!.get() as OrderMediator
        }

        @JvmStatic
        @Provides
        fun provideCalendarMediator(map: Map<Class<*>, @JvmSuppressWildcards Provider<Any>>): CalendarMediator {
            return map[CalendarMediator::class.java]!!.get() as CalendarMediator
        }
    }
}