package ru.soft.core_api.mediator

import ru.soft.core_api.*
import ru.soft.core_api.navigation.*
import ru.soft.core_api.network.*
import ru.soft.core_api.prefs.*

interface ProvidersFacade : MediatorsProvider, AppProvider, NetworkProvider, NavigationProvider,
    PrefProvider