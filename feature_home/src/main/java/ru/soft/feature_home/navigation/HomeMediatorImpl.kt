package ru.soft.feature_home.navigation

import android.util.*
import androidx.fragment.app.*
import com.github.terrakok.cicerone.*
import com.github.terrakok.cicerone.androidx.*
import ru.soft.feature_home.*
import ru.soft.feature_home.presentation.*
import javax.inject.*

class HomeMediatorImpl
@Inject constructor(private val router: Router) : HomeMediator {

    override fun openHomeScreen() {
        Log.d("HomeMediatorImpl", "startHomeScreen is called")
        router.replaceScreen(object : FragmentScreen {
            override fun createFragment(factory: FragmentFactory): Fragment {
                return HomeFragment()
            }
        })
    }
}