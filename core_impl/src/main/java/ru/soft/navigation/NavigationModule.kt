package ru.soft.navigation

import com.github.terrakok.cicerone.*
import dagger.*
import javax.inject.*

@Module
class NavigationModule {

    private val cicerone = Cicerone.create()

    @Singleton
    @Provides
    fun provideCicerone(): Cicerone<Router> = cicerone

    @Singleton
    @Provides
    fun provideRouter(): Router = cicerone.router

    @Singleton
    @Provides
    fun provideNavigationHolder(): NavigatorHolder = cicerone.getNavigatorHolder()
}