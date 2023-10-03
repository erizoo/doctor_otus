package ru.soft.base_arch.utils

import android.annotation.*
import android.content.*
import android.graphics.*
import android.net.*
import android.os.*
import android.text.*
import android.text.style.*
import android.util.*
import android.view.*
import android.widget.*
import androidx.core.widget.*
import androidx.fragment.app.*
import androidx.lifecycle.*
import com.caverock.androidsvg.*
import kotlinx.coroutines.*
import retrofit2.*
import java.text.*
import kotlin.properties.*
import kotlin.reflect.*


inline fun Fragment.launchAndRepeatWithViewLifecycle(
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    crossinline block: suspend CoroutineScope.() -> Unit
) {
    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.lifecycle.repeatOnLifecycle(minActiveState) { block() }
    }
}

@Throws(IllegalArgumentException::class)
fun String.convertStringToBitmap(): Bitmap? {
    val decodedString = Base64.decode(this, Base64.DEFAULT)
    val svgString = String(decodedString, Charsets.UTF_8)

    val svg = SVG.getFromString(svgString)

    val bitmap = Bitmap.createBitmap(
        svg.documentWidth.toInt(),
        svg.documentHeight.toInt(),
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    svg.renderToCanvas(canvas)

    return bitmap
}

inline fun <reified T> argument(
    key: String,
    defaultValue: T? = null
): ReadWriteProperty<Fragment, T> =
    ExtractorDelegate { thisRef ->
        extractFromBundle(
            bundle = thisRef.arguments,
            key = key,
            defaultValue = defaultValue
        )
    }

inline fun <reified T> extractFromBundle(
    bundle: Bundle?,
    key: String,
    defaultValue: T? = null
): T {
    val result = bundle?.get(key) ?: defaultValue
    if (result != null && result !is T) {
        throw ClassCastException("Property $key has different class type")
    }
    return result as T
}

class ExtractorDelegate<R, T>(private val initializer: (R) -> T) : ReadWriteProperty<R, T> {

    private object EMPTY

    private var value: Any? = EMPTY

    override fun setValue(thisRef: R, property: KProperty<*>, value: T) {
        this.value = value
    }

    override fun getValue(thisRef: R, property: KProperty<*>): T {
        if (value == EMPTY) {
            value = initializer(thisRef)
        }
        @Suppress("UNCHECKED_CAST")
        return value as T
    }
}

fun <T> handleResponse(response: Response<T>): Resource<T> {
    return if (response.isSuccessful) {
        Resource.success(data = response.body(), code = response.code())
    } else {
        Resource.error(
            "Error: ${response.errorBody()?.string() ?: "Ошибка"}",
            null,
            code = response.code()
        )
    }
}

fun View.makeVisible() {
    visibility = View.VISIBLE
}

fun View.makeInVisible() {
    visibility = View.INVISIBLE
}

fun View.makeGone() {
    visibility = View.GONE
}

fun View.setSafeOnClickListener(onSafeClick: (View) -> Unit) {
    val safeClickListener = SafeClickListener {
        onSafeClick(it)
    }
    setOnClickListener(safeClickListener)
}

@SuppressLint("SimpleDateFormat")
fun String.parseDate(format: SimpleDateFormat): String {
    val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(this)
    return format.format(date)
}

@SuppressLint("SimpleDateFormat")
fun Long.parseDateToServer(): String {
    return SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(this)
}

@SuppressLint("SimpleDateFormat")
fun Long.parseDateToServerWithoutTime(): String {
    return SimpleDateFormat("yyyy-MM-dd").format(this)
}

fun String.callPhoneNumber(context: Context) {
    val intent = Intent(Intent.ACTION_DIAL, Uri.parse(this))
    context.startActivity(intent)
}

fun makeLink(
    text: String,
    phrase: String,
    isUnderlineText: Boolean = false,
    listener: View.OnClickListener,
): SpannableString {
    val spannableString = SpannableString(text)
    val clickableSpanFirstPhrase = object : ClickableSpan() {
        override fun updateDrawState(ds: TextPaint) {
            ds.color = Color.parseColor("#2D7DF5")      // you can use custom color
            ds.isUnderlineText = isUnderlineText  // this remove the underline
        }

        override fun onClick(view: View) {
            listener.onClick(view)
        }
    }
    val startFirstPhrase = text.indexOf(phrase)
    val endFirstPhrase = startFirstPhrase + phrase.length
    spannableString.setSpan(
        clickableSpanFirstPhrase,
        startFirstPhrase,
        endFirstPhrase,
        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    return spannableString
}


