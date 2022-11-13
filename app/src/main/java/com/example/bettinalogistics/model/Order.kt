package com.example.bettinalogistics.model

import com.example.bettinalogistics.utils.DataConstant
import com.example.bettinalogistics.utils.dateToString
import java.util.*

data class OrderTransaction(
    val order: Order?,
    val address: OrderAddress?
)

data class Order (
    var status: String = DataConstant.ORDER_STATUS_PENDING,
    var orderDate: String = dateToString(Calendar.getInstance().time),
    var transportType: String? = null,
    var transportMethod: String? = null,
    var contNum: Int? = null,
    var productList: List<Product>? = null,
)