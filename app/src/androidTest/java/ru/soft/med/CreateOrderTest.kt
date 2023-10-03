package ru.soft.med

import android.view.*
import androidx.test.espresso.*
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.*
import junit.framework.TestCase.*
import org.hamcrest.CoreMatchers.*
import org.junit.*
import ru.soft.core_api.mediator.*
import ru.soft.feature_auth.R
import ru.soft.main.*
import ru.soft.ui_kit.views.*
import org.hamcrest.Matcher

class CreateOrderTest {

    fun setTextInCustomView(text: String): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return allOf(isDisplayed(), isAssignableFrom(InputPhoneView::class.java))
            }

            override fun getDescription(): String {
                return "Set text in custom view"
            }

            override fun perform(uiController: UiController?, view: View?) {
                val customView = view as InputPhoneView
                customView.setText(text)
            }
        }
    }

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun init() {
        activityRule.scenario.onActivity { activity ->
            ((activity.application as AppWithFacade)).getFacade()
        }
    }

    @Test
    fun shouldPassOnNoInternetScanTest() {
        onView(withId(R.id.iv_phone))
            .check(matches(isDisplayed()))
        val textToSet = "79097844502"
        onView(withId(R.id.iv_phone))
            .perform(setTextInCustomView(textToSet))
        onView(withId(R.id.btn_send_code))
            .perform(click())

        // Создаем GetErrorTextAction для получения текста ошибки из вашей InputPhoneView
        val getErrorTextAction = GetErrorTextAction()

        // Выполняем ViewAction для получения текста ошибки
        onView(isAssignableFrom(InputPhoneView::class.java))
            .perform(getErrorTextAction)

        // Ожидаемый текст ошибки
        val expectedErrorText = "Error: Врач с указаным телефоном не найден!"
        Thread.sleep(2000)
        // Получаем текст ошибки с помощью GetErrorTextAction
        val actualErrorText = getErrorTextAction.getErrorTextResult()

        // Проверяем, что текст ошибки совпадает с ожидаемым значением
        assertEquals(expectedErrorText, actualErrorText)
    }

    @After
    fun after() {

    }
}