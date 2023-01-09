package com.example.bettinalogistics.model


data class Track(
    var id: String? = null,
    var trackCode: String? = null,
    var address: String? = null,
    var status: String? = null,
    var dateUpdate: String? = null,
    var orderId: String? = null,
    var shipper: Shipper? = null,
)