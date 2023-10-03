package ru.soft.ui_kit.views

import android.content.*
import android.telecom.PhoneAccount.*
import android.text.*
import android.util.*
import android.view.*
import android.widget.*
import androidx.annotation.*
import androidx.core.widget.*
import ru.soft.ui_kit.*
import ru.soft.ui_kit.databinding.*

open class InputView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    protected val b by lazy {
        ViewInputBinding.inflate(
            LayoutInflater.from(context), this, true
        )
    }
    private var currentWatcher: TextWatcher? = null

    init {
        checkAttr(context, attrs)
    }

    private fun checkAttr(context: Context, attrs: AttributeSet?) {
        val attributes =
            context.obtainStyledAttributes(attrs, R.styleable.InputView)
        val maxLength = attributes.getInt(R.styleable.InputView_maxLength, NO_RESOURCE_ID)
        setMaxLength(maxLength)
        val gravityValue = attributes.getInt(R.styleable.InputView_inputGravity, Gravity.NO_GRAVITY)
        if (gravityValue != Gravity.NO_GRAVITY) {
            setGravity(gravityValue)
        }
        attributes.getString(R.styleable.InputView_hint)?.let {
            setHint(it)
        }
        attributes.recycle()
    }

    fun doAfterTextChanged(action: (String) -> Unit) {
        currentWatcher = b.editText.doAfterTextChanged {
            hideError()
            action(it.toString())
        }
    }

    fun clickAction(action: () -> Unit) {
        b.editText.setOnClickListener {
            action()
        }
    }

    fun setGravity(gravity: Int) {
        b.editText.gravity = gravity
    }

    fun setMaxLength(maxLength: Int) {
        if (maxLength > 0) {
            b.editText.filters = arrayOf(InputFilter.LengthFilter(maxLength))
        }
    }

    fun setHint(hint: String) {
        b.editText.hint = hint
    }

    fun setHint(@StringRes hint: Int) {
        setHint(resources.getString(hint))
    }

    fun setInputType(inputType: Int) {
        b.editText.inputType = inputType
    }

    fun setSelection(index: Int) {
        b.editText.setSelection(index)
    }

    fun setEndCursor() {
        b.editText.text?.length?.let { b.editText.setSelection(it) }
    }

    fun getEditTextView() = b.editText

    fun isEmptyValue(): Boolean = b.editText.text?.trim()?.isEmpty() == true

    fun setOnInputFocusChangeListener(action: (Boolean) -> Unit) {
        b.editText.setOnFocusChangeListener { _, isFocused ->
            action(isFocused)
        }
    }

    fun setAndShowError(@StringRes error: Int) {
        b.tvLayoutError.text = context.getString(error)
        b.tvLayoutError.visibility = View.VISIBLE
    }

    fun setAndShowError(error: String) {
        b.tvLayoutError.text = error
        b.tvLayoutError.visibility = View.VISIBLE
    }

    fun setAndShowError(error: SpannableStringBuilder) {
        b.tvLayoutError.setText(error, TextView.BufferType.SPANNABLE)
        b.tvLayoutError.visibility = View.VISIBLE
    }

    fun hideError() {
        b.tvLayoutError.visibility = View.GONE
    }

    fun getErrorText() = b.tvLayoutError.text.toString()

    fun getText() = b.editText.text.toString()

    fun getTextOrNull() = b.editText.text.toString().ifEmpty { null }

    fun setText(text: String?) {
        b.editText.setText(text)
    }

    fun setTextWithoutChangeEvents(text: String?) {
        b.editText.setTextWithoutChangeEvents(
            text,
            currentWatcher
        )
    }

    fun setTextWithoutChangeEventsIgnoreFocus(text: String?) {
        b.editText.setTextWithoutChangeEventsIgnoreFocus(
            text,
            currentWatcher
        )
    }

    fun makeTextUntouchable() {
        b.editText.apply {
            isClickable = false
            isFocusable = false
        }
    }

    fun makeTextTouchable() {
        b.editText.apply {
            isClickable = true
            isFocusable = true
        }
    }

    private fun EditText.setTextWithoutChangeEvents(text: String?, currentWatcher: TextWatcher?) {
        if (!isFocused) {
            currentWatcher?.let { removeTextChangedListener(it) }
            setText(text)
            currentWatcher?.let { addTextChangedListener(it) }
        }
    }

    private fun EditText.setTextWithoutChangeEventsFocused(text: String?, currentWatcher: TextWatcher?) {
        if (isFocused) {
            currentWatcher?.let { removeTextChangedListener(it) }
            setText(text)
            currentWatcher?.let { addTextChangedListener(it) }
        }
    }

    private fun EditText.setTextWithoutChangeEventsIgnoreFocus(text: String?, currentWatcher: TextWatcher?) {
        currentWatcher?.let { removeTextChangedListener(it) }
        setText(text)
        currentWatcher?.let { addTextChangedListener(it) }
    }

    private fun EditText.doAfterTextChangedAndFocused(action: (String) -> Unit): TextWatcher {
        return doAfterTextChanged {
            if (isFocused) {
                action(it.toString())
            }
        }
    }
}