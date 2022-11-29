package com.example.bettinalogistics.data

import android.util.Log
import com.example.bettinalogistics.di.AppData
import com.example.bettinalogistics.model.Order
import com.example.bettinalogistics.model.Product
import com.example.bettinalogistics.utils.AppConstant
import com.example.bettinalogistics.utils.DataConstant
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions

interface FollowTrackingRepo {
    suspend fun getAllOrderByStatusAndUser(status: String, onComplete: ((List<Order>?) -> Unit)?)
}

class FollowTrackingRepoImpl : FollowTrackingRepo {
    override suspend fun getAllOrderByStatusAndUser(status: String, onComplete: ((List<Order>?) -> Unit)?) {
        val listOrder = ArrayList<Order>()
        FirebaseFirestore.getInstance().collection(AppConstant.ORDER_COLLECTION)
            .whereEqualTo(DataConstant.USER_ID, AppData.g().userId)
            .get()
            .addOnSuccessListener { queryDocumentSnapshots: QuerySnapshot ->
                for (query in queryDocumentSnapshots) {
                    val order = query.toObject(Order::class.java)
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
               onComplete?.invoke(listOrder)
            }.addOnFailureListener {
                Log.d(AppConstant.TAG, "getAllOrderByStatus: $it")
                onComplete?.invoke(null)
            }.addOnCanceledListener {
                onComplete?.invoke(null)
            }
    }
}