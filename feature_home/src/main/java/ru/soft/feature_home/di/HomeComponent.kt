package ru.soft.feature_home.di

import dagger.*
import ru.soft.core_api.mediator.*
import ru.soft.feature_home.presentation.*
import ru.soft.feature_home.presentation.list_orders.*
import ru.soft.feature_home.presentation.order.*
import javax.inject.*

@Singleton
@Component(
    modules = [HomeModule::class],
    dependencies = [ProvidersFacade::class]
)
interface HomeComponent {

    companion object {

        fun create(providersFacade: ProvidersFacade): HomeComponent {
            return DaggerHomeComponent
                .builder()
                .providersFacade(providersFacade)
                .build()
        }
    }

    fun inject(homeFragment: HomeFragment)

    fun inject(listOrdersFragment: ListOrdersFragment)

    fun inject(orderFragment: OrderFragment)

    fun inject(createOrderFragment: CreateOrderFragment)

    fun inject(dialogListDiagnosis: DialogListDiagnosis)
}