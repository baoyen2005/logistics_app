package com.example.bettinalogistics.data

import android.util.Log
import com.example.bettinalogistics.model.Order
import com.example.bettinalogistics.model.Product
import com.example.bettinalogistics.utils.AppConstant
import com.example.bettinalogistics.utils.DataConstant
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions

interface FollowTrackingRepo {
    suspend fun getAllOrderByStatus(status: String, onComplete: ((List<Order>?) -> Unit)?)
}

class FollowTrackingRepoImpl : FollowTrackingRepo {
    override suspend fun getAllOrderByStatus(status: String, onComplete: ((List<Order>?) -> Unit)?) {
        val listOrder = ArrayList<Order>()
        FirebaseFirestore.getInstance().collection(AppConstant.ORDER_COLLECTION)
            .whereEqualTo(DataConstant.ORDER_STATUS, status)
            .get()
            .addOnSuccessListener { queryDocumentSnapshots: QuerySnapshot ->
                for (query in queryDocumentSnapshots) {
                    val order = query.toObject(Order::class.java)
                    listOrder.add(order)
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