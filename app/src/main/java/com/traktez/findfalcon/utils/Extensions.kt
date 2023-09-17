package com.traktez.findfalcon.utils

import android.content.Context
import android.widget.Toast
import com.traktez.findfalcon.data.entity.SealedResult
import kotlinx.coroutines.Deferred

fun toast(context: Context, msg: String, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(context, msg, length).show()
}

suspend fun <T> requestAwait(call: () -> Deferred<T>): SealedResult<T> {
    return try {
        val result = call().await()
        SealedResult.success(result)
    } catch (exception: Exception) {
        SealedResult.error(exception)
    }
}