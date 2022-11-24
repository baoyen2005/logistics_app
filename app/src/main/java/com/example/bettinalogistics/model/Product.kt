package com.example.bettinalogistics.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Product")
data class Product (
    var imgUri: String? = null,
    var productName : String ? = null,
    var productDes : String? = null,
    var quantity: Long? = null,
    var volume: Double? = null,
    var mass: Double?= null,
    var numberOfCarton: Long? = null,
    var isOrderLCL: Boolean = true,
    var type: String? = null,
    var contType: String? = null,
    val orderProductId:Int = -1
){
    @PrimaryKey(autoGenerate = true) var id = 0
}