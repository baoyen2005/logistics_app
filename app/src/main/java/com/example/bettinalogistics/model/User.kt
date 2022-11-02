package com.example.bettinalogistics.model

data class User(
    var uid : String? = null,
    var image: String? = null,
    var fullName: String = "",
    var phone: String = "",
    var dateOfBirth: String? = null,
    var email: String = "",
    var password: String = "",
    var address: String = "",
    var role: String = "user",
)