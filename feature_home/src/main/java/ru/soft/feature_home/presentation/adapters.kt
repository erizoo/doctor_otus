package ru.soft.feature_home.presentation

import android.annotation.*
import coil.*
import ru.soft.base_arch.recycler.*
import ru.soft.base_arch.utils.*
import ru.soft.feature_home.*
import ru.soft.feature_home.data.models.*
import ru.soft.feature_home.databinding.*
import java.text.*
import java.time.*
import java.time.format.*

@SuppressLint("SetTextI18n")
fun statusCount(
    onClick: (ResponseCountStatusOrders) -> Unit
) = viewBinder<ResponseCountStatusOrders, RvItemCountOrdersBinding>(
    binder = RvItemCountOrdersBinding::bind,
    layoutResId = R.layout.rv_item_count_orders
) {
    bindView { item ->
        tvCount.text = item.count.toString()
        tvStatus.text = item.statusName
        container.setOnClickListener {
            onClick.invoke(item)
        }
    }
}

@SuppressLint("SetTextI18n", "SimpleDateFormat")
fun orders(
    onClick: (ResponseListOrders) -> Unit
) = viewBinder<ResponseListOrders, RvItemOrderBinding>(
    binder = RvItemOrderBinding::bind,
    layoutResId = R.layout.rv_item_order
) {
    bindView { item ->
        val date = item.requestDateTime?.let { it1 ->
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(
                it1
            )
        }
        tvDate.text = date?.let { it1 ->
            SimpleDateFormat("dd.MM.yy HH:mm").format(
                it1
            )
        }
        tvDescription.text = item.patient + "\n" + item.requestAdress
        tvName.text = item.number + " ${
            item.vIPStatus?.takeIf { it.isNotEmpty() }?.let { " - VIP" } ?: ""
        }" + (item.statusActiv?.takeIf { it }?.let { " - актив" } ?: "")
        root.setOnClickListener {
            onClick.invoke(item)
        }
    }
}

@SuppressLint("SetTextI18n", "SimpleDateFormat")
fun medicalServices(
    onClick: (ResponseMedicalService) -> Unit
) = viewBinder<ResponseMedicalService, RvItemMedicalServicesBinding>(
    binder = RvItemMedicalServicesBinding::bind,
    layoutResId = R.layout.rv_item_medical_services
) {
    bindView { item ->
        tvText.text = item.name
        root.setOnClickListener {
            onClick.invoke(item)
        }
    }
}

@SuppressLint("SetTextI18n", "SimpleDateFormat")
fun diagnosis(
    onClick: (ResponseDiagnosis) -> Unit
) = viewBinder<ResponseDiagnosis, RvItemMedicalServicesBinding>(
    binder = RvItemMedicalServicesBinding::bind,
    layoutResId = R.layout.rv_item_medical_services
) {
    bindView { item ->
        tvText.text = item.name
        root.setOnClickListener {
            onClick.invoke(item)
        }
    }
}

@SuppressLint("SetTextI18n", "SimpleDateFormat")
fun medicalServicesWithPrice(
    onClick: (ResponseMedicalService) -> Unit,
    onClickDeleted: (ResponseMedicalService) -> Unit
) = viewBinder<ResponseMedicalService, RvItemMedicalServicesWithPriceBinding>(
    binder = RvItemMedicalServicesWithPriceBinding::bind,
    layoutResId = R.layout.rv_item_medical_services_with_price,
    areItemsSame = { o, n -> o.gUID == n.gUID },
    areContentsSame = { o, n -> o.gUID == n.gUID },
) {
    bindView { item ->
        tvText.text = item.name + " - " + item.price.toString() + " р"
        ivTrash.setSafeOnClickListener {
            onClickDeleted.invoke(item)
        }
        root.setOnClickListener {
            onClick.invoke(item)
        }
    }
}

@SuppressLint("SetTextI18n", "SimpleDateFormat")
fun filesAdapter(
    onClick: (ResponseListTypeFiles) -> Unit,
) = viewBinder<ResponseListTypeFiles, RvItemFilesBinding>(
    binder = RvItemFilesBinding::bind,
    layoutResId = R.layout.rv_item_files,
    areItemsSame = { o, n -> o.gUID == n.gUID },
    areContentsSame = { o, n -> o.isHaveFile == n.isHaveFile },
) {
    bindView { item ->
        tvTitle.text = item.name
        tvDescription.text = item.description
        if (item.isHaveFile) {
            ivLogo.load(ru.soft.ui_atoms.R.drawable.ic_check)
        } else {
            ivLogo.load(ru.soft.ui_atoms.R.drawable.ic_plus_add_circle)
        }
        root.setSafeOnClickListener {
            onClick.invoke(item)
        }
    }
}

fun inspections(
    afterTextChanged: (ResponseOrder.InspectionDetail) -> Unit,
) = viewBinder<ResponseOrder.InspectionDetail, RvItemInspectionBinding>(
    binder = RvItemInspectionBinding::bind,
    layoutResId = R.layout.rv_item_inspection,
    areItemsSame = { o, n -> o.name == n.name },
    areContentsSame = { o, n -> o.value == n.value },
) {
    bindView { item ->
        tvTitle.text = item.name
        etValue.setTextWithoutChangeEvents(item.value)
        item.name?.let { it1 -> etValue.setHint(it1) }
        etValue.doAfterTextChanged {
            item.value = it
            afterTextChanged.invoke(item)
        }
    }
}

@SuppressLint("SetTextI18n", "SimpleDateFormat")
fun medicalServicesWithoutPrice(
    onClick: (ResponseMedicalService) -> Unit,
    onClickDeleted: (ResponseMedicalService) -> Unit
) = viewBinder<ResponseMedicalService, RvItemMedicalServicesWithPriceBinding>(
    binder = RvItemMedicalServicesWithPriceBinding::bind,
    layoutResId = R.layout.rv_item_medical_services_with_price,
    areItemsSame = { o, n -> o.gUID == n.gUID },
    areContentsSame = { o, n -> o.gUID == n.gUID },
) {
    bindView { item ->
        tvText.text = item.name
        ivTrash.setSafeOnClickListener {
            onClickDeleted.invoke(item)
        }
        root.setOnClickListener {
            onClick.invoke(item)
        }
    }
}