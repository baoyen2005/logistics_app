package com.example.bettinalogistics.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.example.bettinalogistics.utils.DataConstant
import com.example.bettinalogistics.utils.dateToString
import java.util.*

data class Order(
    val code: String?,
    var productOrder: ProductOrder?,
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
// luu list duoi database-sql lite
@Entity
class OrderedProduct{
    @PrimaryKey(autoGenerate = true) var id = 0
}

data class ProductOrder(
    @Embedded val orderedProduct: OrderedProduct,
    @Relation(
        parentColumn = "id",
        entityColumn = "orderProductId"
    )
    var productList: List<Product>?
)

data class ConfirmOrder(
    var transportType: String?,
    var transportMethod: String?,
    var product: Product?,
    var amount: Int?,
    var status: String = DataConstant.ORDER_STATUS_PENDING,
)