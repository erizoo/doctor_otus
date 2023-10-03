package ru.soft.med

import android.app.Application
import ru.soft.core.*
import ru.soft.core_api.*
import ru.soft.core_api.mediator.*

class App : Application(), AppWithFacade {

    companion object {
        private var facadeComponent: FacadeComponent? = null
        private lateinit var appProvider: AppProvider
    }

    override fun getFacade(): ProvidersFacade {
        if (facadeComponent == null) {
            appProvider = AppComponent.create(this)
            facadeComponent = FacadeComponent.init(
                appProvider = appProvider,
                networkProvider = CoreProvidersFactory.createNetworkBuilder(appProvider),
                navigationProvider = CoreProvidersFactory.createNavigationBuilder(),
                prefProvider = CoreProvidersFactory.createPrefBuilder(appProvider)
            )
        }
        return facadeComponent!!
    }

    override fun onCreate() {
        super.onCreate()
        getFacade()
    }
}