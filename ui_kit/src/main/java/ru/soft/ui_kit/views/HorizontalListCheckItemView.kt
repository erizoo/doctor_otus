package ru.soft.ui_kit.views

import android.content.*
import android.util.*
import android.view.*
import android.widget.*
import androidx.core.view.*
import ru.soft.ui_kit.*
import ru.soft.ui_kit.databinding.*

class HorizontalListCheckItemView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private val b = RvItemStatusOperationsBinding.inflate(
        LayoutInflater.from(context), this, true
    )

    var text: CharSequence
        get() = b.checkbox.text
        set(value) {
            b.checkbox.text = value
        }

    var isChecked: Boolean
        get() = b.checkbox.isChecked
        set(value) {
            b.checkbox.isChecked = value
        }

    var isMultiCheckEnabled = false

    private var onCheckChangeAction: (isChecked: Boolean) -> Unit = {}

    init {
        checkAttr(context, attrs)
    }

    private fun checkAttr(context: Context, attrs: AttributeSet?) {
        val attributes =
            context.obtainStyledAttributes(attrs, R.styleable.HorizontalListCheckItemView)
        if (attributes.hasValue(R.styleable.HorizontalListCheckItemView_checkItemText)) {
            text =
                attributes.getText(R.styleable.HorizontalListCheckItemView_checkItemText)
        }
        attributes.recycle()
    }

    fun setOnCheckChangedListener(action: (isChecked: Boolean) -> Unit) {
        b.checkbox.setOnCheckedChangeListener { _, isChecked -> action(isChecked) }
        onCheckChangeAction = action
    }

    fun setIsCheckedWithoutListener(isChecked: Boolean) {
        b.checkbox.setOnCheckedChangeListener(null)
        b.checkbox.isChecked = isChecked
        b.checkbox.setOnCheckedChangeListener { _, isChecked -> onCheckChangeAction(isChecked) }
    }

    fun removeStartMargin() {
        val layoutParams = b.checkbox.layoutParams as MarginLayoutParams
        layoutParams.marginStart = 0
        b.checkbox.layoutParams = layoutParams
    }

    fun setDefaultCheckChangeListener(extraAction: (isChecked: Boolean) -> Unit = {}) {
        setOnCheckChangedListener { isChecked ->
            if (!isMultiCheckEnabled && isChecked) {
                (parent as ViewGroup).children.filter { it != this }.forEach {
                    (it as? HorizontalListCheckItemView)?.isChecked = false
                }
            }
            if (!isChecked
                && (parent as ViewGroup).children.filter { it != this }
                    .none { (it as? HorizontalListCheckItemView)?.isChecked == true }
            ) {
                this.isChecked = true
            }
            extraAction(isChecked)
        }
    }
}