package ru.soft.med.feature_calendar.presentation

import android.annotation.*
import ru.soft.base_arch.recycler.*
import ru.soft.base_arch.utils.*
import ru.soft.med.feature_calendar.*
import ru.soft.med.feature_calendar.data.models.*
import ru.soft.med.feature_calendar.databinding.*
import java.text.*

@SuppressLint("SetTextI18n", "SimpleDateFormat")
fun schedule(
    onClick: (ResponseSchedule) -> Unit
) = viewBinder<ResponseSchedule, RvItemScheduleBinding>(
    binder = RvItemScheduleBinding::bind,
    layoutResId = R.layout.rv_item_schedule
) {
    bindView { item ->
        tvValue.text =
            "${item.timeType}: с ${item.startTime?.parseDate(SimpleDateFormat("HH-mm"))} до ${
                item.endTime?.parseDate(SimpleDateFormat("HH-mm"))
            }"
        root.setOnClickListener {
            onClick.invoke(item)
        }
    }
}