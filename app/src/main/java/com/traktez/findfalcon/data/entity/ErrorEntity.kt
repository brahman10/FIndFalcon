package com.traktez.findfalcon.data.entity

import com.google.gson.annotations.SerializedName

data class ErrorEntity(
    @SerializedName("error") val errorMessage:String?
)
