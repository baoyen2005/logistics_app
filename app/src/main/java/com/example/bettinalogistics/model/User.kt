package com.example.bettinalogistics.model

data class User(
    var uid : String? = null,
    var image: String? = null,
    var fullName: String ?= null,
    var phone: String? = null,
    var dateOfBirth: String? = null,
    var email: String? = null,
    var password: String? =null,
    var address: String? = null,
    var role: String = "user",
)