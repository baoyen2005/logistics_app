package com.example.bettinalogistics.model.googlePlaceModel.directionPlaceModel

import com.squareup.moshi.Json

data class DirectionPolylineModel(
    @field:Json(name="points")

    var points: String? = null
)