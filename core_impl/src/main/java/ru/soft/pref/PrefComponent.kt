package ru.soft.pref

import dagger.*
import ru.soft.core_api.*
import ru.soft.core_api.prefs.*
import javax.inject.*


@Singleton
@Component(
    dependencies = [AppProvider::class],
    modules = [PrefModule::class]
)
interface PrefComponent : PrefProvider