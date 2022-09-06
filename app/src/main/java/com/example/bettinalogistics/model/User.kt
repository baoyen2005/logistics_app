package com.example.bettinalogistics.model

import android.net.Uri
import java.util.*

data class User(
    var image: String? = null,
    var fullName: String = "",
    var phone: String = "",
    var dateOfBirth: Date? = null,
    var email: String = "",
    var password: String = "",
    var address: String = ""
)