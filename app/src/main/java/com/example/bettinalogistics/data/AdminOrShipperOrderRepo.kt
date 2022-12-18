package com.example.bettinalogistics.data

import com.example.bettinalogistics.model.Order
import com.example.bettinalogistics.utils.AppConstant
import com.example.bettinalogistics.utils.DataConstant
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions

interface AdminOrShipperOrderRepo {
    suspend fun getListOrderByStatus(status: String?, onComplete: ((List<Order>?) -> Unit)?)
    suspend fun updateOrderToDelivering(order: Order, onComplete: ((Boolean) -> Unit))
}
class AdminOrShipperOrderRepoImpl : AdminOrShipperOrderRepo{
    override suspend fun getListOrderByStatus(
        status: String?,
        onComplete: ((List<Order>?) -> Unit)?
    ) {
        val listOrder: ArrayList<Order> = ArrayList()
        FirebaseFirestore.getInstance().collection(AppConstant.ORDER_COLLECTION)
            .get()
            .addOnSuccessListener { queryDocumentSnapshots: QuerySnapshot ->
                for (query in queryDocumentSnapshots) {
                    val order = query.toObject(Order::class.java);
                    if(status == DataConstant.ORDER_STATUS_PAYMENT_WAITING || status == DataConstant.ORDER_STATUS_PAYMENT_PAID)
                    {
                        if(order.statusPayment == status)
                        {
                            listOrder.add(order)
                        }
                    }
                    else{
                        if(order.statusOrder == status)
                        {
                            listOrder.add(order)
                        }
                    }
                }
                if (listOrder.isNotEmpty()) {
                    onComplete?.invoke(listOrder)
                } else {
                    onComplete?.invoke(null)
                }
            }
            .addOnFailureListener {
                onComplete?.invoke(null)
            }
    }

    override suspend fun updateOrderToDelivering(order: Order, onComplete: (Boolean) -> Unit) {
        val doc = order.id?.let {
            FirebaseFirestore.getInstance().collection(AppConstant.ORDER_COLLECTION)
                .document(it)
        }
        doc?.set(order, SetOptions.merge())?.addOnSuccessListener {
            onComplete.invoke(true)
        }?.addOnFailureListener {
            onComplete.invoke(false)
        }
    }

}