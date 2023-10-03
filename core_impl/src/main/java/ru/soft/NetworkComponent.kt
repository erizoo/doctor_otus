package ru.soft

import dagger.*
import ru.soft.core_api.*
import ru.soft.core_api.network.*
import javax.inject.*

@Singleton
@Component(
    dependencies = [AppProvider::class],
    modules = [NetworkModule::class]
)
interface NetworkComponent : NetworkProvider
