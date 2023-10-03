package ru.soft.feature_home.navigation

import dagger.*
import dagger.multibindings.*
import ru.soft.feature_home.*

@Module
interface HomeExternalModule {

    @Binds
    @IntoMap
    @ClassKey(HomeMediator::class)
    fun bindMediator(mediator: HomeMediatorImpl): Any

}