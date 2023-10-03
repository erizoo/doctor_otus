package ru.soft.feature_auth.di

import androidx.lifecycle.*
import dagger.*
import retrofit2.*
import ru.soft.feature_auth.data.*
import ru.soft.feature_auth.presentation.*
import ru.soft.feature_home.*
import ru.soft.main_api.*
import javax.inject.*

@Module
abstract class AuthModule {

    @Binds
    abstract fun bindsAuthViewModelFactory(authViewModelFactory: AuthViewModelFactory): ViewModelProvider.Factory

    @Module
    companion object{

        @JvmStatic
        @Provides
        fun provideAuthService(retrofit: Retrofit): AuthService {
            return retrofit.create(AuthService::class.java)
        }

        @JvmStatic
        @Provides
        fun provideAuthRepository(authService: AuthService): AuthRepository {
            return AuthRepositoryImpl(authService)
        }

        @JvmStatic
        @Provides
        fun provideMediator(map: Map<Class<*>, @JvmSuppressWildcards Provider<Any>>): HomeMediator {
            return map[HomeMediator::class.java]!!.get() as HomeMediator
        }
    }
}