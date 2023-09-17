package com.traktez.findfalcon.data.entity

sealed class State<T> {
    class Loading<T> : State<T>()
    data class Error<T>(val errorMessage: String?, val error: Throwable, val errorCode: Int) :
        State<T>()

    data class Success<T>(var data: T) : State<T>()

    companion object {
        fun <T> loading(): State<T> = Loading()

        fun <T> error(errorMessage: String, error: Throwable, errorCode: Int): State<T> =
            Error(errorMessage, error, errorCode)

        fun <T> success(data: T): State<T> = Success(data)
    }
}