package ru.soft.feature_auth.navigation

import dagger.*
import dagger.multibindings.*
import ru.soft.*
import ru.soft.core_api.mediator.*

@Module
interface AuthExternalModule {

    @Binds
    @IntoMap
    @ClassKey(AuthMediator::class)
    fun bindMediator(mediator: AuthMediatorImpl): Any

}