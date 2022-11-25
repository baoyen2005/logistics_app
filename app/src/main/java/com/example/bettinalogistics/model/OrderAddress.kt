package com.example.bettinalogistics.model

data class OrderAddress(
    var originAddress :String?,
    var originAddressLat :Double?,
    var originAddressLon :Double?,
    var destinationAddress :String?,
    var destinationLat :Double?,
    var destinationLon :Double?,
)