package com.fabriik.common.data

data class Resource<out T>(val status: Status, val data: T?, val message: String?, val throwable: Throwable?) {
    companion object {
        fun <T> success(data: T?): Resource<T?> =
            Resource(status = Status.SUCCESS, data = data, message = null, throwable = null)

        fun <T> error(data: T? = null, message: String, throwable: Throwable? = null): Resource<T> =
            Resource(status = Status.ERROR, data = data, message = message, throwable = throwable)
    }
}