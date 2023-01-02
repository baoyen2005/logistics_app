package com.example.bettinalogistics.model

data class Card(
    var id: String? = null,
    var user: User? = null,
    var name: String? = null,
    var accountNumber: String? = null,
    var accountName: String? = null,
    var cardNumber: String? = null,
    var dateOfExpired: String? = null,
)