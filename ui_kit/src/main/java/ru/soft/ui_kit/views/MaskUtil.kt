package ru.soft.ui_kit.views

import android.text.*
import android.widget.*
import com.redmadrobot.inputmask.*
import ru.tinkoff.decoro.*
import ru.tinkoff.decoro.slots.*
import ru.tinkoff.decoro.watchers.*

object MaskUtil {

    private const val PHONE_MASK = "+7 ([000]) [000]-[00]-[00]"

    private val PHONE_NUMBER = arrayOf(
        PredefinedSlots.hardcodedSlot('+').withTags(Slot.TAG_DECORATION),
        PredefinedSlots.hardcodedSlot(' ').withTags(Slot.TAG_DECORATION),
        PredefinedSlots.hardcodedSlot('7').withTags(Slot.TAG_DECORATION),
        PredefinedSlots.hardcodedSlot(' ').withTags(Slot.TAG_DECORATION),
        PredefinedSlots.hardcodedSlot('(').withTags(Slot.TAG_DECORATION),
        PredefinedSlots.digit(),
        PredefinedSlots.digit(),
        PredefinedSlots.digit(),
        PredefinedSlots.hardcodedSlot(')').withTags(Slot.TAG_DECORATION),
        PredefinedSlots.hardcodedSlot(' ').withTags(Slot.TAG_DECORATION),
        PredefinedSlots.digit(),
        PredefinedSlots.digit(),
        PredefinedSlots.digit(),
        PredefinedSlots.hardcodedSlot(' ').withTags(Slot.TAG_DECORATION),
        PredefinedSlots.digit(),
        PredefinedSlots.digit(),
        PredefinedSlots.hardcodedSlot(' ').withTags(Slot.TAG_DECORATION),
        PredefinedSlots.digit(),
        PredefinedSlots.digit()
    )

    class EnglishLetterValidator internal constructor(private val supportsEnglish: Boolean = true) :
        Slot.SlotValidator {
        override fun validate(value: Char): Boolean {
            return validateEnglishLetter(value)
        }

        private fun validateEnglishLetter(value: Char): Boolean {
            return supportsEnglish == isEnglishCharacter(value.code)
        }

        private fun isEnglishCharacter(charCode: Int): Boolean {
            return 'A'.code <= charCode && charCode <= 'Z'.code || 'a'.code <= charCode && charCode <= 'z'.code
        }
    }

    class EnglishLetterOrNumberValidator internal constructor(private val supportsEnglish: Boolean = true) :
        Slot.SlotValidator {
        override fun validate(value: Char): Boolean {
            return validateEnglishLetterOrNumber(value)
        }

        private fun validateEnglishLetterOrNumber(value: Char): Boolean {
            return supportsEnglish == isEnglishOrNumberCharacter(value.code, value)
        }

        private fun isEnglishOrNumberCharacter(charCode: Int, symbol: Char): Boolean {
            return ('A'.code <= charCode && charCode <= 'Z'.code || 'a'.code <= charCode && charCode <= 'z'.code)
                    || Character.isDigit(symbol)
        }
    }

    private fun getLatinSlot() = Slot(null, EnglishLetterValidator())

    private fun getLatinOrNumberSlot() = Slot(null, EnglishLetterOrNumberValidator())

    fun createPhoneNumberMask(): FormatWatcher {
        val mask = MaskImpl.createTerminated(PHONE_NUMBER)
        mask?.isForbidInputWhenFilled = true
        mask?.isShowingEmptySlots = false
        return MaskFormatWatcher(mask)
    }

    fun installPhoneMask(
        editText: EditText,
        mask: String? = PHONE_MASK,
        afterTextChanged: () -> Unit = {}
    ) {
        MaskedTextChangedListener.installOn(
            editText,
            mask ?: PHONE_MASK,
            listener = object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    afterTextChanged()
                }
            }
        )
    }

}