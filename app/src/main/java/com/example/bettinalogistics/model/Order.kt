package com.example.bettinalogistics.model

import com.example.bettinalogistics.utils.DataConstant
import com.example.bettinalogistics.utils.dateToString
import java.util.*

data class Order(
    var id: String? = null,
    val orderCode: String? = null,
    var productList: List<Product>? = null,
    val address: OrderAddress? = null,
    val company: UserCompany? = null,
    var statusOrder: String = DataConstant.ORDER_STATUS_PENDING,
    var statusPayment: String = DataConstant.ORDER_STATUS_PAYMENT_WAITING,
    var orderDate: String = dateToString(Calendar.getInstance().time),
    var amountBeforeDiscount: Double? = null,
    var discount: Double? = null,
    var amountAfterDiscount: Double? = null,
    var typeTransportation: String? = null,
    var methodTransport: String? = null
)

data class ConfirmOrder(
    var transportType: String?,
    var transportMethod: String?,
    var product: Product?,
    var amount: Int?,
    var status: String = DataConstant.ORDER_STATUS_PENDING,
)