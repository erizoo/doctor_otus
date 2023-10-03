package ru.soft.main_api

import android.content.Context

interface MainMediator {

    fun showBottomNav(context: Context)

    fun hideBottomBar(context: Context)

    fun openHome(context: Context)

}