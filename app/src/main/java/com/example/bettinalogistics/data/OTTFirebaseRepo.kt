package com.example.bettinalogistics.data

import android.util.Log
import com.example.baseapp.di.Common
import com.example.bettinalogistics.di.AppData
import com.example.bettinalogistics.model.Notification
import com.example.bettinalogistics.model.OttRequest
import com.example.bettinalogistics.model.OttResponse
import com.example.bettinalogistics.model.TokenOtt
import com.example.bettinalogistics.utils.AppConstant
import com.example.bettinalogistics.utils.DataConstant
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

interface OTTFirebaseRepo {
    suspend fun getAllToken(onComplete: ((List<TokenOtt>?) -> Unit)?)
    suspend fun getAllNotification(role: String,onComplete: ((List<Notification>?) -> Unit)?)
    suspend fun sendNotiRequestFirebase(notification: Notification, onComplete: ((Boolean) -> Unit)?)
    suspend fun sendOttServer(ottRequest: OttRequest, onComplete: ((OttResponse?) -> Unit)?)
}

class OttFirebaseRepoImpl : OTTFirebaseRepo {
    override suspend fun getAllToken(onComplete: ((List<TokenOtt>?) -> Unit)?) {
        val listToken: ArrayList<TokenOtt> = ArrayList()
        FirebaseFirestore.getInstance().collection(AppConstant.TOKEN_OTT)
            .get()
            .addOnSuccessListener { queryDocumentSnapshots: QuerySnapshot ->
                for (query in queryDocumentSnapshots) {
                    val order = query.toObject(TokenOtt::class.java);
                    listToken.add(order)
                }
                if (listToken.isNotEmpty()) {
                    onComplete?.invoke(listToken)
                } else {
                    onComplete?.invoke(null)
                }
            }
            .addOnFailureListener {
                onComplete?.invoke(null)
            }
    }

    override suspend fun getAllNotification(role: String, onComplete: ((List<Notification>?) -> Unit)?) {
        val listToken: ArrayList<Notification> = ArrayList()
        FirebaseFirestore.getInstance().collection(AppConstant.SEND_NOTIFICATION)
            .whereEqualTo(DataConstant.PERSON_RECEIVER_NOTIFICATION,role)
            .get()
            .addOnSuccessListener { queryDocumentSnapshots: QuerySnapshot ->
                for (query in queryDocumentSnapshots) {
                    val order = query.toObject(Notification::class.java);
                    listToken.add(order)
                }
                if (listToken.isNotEmpty()) {
                    onComplete?.invoke(listToken)
                } else {
                    onComplete?.invoke(null)
                }
            }
            .addOnFailureListener {
                onComplete?.invoke(null)
            }
    }

    override suspend fun sendNotiRequestFirebase(
        notification: Notification,
        onComplete: ((Boolean) -> Unit)?
    ) {
        val documentReference = FirebaseFirestore.getInstance().collection(AppConstant.SEND_NOTIFICATION)
            .document()
        val values: HashMap<String, Any?> = HashMap()
        values[DataConstant.ID_NOTIFICATION] = documentReference.id
        values[DataConstant.CONTENT_NOTIFICATION] = notification.contentNoti ?: ""
        values[DataConstant.ORDER_NOTIFICATION] = notification.order
        values[DataConstant.CONFIRM_DATE_NOTIFICATION] = notification.confirmDate
        values[DataConstant.PERSON_RECEIVER_NOTIFICATION] = notification.notiTo
        values[DataConstant.USER_ID] = AppData.g().userId
        values[DataConstant.TIME_ESTIMATE_NOTIFICATION] = notification.timeTransactionEstimate

        documentReference.set(values, SetOptions.merge()).addOnCompleteListener { it ->
            if (it.isSuccessful) {
                if (Common.currentActivity!!.isDestroyed || Common.currentActivity!!.isFinishing) {
                    return@addOnCompleteListener
                } else onComplete?.invoke(true)
            } else {
                onComplete?.invoke(false)
            }
        }
    }

    override suspend fun sendOttServer(
        ottRequest: OttRequest,
        onComplete: ((OttResponse?) -> Unit)?
    ) {
        val apiService = ServiceBuilder.getInstance().create(OttFirebaseApi::class.java)
        val call = apiService.sendOttFirebase(ottRequest)
        Log.d(AppConstant.TAG, "sendOtt: $ottRequest")
        call.enqueue(object : Callback<OttResponse?> {
            override fun onResponse(call: Call<OttResponse?>, response: Response<OttResponse?>) {
                val statusCode: Int = response.code()
                Log.d(AppConstant.TAG, "onResponse: $statusCode")
                val ottResponse = response.body()
                onComplete?.invoke(ottResponse)
                // Catching Responses From Retrofit
                Log.d("TAG", "onResponseisSuccessful: " + response.isSuccessful());
                Log.d("TAG", "onResponsebody: " + response.body());
                Log.d("TAG", "onResponseerrorBody: " + response.errorBody());
                Log.d("TAG", "onResponsemessage: " + response.message());
                Log.d("TAG", "onResponsecode: " + response.code());
                Log.d("TAG", "onResponseheaders: " + response.headers());
                Log.d("TAG", "onResponseraw: " + response.raw());
                Log.d("TAG", "onResponsetoString: " + response.toString());
            }

            override fun onFailure(call: Call<OttResponse?>, t: Throwable) {
                Log.d(AppConstant.TAG, "onFailure: $t")
                Log.d("TAG", "onFailuregetLocalizedMessage: " + t.getLocalizedMessage());
                Log.d("TAG", "onFailuregetMessage: " + t.message);
                Log.d("TAG", "onFailuretoString: " + t.toString());
                Log.d("TAG", "onFailurefillInStackTrace: " + t.fillInStackTrace());
                Log.d("TAG", "onFailuregetCause: " + t.cause);
                Log.d("TAG", "onFailuregetStackTrace: " + Arrays.toString(t.getStackTrace()));
                Log.d("TAG", "getSuppressed: " + Arrays.toString(t.getSuppressed()));
            }
        })
    }
}