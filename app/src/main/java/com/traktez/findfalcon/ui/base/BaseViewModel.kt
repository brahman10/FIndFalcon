package com.traktez.findfalconeproject.ui.base

import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.traktez.findfalcon.data.entity.ErrorEntity
import com.traktez.findfalcon.data.entity.SealedResult
import com.traktez.findfalcon.data.entity.State
import retrofit2.HttpException
import javax.inject.Inject

abstract class BaseViewModel : ViewModel() {
    @Inject
    lateinit var gson: Gson

    fun <T> SealedResult<T>.toState(): State<T> {
        return when (this) {
            is SealedResult.Success -> State.success(data)
            is SealedResult.Error -> {
                var errorMessage = ""
                var code = 0
                if (exception is HttpException) {
                    code = exception.code()
                    val errorBody = exception.response()?.errorBody()
                    if (errorBody != null) {
                        errorMessage = try {
                            val type = object : TypeToken<ErrorEntity>() {}.type
                            val errorResponse: ErrorEntity =
                                gson.fromJson(errorBody.charStream(), type)
                            errorResponse.errorMessage ?: "Unknown Error"
                        } catch (e: java.lang.Exception) {
                            exception.message ?: "Unknown Error"
                        }
                    }
                }
                if (errorMessage.isBlank()) errorMessage = exception.message ?: "Unknown Error"
                State.error(errorMessage, exception, code)
            }
        }
    }

    /**
     * Use this function to clear viewModel manually if needed.
     * It will clear all the jobs inside viewModel
     */
    fun clearViewModel() {
        onCleared()
    }
}