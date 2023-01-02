package com.example.bettinalogistics.model

data class Product (
    var productId: String? = null,
    var productDocumentId: String = "",
    var imgUri: String? = null,
    var productName : String ? = null,
    var productDes : String? = null,
    var quantity: Long? = null,
    var volume: Double? = null,
    var mass: Double?= null,
    var numberOfCarton: Long? = null,
    var isOrderLCL: Boolean = false,
    var type: TypeCommonEntity? = null,
    var contType: TypeCommonEntity? = null,
)

data class TypeCommonEntity(
    var title :String? = "",
    var descript :String? = "",
    var priceKg: Double? = 0.0,
    var priceM3 : Double? = 0.0,
    var volumeMaxOfCont: Double = 0.0,
    var quantity : Double = 0.0,
)