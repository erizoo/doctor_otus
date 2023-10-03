package ru.soft.feature_home.navigation

import android.util.*
import androidx.fragment.app.*
import com.github.terrakok.cicerone.*
import com.github.terrakok.cicerone.androidx.*
import ru.soft.feature_home.*
import ru.soft.feature_home.presentation.order.*
import javax.inject.*

class OrderMediatorImpl
@Inject constructor(private val router: Router) : OrderMediator {

    override fun openOrderScreen(guid: String) {
        Log.d("OrderMediatorImpl", "openOrderScreen is called")
        router.replaceScreen(object : FragmentScreen {
            override fun createFragment(factory: FragmentFactory): Fragment {
                return OrderFragment.newInstance(
                    requestId = guid
                )
            }
        })
    }

    override fun openCreateOrderScreen() {
        Log.d("OrderMediatorImpl", "openCreateOrderScreen is called")
        router.replaceScreen(object : FragmentScreen {
            override fun createFragment(factory: FragmentFactory): Fragment {
                return CreateOrderFragment()
            }
        })
    }
}