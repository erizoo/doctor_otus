package ru.soft.core_api.navigation

import com.github.terrakok.cicerone.*

interface NavigationProvider {

    fun provideRouter() : Router

    fun provideCicerone() : Cicerone<Router>

    fun provideNavigationHolder() : NavigatorHolder

}