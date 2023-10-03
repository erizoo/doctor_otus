package ru.soft.ui_kit.views

import android.content.*
import android.util.*
import android.view.*
import android.widget.*
import ru.soft.ui_kit.*
import ru.soft.ui_kit.databinding.*

class InputDateView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    protected val b by lazy {
        ViewInputDateBinding.inflate(
            LayoutInflater.from(context), this, true
        )
    }

    init {
        checkAttr(context, attrs)
    }

    private fun checkAttr(context: Context, attrs: AttributeSet?) {
        val attributes =
            context.obtainStyledAttributes(attrs, R.styleable.InputDateView)
        val textDate = attributes.getString(
            R.styleable.InputDateView_textDate
        )
        b.tvDate.text = textDate
        attributes.recycle()
    }

    fun clickAction(action: () -> Unit) {
        b.root.setOnClickListener {
            action()
        }
    }

    fun setText(text: String) {
        b.tvDate.text = text
    }

}