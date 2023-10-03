package ru.soft.ui_kit.views

import android.annotation.*
import android.content.*
import android.text.*
import android.util.*
import androidx.core.widget.*
import ru.soft.ui_atoms.*

@SuppressLint("SetTextI18n")
open class InputPhoneView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : InputView(context, attrs, defStyleAttr) {

    companion object {
        private const val PHONE_LENGTH = 12
    }

    private val maskNumbers = listOf('7')
    private var needToMoveCursor = false

    init {
        setInputType(InputType.TYPE_CLASS_PHONE)
        MaskUtil.installPhoneMask(getEditTextView()) {
            if (needToMoveCursor) {
                setEndCursor()
                needToMoveCursor = false
            }
        }
        b.editText.doOnTextChanged { text, _, _, _ ->
            if (text?.length == 1 && text.first() in maskNumbers) {
                b.editText.setText("($text")
                needToMoveCursor = true
            }
        }
    }

    fun getValue() = getText().replace("+", "")
        .replace(" ", "")
        .replace("(", "")
        .replace(")", "")

    fun validate(showError: Boolean): Boolean {
        var isValid = true
        val text = getValue()
        if (text.isEmpty() || text.length != PHONE_LENGTH
        ) isValid = false
        if (isValid) hideError() else if (showError) setAndShowError(R.string.error_check_phone_number)
        return isValid
    }

}