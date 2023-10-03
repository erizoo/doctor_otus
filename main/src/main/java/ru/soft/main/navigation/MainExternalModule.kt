package ru.soft.main.navigation

import dagger.*
import dagger.multibindings.*
import ru.soft.*
import ru.soft.main_api.*

@Module
interface MainExternalModule {

    @Binds
    @IntoMap
    @ClassKey(MainMediator::class)
    fun bindMediator(mediator: MainMediatorImpl): Any

}