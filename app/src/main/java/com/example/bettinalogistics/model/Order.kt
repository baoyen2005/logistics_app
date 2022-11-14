package com.example.bettinalogistics.model

import com.example.bettinalogistics.utils.DataConstant
import com.example.bettinalogistics.utils.dateToString
import java.util.*

data class OrderTransaction(
    val code: String?,
    val order: Order?,
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

data class Order (
    var transportType: String? = null,
    var transportMethod: String? = null,
    var contNum: Int? = null,
    var productList: List<Product>? = null,
)