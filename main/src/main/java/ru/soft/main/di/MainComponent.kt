package ru.soft.main.di

import dagger.*
import ru.soft.core_api.mediator.*
import ru.soft.main.*
import javax.inject.*

@Singleton
@Component(
    modules = [MainModule::class],
    dependencies = [ProvidersFacade::class]
)
interface MainComponent {

    companion object {

        fun create(providersFacade: ProvidersFacade): MainComponent {
            return DaggerMainComponent
                .builder()
                .providersFacade(providersFacade)
                .build()
        }
    }

    fun inject(mainActivity: MainActivity)
}