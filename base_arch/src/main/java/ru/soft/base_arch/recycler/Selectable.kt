package ru.soft.base_arch.recycler

data class Selectable<T>(
    val item: T,
    var isSelected: Boolean = false
)

fun <T> List<T>.toSelectable(selectableMapper: (T) -> Boolean = {
    false }) = map { Selectable(it, selectableMapper.invoke(it)) }
