package ru.soft.core

import ru.soft.*
import ru.soft.core_api.*
import ru.soft.core_api.navigation.*
import ru.soft.core_api.network.*
import ru.soft.core_api.prefs.*
import ru.soft.navigation.*
import ru.soft.pref.*

object CoreProvidersFactory {

    fun createNetworkBuilder(appProvider: AppProvider): NetworkProvider {
        return DaggerNetworkComponent.builder().appProvider(appProvider).build()
    }

    fun createNavigationBuilder(): NavigationProvider {
        return DaggerNavigationComponent.builder().build()
    }

    fun createPrefBuilder(appProvider: AppProvider): PrefProvider {
        val prefModule = PrefModule(appProvider.provideContext())
        return DaggerPrefComponent.builder().appProvider(appProvider).prefModule(prefModule).build()
    }
}