package com.example.bettinalogistics.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.bettinalogistics.utils.DataConstant
import com.example.bettinalogistics.utils.dateToString
import java.util.*

data class Order(
    val code: String?,
    var product: List<Product>?,
    val address: OrderAddress?,
    val company: UserCompany?,
    var status: String = DataConstant.ORDER_STATUS_PENDING,
    var orderDate: String = dateToString(Calendar.getInstance().time),
    var amountBeforeDiscount : Double?,
    var discount: Double,
    var amountAfterDiscount: Double?,
    var typeTransportation : String?,
    var methodTransport : String?
)

data class ConfirmOrder(
    var transportType: String?,
    var transportMethod: String?,
    var product: Product?,
    var amount: Int?,
    var status: String = DataConstant.ORDER_STATUS_PENDING,
)