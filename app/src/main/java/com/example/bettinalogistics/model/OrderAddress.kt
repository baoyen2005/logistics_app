package com.example.bettinalogistics.model

data class OrderAddress(
    var originAddress: String? = null,
    var originAddressLat: Double? = null,
    var originAddressLon: Double? = null,
    var destinationAddress: String? = null,
    var destinationLat: Double? = null,
    var destinationLon: Double? = null,
)