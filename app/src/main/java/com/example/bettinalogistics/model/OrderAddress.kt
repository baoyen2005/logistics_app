package com.example.bettinalogistics.model

data class OrderAddress(
    var address: AddressData?,
    var distance :String?,
)
data class AddressData(
    var originAddress :String?,
    var originAddressLat :Double?,
    var originAddressLon :Double?,
    var destinationAddress :String?,
    var destinationLat :Double?,
    var destinationLon :Double?,
)