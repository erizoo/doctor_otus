package ru.soft.base_arch.utils

data class Resource<out T>(
    val status: Status,
    val data: T?,
    val message: String?,
    val statusCode: Int?
) {
    companion object {
        fun <T> success(data: T?, code: Int): Resource<T> {
            return Resource(Status.SUCCESS, data, null, code)
        }

        fun <T> error(msg: String, data: T?, code: Int): Resource<T & Any> {
            return Resource(Status.ERROR, data, msg, code)
        }
    }
}

enum class Status {
    SUCCESS,
    ERROR
}
