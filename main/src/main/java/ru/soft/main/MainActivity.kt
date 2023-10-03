package ru.soft.main

import android.*
import android.content.*
import android.content.pm.*
import android.os.*
import android.view.*
import androidx.activity.result.contract.*
import androidx.appcompat.app.*
import androidx.constraintlayout.widget.*
import androidx.core.content.*
import com.github.terrakok.cicerone.*
import com.github.terrakok.cicerone.androidx.*
import com.google.android.material.bottomnavigation.*
import ru.soft.*
import ru.soft.base_arch.utils.BiometricUtil.confirmFingerPrint
import ru.soft.base_arch.utils.BiometricUtil.createPromptInfo
import ru.soft.base_arch.utils.BiometricUtil.isAvailableBiometric
import ru.soft.core_api.mediator.*
import ru.soft.feature_home.*
import ru.soft.main.di.*
import ru.soft.med.feature_calendar_api.*
import java.util.concurrent.*
import javax.inject.*

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var navigatorHolder: NavigatorHolder
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var constraintLayout: ConstraintLayout
    private val navigator = AppNavigator(this, R.id.container)
    private lateinit var executor: Executor

    @Inject
    lateinit var authMediator: AuthMediator

    @Inject
    lateinit var homeMediator: HomeMediator

    @Inject
    lateinit var calendarMediator: CalendarMediator

    @Inject
    lateinit var orderMediator: OrderMediator

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var sharedPreferencesEditor: SharedPreferences.Editor

    companion object {

        const val ARG_IS_SHOW_BOTTOM_MENU = "ARG_IS_SHOW_BOTTOM_MENU"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainComponent.create((application as AppWithFacade).getFacade()).inject(this)
        setContentView(R.layout.activity_main)
        navigatorHolder.setNavigator(navigator)
        executor = ContextCompat.getMainExecutor(this)
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        constraintLayout = findViewById(R.id.layout)

        if (!intent.extras?.getString("guid", "").isNullOrEmpty()) {
            intent.extras?.getString("guid", "")?.let { orderMediator.openOrderScreen(it) }
        } else {
            if (sharedPreferences.getString("TOKEN", null)
                    .isNullOrEmpty() && sharedPreferences.getBoolean("IS_LOGIN", false)
            ) {
                authMediator.startAuthScreen()
                val showBottomNav = intent.getBooleanExtra(ARG_IS_SHOW_BOTTOM_MENU, false)
                showBottomNav(showBottomNav)
            } else {
                if (isAvailableBiometric(applicationContext)) {
                    callBiometric()
                } else {
                    if (!sharedPreferences.getBoolean("IS_LOGIN", false)) {
                        authMediator.startAuthScreen()
                        showBottomNav(false)
                    } else {
                        homeMediator.openHomeScreen()
                        showBottomNav(true)
                    }
                }
            }
        }

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    homeMediator.openHomeScreen()
                }

                R.id.navigation_calendar -> {
                    calendarMediator.openCalendarScreen()
                }

                R.id.navigation_create_order -> {
                    orderMediator.openCreateOrderScreen()
                }
            }
            true
        }

    }

    private fun callBiometric() {
        confirmFingerPrint(
            this,
            executor,
            createPromptInfo(
                "Проверка безопасности",
                "Войдите используя отпечаток пальца",
                "Пожалуйста, авторизуйтесь по телефону"
            ),
            onAuthenticationError = {
                authMediator.startAuthScreen()
                showBottomNav(false)
            },
            onAuthenticationFailed = {
                authMediator.startAuthScreen()
                showBottomNav(false)
            },
            onAuthenticationSucceeded = {
                homeMediator.openHomeScreen()
                showBottomNav(true)
            }
        )
    }

    override fun onStart() {
        sharedPreferencesEditor.putBoolean("IS_LOGIN", false)
        sharedPreferencesEditor.apply()
        super.onStart()
    }

    override fun onDestroy() {
        navigatorHolder.removeNavigator()
        sharedPreferencesEditor.putBoolean("IS_LOGIN", false)
        sharedPreferencesEditor.apply()
        super.onDestroy()
    }

    fun showBottomNav(show: Boolean) {
        bottomNavigationView.visibility = if (show) View.VISIBLE else View.GONE

        val set = ConstraintSet()
        set.clone(constraintLayout)

        val bottomConstraint = if (show) bottomNavigationView.id else constraintLayout.id
        set.connect(R.id.container, ConstraintSet.BOTTOM, bottomConstraint, ConstraintSet.BOTTOM)
        set.applyTo(constraintLayout)
    }

    fun openHome() {
        bottomNavigationView.selectedItemId = R.id.navigation_home
    }
}
