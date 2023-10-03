package ru.soft.base_arch.utils

import android.app.*
import android.content.*
import android.os.*
import android.util.*
import androidx.biometric.*
import androidx.biometric.BiometricManager.Authenticators.*
import androidx.fragment.app.*
import java.util.concurrent.*

object BiometricUtil {

    const val TAG = "BiometricUtil"

    fun Activity.createPromptInfo(
        title: String,
        subtitle: String,
        negativeButton: String
    ): BiometricPrompt.PromptInfo {
        return BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setNegativeButtonText(negativeButton)
            .build()
    }

    fun isAvailableBiometric(context: Context): Boolean {
        val biometricManager = BiometricManager.from(context)
        return when (biometricManager.canAuthenticate(BIOMETRIC_STRONG or
                BIOMETRIC_WEAK)) {
            BiometricManager.BIOMETRIC_SUCCESS ->
                true
            else -> false
        }
    }

    fun confirmFingerPrint(
        activity: FragmentActivity,
        executor: Executor,
        promptInfo: BiometricPrompt.PromptInfo,
        onAuthenticationError: () -> Unit = {},
        onAuthenticationSucceeded: () -> Unit = {},
        onAuthenticationFailed: () -> Unit = {}
    ) {
        var biometricPrompt: BiometricPrompt? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            biometricPrompt = BiometricPrompt(activity, executor,
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        super.onAuthenticationError(errorCode, errString)
                        onAuthenticationError()
                    }

                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)
                        onAuthenticationSucceeded()
                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        onAuthenticationFailed()
                    }
                })
        }
        try {
            biometricPrompt?.authenticate(promptInfo)
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
        }
    }
}