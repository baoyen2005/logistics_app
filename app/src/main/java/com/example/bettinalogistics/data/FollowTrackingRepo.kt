package com.example.bettinalogistics.data

import android.util.Log
import com.example.baseapp.di.Common
import com.example.bettinalogistics.di.AppData
import com.example.bettinalogistics.model.Order
import com.example.bettinalogistics.model.Track
import com.example.bettinalogistics.utils.AppConstant
import com.example.bettinalogistics.utils.AppConstant.Companion.TAG
import com.example.bettinalogistics.utils.DataConstant
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions

interface FollowTrackingRepo {
    suspend fun getAllOrderByStatusAndUser(status: String, onComplete: ((List<Order>?) -> Unit)?)
    suspend fun getAllTrackingByOrder(idOrder: String, onComplete: ((List<Track>?) -> Unit)?)
    suspend fun addOrderTrack(track: Track, onComplete: ((Boolean) -> Unit)?)
    suspend fun updateOrderTrack(track: Track, onComplete: ((Boolean) -> Unit)?)
    suspend fun deleteOrderTrack(track: Track, onComplete: ((Boolean) -> Unit)?)
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

    override suspend fun getAllTrackingByOrder(
        idOrder: String,
        onComplete: ((List<Track>?) -> Unit)?
    ) {
        FirebaseFirestore.getInstance().collection(AppConstant.TRACK_ORDER_COLLECTION)
            .whereEqualTo(DataConstant.TRACK_ORDER_ID, idOrder)
            .get()
            .addOnCompleteListener {
                if (Common.currentActivity!!.isDestroyed || Common.currentActivity!!.isFinishing) {
                    return@addOnCompleteListener
                }
                val listTrack: List<Track> = it.result.toObjects(Track::class.java)
                onComplete?.invoke(listTrack)
                if (listTrack.isEmpty()) {
                    onComplete?.invoke(null)
                } else {
                    onComplete?.invoke(listTrack)
                }
            }.addOnFailureListener {
                Log.d(AppConstant.TAG, "getAllTruckByOrder: $it")
                onComplete?.invoke(null)
            }.addOnCanceledListener {
                onComplete?.invoke(null)
            }
    }

    override suspend fun addOrderTrack(track: Track, onComplete: ((Boolean) -> Unit)?) {
        val values: HashMap<String, String?> = HashMap()
        val documentReference =
            FirebaseFirestore.getInstance().collection(AppConstant.TRACK_ORDER_COLLECTION)
                .document()
        values[DataConstant.TRACK_ID] = documentReference.id
        values[DataConstant.TRACK_ADDRESS] = track.address
        values[DataConstant.TRACK_STATUS] = track.status
        values[DataConstant.TRACK_DATE_UPDATE] = track.dateUpdate
        values[DataConstant.TRACK_ORDER_ID] = track.orderId
        values[DataConstant.TRACK_CODE] = track.trackCode
        documentReference.set(values, SetOptions.merge()).addOnCompleteListener { it ->
            if (it.isSuccessful) {
                onComplete?.invoke(true)
            } else {
                onComplete?.invoke(false)
            }
        }.addOnFailureListener {
            Log.w(TAG, "addOrderTrack: $it")
            onComplete?.invoke(false)
        }.addOnCanceledListener {
            onComplete?.invoke(false)
        }
    }

    override suspend fun updateOrderTrack(track: Track, onComplete: ((Boolean) -> Unit)?) {
        var trackGetFirebase: Track? = null
        FirebaseFirestore.getInstance().collection(AppConstant.TRACK_ORDER_COLLECTION)
            .whereEqualTo(DataConstant.TRACK_CODE, track.trackCode)
            .get()
            .addOnSuccessListener { queryDocumentSnapshots: QuerySnapshot ->
                for (query in queryDocumentSnapshots) {
                    trackGetFirebase = query.toObject(Track::class.java)
                }
                trackGetFirebase?.id?.let {
                    if (it.isNotEmpty()) {
                        val doc = track.id?.let {
                            FirebaseFirestore.getInstance()
                                .collection(AppConstant.TRACK_ORDER_COLLECTION)
                                .document(it)
                        }
                        doc?.set(track, SetOptions.merge())?.addOnSuccessListener {
                            onComplete?.invoke(true)
                        }?.addOnFailureListener {
                            onComplete?.invoke(false)
                        }
                    }
                }
            }.addOnFailureListener {
                Log.d(TAG, "updateOrderTrack: $it")
                onComplete?.invoke(false)
            }.addOnCanceledListener {
                onComplete?.invoke(false)
            }
    }

    override suspend fun deleteOrderTrack(track: Track, onComplete: ((Boolean) -> Unit)?) {
        var trackGetFirebase: Track? = null
        FirebaseFirestore.getInstance().collection(AppConstant.TRACK_ORDER_COLLECTION)
            .whereEqualTo(DataConstant.TRACK_CODE, track.trackCode)
            .get()
            .addOnSuccessListener { queryDocumentSnapshots: QuerySnapshot ->
                for (query in queryDocumentSnapshots) {
                    trackGetFirebase = query.toObject(Track::class.java)
                }
                trackGetFirebase?.id?.let {
                    if (it.isNotEmpty()) {
                        FirebaseFirestore.getInstance()
                            .collection(AppConstant.TRACK_ORDER_COLLECTION).document(
                                it
                            ).delete().addOnSuccessListener {
                                onComplete?.invoke(true)
                            }.addOnFailureListener {
                                onComplete?.invoke(false)
                            }
                    }
                }
            }.addOnFailureListener {
                Log.d(TAG, "deleteOrderTrack: $it")
                onComplete?.invoke(false)
            }.addOnCanceledListener {
                onComplete?.invoke(false)
            }
    }
}