package ru.soft.feature_home.presentation.list_orders

import android.content.SharedPreferences
import androidx.lifecycle.*
import kotlinx.coroutines.flow.*
import ru.soft.base_arch.base.*
import ru.soft.base_arch.utils.*
import ru.soft.feature_home.data.*
import ru.soft.feature_home.data.models.*
import java.util.concurrent.atomic.*
import javax.inject.*

class ListOrdersViewModel @Inject constructor(
    private val homeRepository: HomeRepository,
    private val sharedPreferences: SharedPreferences,
) : BaseViewModel() {

    private val _listOrders = MutableSharedFlow<List<ResponseListOrders>>()
    val listOrders = _listOrders.asSharedFlow()

    private val _isErrorToken = MutableSharedFlow<Boolean>()
    val isErrorToken = _isErrorToken.asSharedFlow()

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

    val listResult = mutableListOf<ResponseListOrders>()

    fun getOrders(status: String) {
        launchJob(
            useCase = {
                homeRepository.getOrders(
                    status = status,
                    token = sharedPreferences.getString("TOKEN", "") ?: ""
                )
            },
            resultAction = {
                when (it.status) {
                    Status.SUCCESS -> {
                        listResult.clear()
                        it.data?.let { it1 -> listResult.addAll(it1) }
                        it.data?.let { it1 -> _listOrders.emit(it1) }
                    }

                    Status.ERROR -> {
                        if (it.statusCode == 401) {
                            sharedPreferences.edit().putString("TOKEN", "").apply()
                            _isErrorToken.emit(true)
                        }
                    }
                }
            },
            incrementLoadingCounter = ::incrementLoadingCounter,
            decrementLoadingCounter = ::decrementLoadingCounter
        )
    }
}


class ListOrdersViewModelFactory @Inject constructor(
    private val homeRepository: HomeRepository,
    private val sharedPreferences: SharedPreferences,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ListOrdersViewModel::class.java)) {
            return ListOrdersViewModel(homeRepository, sharedPreferences) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}