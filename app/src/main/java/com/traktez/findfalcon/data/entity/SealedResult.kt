package com.traktez.findfalcon.data.entity

sealed class SealedResult<out T> {
    data class Success<out T>(val data: T) : SealedResult<T>()
    data class Error(val exception: Exception) : SealedResult<Nothing>()

    companion object {
        fun <T> success(data: T): SealedResult<T> = Success(data)
        fun error(exception: Exception) : SealedResult<Nothing> = Error(exception)
    }
}