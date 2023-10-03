package ru.soft.feature_auth.di

import dagger.*
import ru.soft.core_api.mediator.*
import ru.soft.feature_auth.presentation.*
import javax.inject.*

@Singleton
@Component(
    modules = [AuthModule::class],
    dependencies = [ProvidersFacade::class]
)
interface AuthComponent {

    companion object {

        fun create(providersFacade: ProvidersFacade): AuthComponent {
            return DaggerAuthComponent
                .builder()
                .providersFacade(providersFacade)
                .build()
        }
    }

    fun inject(authFragment: AuthFragment)

    fun inject(smsCodeFragment: SmsCodeFragment)
}