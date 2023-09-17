package com.traktez.findfalcon.data.entity

import com.google.gson.annotations.SerializedName

data class PlanetEntity(
    var planetName: String,
    var distance: Int,
    var planetSearching: Boolean,
    var vehicleName: String?
) {
    override fun toString(): String {
        return planetName
    }
}

data class VehicleEntity(
    var vehicleName: String,
    @SerializedName("total_no")
    var totalNumber: Int,
    @SerializedName("max_distance")
    var maxDistance: Int,
    var speed: Int
) {
    override fun toString(): String {
        return vehicleName
    }
}

data class FindFalconeRequest(
    var token: String? = null,
    var planet_names: ArrayList<String>? = null,
    var vehicle_names: ArrayList<String>? = null
)

data class FindFalconeResponse(
    var planet_name: String,
    var status: String
)