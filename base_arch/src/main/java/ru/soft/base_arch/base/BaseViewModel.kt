package ru.soft.base_arch.base

import android.util.*
import androidx.lifecycle.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import ru.soft.base_arch.utils.*

abstract class BaseViewModel : ViewModel() {

    protected val _successMessage = MutableSharedFlow<String?>()
    val successMessage = _successMessage.asSharedFlow()

    fun <D : Any> launchJob(
        useCase: suspend () -> Flow<Resource<D>>,
        resultAction: suspend (Resource<D>) -> Unit,
        incrementLoadingCounter: (suspend () -> Unit)? = null,
        decrementLoadingCounter: (suspend () -> Unit)? = null
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = useCase.invoke()
                response.onStart {
                    incrementLoadingCounter?.invoke()
                }.onCompletion {
                    decrementLoadingCounter?.invoke()
                }.catch { error ->
                    Log.e("NETWORK", error.message.toString())
                }.collect {
                    resultAction(it)
                }
            } catch (e: Exception) {
                Log.e("NETWORK", e.message.toString())
            }
        }
    }
}