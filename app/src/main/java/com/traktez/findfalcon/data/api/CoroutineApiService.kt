package com.traktez.findfalcon.data.api

import com.traktez.findfalcon.data.entity.FindFalconeRequest
import com.traktez.findfalcon.data.entity.FindFalconeResponse
import com.traktez.findfalcon.data.entity.PlanetEntity
import com.traktez.findfalcon.data.entity.VehicleEntity
import kotlinx.coroutines.Deferred
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface CoroutineApiService {

    @GET("planets")
    fun getPlanetsAsync(): Deferred<ArrayList<PlanetEntity>>

    @GET("vehicles")
    fun getVehiclesAsync(): Deferred<ArrayList<VehicleEntity>>

    @POST("token")
    @Headers("Accept:application/json")
    fun getTokenAsync(): Deferred<FindFalconeRequest>

    @POST("find")
    @Headers("Accept:application/json", "Content-Type:application/json")
    fun findFalconeAsync(@Body requestBody: FindFalconeRequest): Deferred<FindFalconeResponse>
}