package ru.soft.med

import dagger.*
import ru.soft.core_api.*
import ru.soft.core_api.mediator.*
import ru.soft.core_api.navigation.*
import ru.soft.core_api.network.*
import ru.soft.core_api.prefs.*
import ru.soft.feature_auth.navigation.*
import ru.soft.feature_home.navigation.*
import ru.soft.main.navigation.*
import ru.soft.med.feature_calendar.navigation.*

@Component(
    dependencies = [AppProvider::class, NetworkProvider::class, NavigationProvider::class, PrefProvider::class],
    modules = [MainExternalModule::class, AuthExternalModule::class,
        HomeExternalModule::class, OrderExternalModule::class, CalendarExtraModule::class]
)
interface FacadeComponent : ProvidersFacade {

    companion object {

        fun init(
            appProvider: AppProvider,
            networkProvider: NetworkProvider,
            prefProvider: PrefProvider,
            navigationProvider: NavigationProvider
        ): FacadeComponent =
            DaggerFacadeComponent.builder()
                .appProvider(appProvider)
                .networkProvider(networkProvider)
                .navigationProvider(navigationProvider)
                .prefProvider(prefProvider)
                .build()
    }
}