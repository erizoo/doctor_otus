package ru.soft.feature_home.presentation

import android.content.*
import androidx.lifecycle.*
import kotlinx.coroutines.flow.*
import ru.soft.base_arch.base.*
import ru.soft.base_arch.utils.*
import ru.soft.feature_home.data.*
import ru.soft.feature_home.data.models.*
import java.util.concurrent.atomic.*
import javax.inject.*

class HomeViewModel @Inject constructor(
    private val homeRepository: HomeRepository,
    private val sharedPreferences: SharedPreferences,
) : BaseViewModel() {

    private val _listStatus = MutableSharedFlow<List<ResponseCountStatusOrders>>()
    val listStatus = _listStatus.asSharedFlow()

    private val _isErrorToken = MutableSharedFlow<Boolean>()
    val isErrorToken = _isErrorToken.asSharedFlow()

    val doctor = sharedPreferences.getString("DOCTOR", "")

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

    fun getListStatus() {
        launchJob(
            useCase = {
                homeRepository.getListStatus(
                    token = sharedPreferences.getString("TOKEN", "") ?: ""
                )
            },
            resultAction = {
                when (it.status) {
                    Status.SUCCESS -> {
                        it.data?.let { it1 -> _listStatus.emit(it1) }
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

class HomeViewModelFactory @Inject constructor(
    private val homeRepository: HomeRepository,
    private val sharedPreferences: SharedPreferences
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(homeRepository, sharedPreferences) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}