package com.example.bettinalogistics.model

import android.net.Uri

data class Order (
    var imgUri: Uri? = null,
    var productName : String ? = null,
    var productDes : String? = null,
    var quantity: Long? = null,
    var volume: Double? = null,
    var mass: Double?= null,
    var numberOfCarton: Long? = null,
    var isOrderLCL: Boolean = true
    )