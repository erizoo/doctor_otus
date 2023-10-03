package ru.soft.feature_home.di

import androidx.lifecycle.*
import com.github.terrakok.cicerone.*
import dagger.*
import retrofit2.*
import ru.soft.*
import ru.soft.feature_home.data.*
import ru.soft.feature_home.presentation.*
import ru.soft.feature_home.presentation.list_orders.*
import ru.soft.feature_home.presentation.order.*
import ru.soft.main_api.*
import javax.inject.*

@Module
abstract class HomeModule {

    @Binds
    abstract fun bindsHomeViewModelFactory(
        homeViewModelFactory: HomeViewModelFactory
    ): ViewModelProvider.Factory

    @Binds
    abstract fun bindsListOrdersViewModelFactory(
        listOrdersViewModelFactory: ListOrdersViewModelFactory
    ): ViewModelProvider.Factory

    @Binds
    abstract fun bindsOrderViewModelFactory(
        orderViewModelFactory: OrderViewModelFactory
    ): ViewModelProvider.Factory

    @Module
    companion object {

        @JvmStatic
        @Provides
        fun provideMediator(map: Map<Class<*>, @JvmSuppressWildcards Provider<Any>>): MainMediator {
            return map[MainMediator::class.java]!!.get() as MainMediator
        }

        @JvmStatic
        @Provides
        fun provideAuthMediator(map: Map<Class<*>, @JvmSuppressWildcards Provider<Any>>): AuthMediator {
            return map[AuthMediator::class.java]!!.get() as AuthMediator
        }

        @JvmStatic
        @Provides
        fun provideHomeService(retrofit: Retrofit): HomeApiService {
            return retrofit.create(HomeApiService::class.java)
        }

        @JvmStatic
        @Provides
        fun provideHomeRepository(homeService: HomeApiService): HomeRepository {
            return HomeRepositoryImpl(homeService)
        }

    }
}