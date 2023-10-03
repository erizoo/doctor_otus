package ru.soft.med.feature_calendar.presentation

import android.os.*
import android.view.*
import android.widget.*
import androidx.core.view.*
import androidx.fragment.app.*
import com.github.terrakok.cicerone.androidx.*
import ru.soft.base_arch.base.*
import ru.soft.base_arch.recycler.*
import ru.soft.base_arch.utils.*
import ru.soft.core_api.mediator.*
import ru.soft.med.feature_calendar.R
import ru.soft.med.feature_calendar.databinding.*
import ru.soft.med.feature_calendar.di.*
import java.util.*
import javax.inject.*

class CalendarFragment : BaseFragment(R.layout.fragment_calendar) {

    private val binding by viewBinding(FragmentCalendarBinding::bind)
    private var selectedYear = Calendar.getInstance().get(Calendar.YEAR)
    private var selectedMonth = Calendar.getInstance().get(Calendar.MONTH)
    private var selectedDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

    @Inject
    lateinit var viewModelFactory: CalendarViewModelFactory
    private val viewModel by viewModels<CalendarViewModel> { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CalendarComponent.create((requireActivity().application as AppWithFacade).getFacade())
            .inject(this)
    }

    private fun formatNumber(number: Int): String {
        return String.format("%02d", number)
    }

    private val scheduleAdapter by lazy {
        MultiBindingAdapter(
            schedule {

            }
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvItem.adapter = scheduleAdapter

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            binding.datePicker.setOnDateChangedListener { _, year, monthOfYear, dayOfMonth ->
                if (selectedYear != year ||
                    selectedMonth != monthOfYear ||
                    selectedDay != dayOfMonth) {

                    selectedYear = year
                    selectedMonth = monthOfYear
                    selectedDay = dayOfMonth

                    // Здесь ваш код для реакции на изменение даты
                    val selectedDate = "$selectedYear${formatNumber(selectedMonth + 1)}${formatNumber(selectedDay)}"
                    viewModel.getSchedule(
                        date = selectedDate
                    )
                }
            }
        }

        launchAndRepeatWithViewLifecycle {
            viewModel.loading.collect {
                binding.layoutProgress.layoutProgress.isVisible = it
            }
        }
        launchAndRepeatWithViewLifecycle {
            viewModel.scheduleList.collect {
                scheduleAdapter.items = it
            }
        }
    }
}