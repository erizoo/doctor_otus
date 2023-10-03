package ru.soft.med.feature_calendar.navigation

import android.util.*
import androidx.fragment.app.*
import com.github.terrakok.cicerone.*
import com.github.terrakok.cicerone.androidx.*
import ru.soft.med.feature_calendar.presentation.*
import ru.soft.med.feature_calendar_api.*
import javax.inject.*

class CalendarMediatorImpl
@Inject constructor(private val router: Router) : CalendarMediator {

    override fun openCalendarScreen() {
        Log.d("HomeMediatorImpl", "startHomeScreen is called")
        router.replaceScreen(object : FragmentScreen {
            override fun createFragment(factory: FragmentFactory): Fragment {
                return CalendarFragment()
            }
        })
    }
}