package ru.soft.feature_auth.presentation

import android.content.*
import androidx.lifecycle.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import ru.soft.base_arch.base.*
import ru.soft.base_arch.utils.*
import ru.soft.feature_auth.data.*
import ru.soft.feature_auth.data.models.*
import java.util.concurrent.atomic.*
import javax.inject.*

private const val TOKEN = "TOKEN"
private const val IS_LOGIN = "IS_LOGIN"
private const val DOCTOR = "DOCTOR"
private const val FIREBASE_TOKEN = "FIREBASE_TOKEN"

class AuthViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val sharedPreferencesEditor: SharedPreferences.Editor,
    private val sharedPreferences: SharedPreferences
) : BaseViewModel() {

    private val _isSuccessSent = MutableSharedFlow<Boolean>()
    val isSuccessSent = _isSuccessSent.asSharedFlow()

    private val _isErrorSent = MutableSharedFlow<String>()
    val isErrorSent = _isErrorSent.asSharedFlow()

    private val _isSuccessCheckedCode = MutableSharedFlow<Boolean>()
    val isSuccessCheckedCode = _isSuccessCheckedCode.asSharedFlow()

    private val loadingCounter = AtomicInteger(0)
    private val _loading = MutableStateFlow(0)
    val loading: Flow<Boolean>
        get() = _loading.map { it != 0 }

    private fun incrementLoadingCounter() {
        _loading.tryEmit(loadingCounter.incrementAndGet())
    }

    private fun decrementLoadingCounter() {
        _loading.tryEmit(loadingCounter.decrementAndGet())
    }

    private fun sendFirebaseToken(token: String, tokenFirebase: String) {
        launchJob(
            useCase = {
                repository.sendFirebaseToken(
                    body = RequestTokenFirebase(
                        token = token,
                        tokenFairBase = tokenFirebase
                    )
                )
            },
            resultAction = {
                when (it.status) {
                    Status.SUCCESS -> {
                        _isSuccessCheckedCode.emit(true)
                    }

                    Status.ERROR -> {
                        _isSuccessCheckedCode.emit(true)
                    }
                    else -> {
                        _isSuccessCheckedCode.emit(true)
                    }
                }
            },
            incrementLoadingCounter = ::incrementLoadingCounter,
            decrementLoadingCounter = ::decrementLoadingCounter
        )
    }

    fun login(phone: String) {
        launchJob(
            useCase = {
                repository.login(phone = phone)
            },
            resultAction = {
                when (it.status) {
                    Status.SUCCESS -> {
                        _isSuccessSent.emit(true)
                    }

                    Status.ERROR -> {
                        _isErrorSent.emit(it.message.toString())
                    }

                    else -> {}
                }
            },
            incrementLoadingCounter = ::incrementLoadingCounter,
            decrementLoadingCounter = ::decrementLoadingCounter
        )
    }

    fun checkSmsCode(phone: String, code: String) {
        launchJob(
            useCase = {
                repository.checkSmsCode(phone = phone, smsCode = code)
            },
            resultAction = {
                when (it.status) {
                    Status.SUCCESS -> {
                        sharedPreferencesEditor.putString(TOKEN, it.data?.get(0)?.token)
                        sharedPreferencesEditor.putString(DOCTOR, it.data?.get(0)?.doctor)
                        sharedPreferencesEditor.putBoolean(IS_LOGIN, true)
                        sharedPreferencesEditor.apply()
                        sharedPreferences.getString(FIREBASE_TOKEN, "")?.let { it1 ->
                            it.data?.get(0)?.token?.let { it2 ->
                                sendFirebaseToken(
                                    it2,
                                    it1
                                )
                            }
                        }
                    }

                    Status.ERROR -> {
                        _isErrorSent.emit(it.message.toString())
                    }

                    else -> {}
                }
            },
            incrementLoadingCounter = ::incrementLoadingCounter,
            decrementLoadingCounter = ::decrementLoadingCounter
        )
    }
}

class AuthViewModelFactory @Inject constructor(
    private val authRepository: AuthRepository,
    private val sharedPreferencesEditor: SharedPreferences.Editor,
    private val sharedPreferences: SharedPreferences
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(authRepository, sharedPreferencesEditor, sharedPreferences) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}