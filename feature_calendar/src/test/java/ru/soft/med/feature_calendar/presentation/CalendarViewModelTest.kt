package ru.soft.med.feature_calendar.presentation

import android.content.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.mockito.*
import org.mockito.Mockito.*
import ru.soft.base_arch.utils.*
import ru.soft.med.feature_calendar.data.*
import ru.soft.med.feature_calendar.data.models.*

@ExperimentalCoroutinesApi
class CalendarViewModelTest {

    @Mock
    private lateinit var calendarRepository: CalendarRepository

    @Mock
    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var viewModel: CalendarViewModel

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)
        viewModel = CalendarViewModel(calendarRepository, sharedPreferences)
    }

    @Test
    fun testGetScheduleShouldEmitScheduleListOnSuccess(): Unit = runBlocking {
        // Arrange
        val date = "2023-09-21"
        val fakeScheduleList = mutableListOf<ResponseSchedule>().apply {
            this.add(
                ResponseSchedule(
                    date = "",
                    endTime = "",
                    startTime = "",
                    timeType = ""
                )
            )
        }
        val response = Resource(
            status = Status.SUCCESS,
            data = fakeScheduleList,
            message = "success",
            statusCode = 200,
        )

        `when`(sharedPreferences.getString("TOKEN", "")).thenReturn("fake_token")
        `when`(calendarRepository.getSchedule("fake_token", date)).thenReturn(flow {
            emit(response)
        })

        // Act
        viewModel.getSchedule(date)

        viewModel.scheduleList
            .take(1)
            .collect { emittedList ->
                assertEquals(fakeScheduleList, emittedList)
            }
    }
}