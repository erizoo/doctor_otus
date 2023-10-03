package ru.soft.feature_home.navigation

import dagger.*
import dagger.multibindings.*
import ru.soft.feature_home.*

@Module
interface OrderExternalModule {

    @Binds
    @IntoMap
    @ClassKey(OrderMediator::class)
    fun bindMediator(mediator: OrderMediatorImpl): Any

}