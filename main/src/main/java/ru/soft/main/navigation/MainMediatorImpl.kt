package ru.soft.main.navigation

import android.content.*
import android.util.*
import ru.soft.main.*
import ru.soft.main_api.*
import javax.inject.*

class MainMediatorImpl
@Inject constructor() : MainMediator {

    override fun showBottomNav(context: Context) {
        Log.d("MainMediatorImpl", "showBottomNav is called")
        val activity = (context as? MainActivity)
        activity?.showBottomNav(true)
    }

    override fun hideBottomBar(context: Context) {
        Log.d("MainMediatorImpl", "hideBottomBar is called")
        val activity = (context as? MainActivity)
        activity?.showBottomNav(false)
    }

    override fun openHome(context: Context) {
        Log.d("MainMediatorImpl", "openHome is called")
        val activity = (context as? MainActivity)
        activity?.openHome()
    }
}