package com.example.bettinalogistics.model.googlePlaceModel.directionPlaceModel

import com.squareup.moshi.Json

data class EndLocationModel(
    @field:Json(name = "lat")
    var lat: Double? = null,

    @field:Json(name = "lng")
    var lng: Double? = null
) {

}