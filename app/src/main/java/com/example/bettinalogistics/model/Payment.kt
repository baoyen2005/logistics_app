package com.example.bettinalogistics.model

data class Payment(
    var id : String? = null,
    var imgUrlPayment : String? = null,
    var contentPayment:String? = null,
    var order: Order? = null,
    var user: User? = null,
    var card: Card? = null,
    var datePayment: String? = null,
)