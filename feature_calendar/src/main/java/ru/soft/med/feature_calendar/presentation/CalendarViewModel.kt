package ru.soft.med.feature_calendar.presentation

import android.content.*
import androidx.lifecycle.*
import kotlinx.coroutines.flow.*
import ru.soft.base_arch.base.*
import ru.soft.base_arch.utils.*
import ru.soft.med.feature_calendar.data.*
import ru.soft.med.feature_calendar.data.models.*
import java.util.concurrent.atomic.*
import javax.inject.*

class CalendarViewModel @Inject constructor(
    private val calendarRepository: CalendarRepository,
    private val sharedPreferences: SharedPreferences,
) : BaseViewModel() {

    private val _scheduleList = MutableSharedFlow<List<ResponseSchedule>>()
    val scheduleList = _scheduleList.asSharedFlow()

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

    fun getSchedule(date: String) {
        launchJob(
            useCase = {
                calendarRepository.getSchedule(
                    token = sharedPreferences.getString("TOKEN", "") ?: "",
                    date = date
                )
            },
            resultAction = {
                when (it.status) {
                    Status.SUCCESS -> {
                        it.data?.let { it1 -> _scheduleList.emit(it1) }
                    }

                    Status.ERROR -> {}
                    else -> {}
                }
            },
            incrementLoadingCounter = ::incrementLoadingCounter,
            decrementLoadingCounter = ::decrementLoadingCounter
        )
    }
}

class CalendarViewModelFactory @Inject constructor(
    private val calendarRepository: CalendarRepository,
    private val sharedPreferences: SharedPreferences
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CalendarViewModel::class.java)) {
            return CalendarViewModel(calendarRepository, sharedPreferences) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}