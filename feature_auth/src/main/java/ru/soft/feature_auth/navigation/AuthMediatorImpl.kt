package ru.soft.feature_auth.navigation

import android.util.*
import androidx.fragment.app.*
import com.github.terrakok.cicerone.*
import com.github.terrakok.cicerone.androidx.*
import ru.soft.*
import ru.soft.feature_auth.presentation.*
import javax.inject.*

class AuthMediatorImpl
@Inject constructor(private val router: Router) : AuthMediator {

    override fun startAuthScreen() {
        Log.d("AuthMediatorImpl", "startAuthScreen is called")
        router.replaceScreen(object : FragmentScreen {
            override fun createFragment(factory: FragmentFactory): Fragment {
                return AuthFragment()
            }
        })
    }
}