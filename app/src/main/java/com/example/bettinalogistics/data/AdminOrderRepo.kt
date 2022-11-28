package com.example.bettinalogistics.data

import com.example.bettinalogistics.model.Order
import com.example.bettinalogistics.utils.AppConstant
import com.example.bettinalogistics.utils.DataConstant
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

interface AdminOrderRepo {
    suspend fun getListOrderByStatus(status: String?, onComplete: ((List<Order>?) -> Unit)?)
}
class AdminOrderRepoImpl : AdminOrderRepo{
    override suspend fun getListOrderByStatus(
        status: String?,
        onComplete: ((List<Order>?) -> Unit)?
    ) {
        val listOrder: ArrayList<Order> = ArrayList()
        FirebaseFirestore.getInstance().collection(AppConstant.ORDER_COLLECTION)
            .whereEqualTo(DataConstant.ORDER_STATUS, status)
            .get()
            .addOnSuccessListener { queryDocumentSnapshots: QuerySnapshot ->
                for (query in queryDocumentSnapshots) {
                    val order = query.toObject(Order::class.java);
                    listOrder.add(order)
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

}