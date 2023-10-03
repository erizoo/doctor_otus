package ru.soft.navigation

import dagger.*
import ru.soft.core_api.navigation.*
import javax.inject.*

@Singleton
@Component(
    modules = [NavigationModule::class]
)
interface NavigationComponent : NavigationProvider