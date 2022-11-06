package com.example.nearmekotlindemo.models.googlePlaceModel


import com.example.bettinalogistics.model.googlePlaceModel.LocationModel
import com.squareup.moshi.Json

data class GeometryModel(
    @field:Json(name = "location")
    val location: LocationModel?
)