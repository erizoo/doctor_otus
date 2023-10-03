package ru.soft.med

import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import android.view.View
import org.hamcrest.Matcher
import ru.soft.ui_kit.views.*

class GetErrorTextAction : ViewAction {
    private var errorText: String? = null

    override fun getDescription(): String {
        return "Get error text from custom view"
    }

    override fun getConstraints(): Matcher<View> {
        return isAssignableFrom(View::class.java)
    }

    override fun perform(uiController: UiController?, view: View?) {
        if (view != null && view is InputPhoneView) {
            errorText = view.getErrorText()
        }
    }

    fun getErrorTextResult(): String? {
        return errorText
    }
}