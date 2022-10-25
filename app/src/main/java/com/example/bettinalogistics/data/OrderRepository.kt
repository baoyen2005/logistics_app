package com.example.bettinalogistics.data

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.bettinalogistics.di.AppData
import com.example.bettinalogistics.model.Order
import com.example.bettinalogistics.utils.AppConstant.Companion.ORDER_COLLECTION
import com.example.bettinalogistics.utils.AppConstant.Companion.ORDER_IMAGE_STORAGE
import com.example.bettinalogistics.utils.AppConstant.Companion.TAG
import com.example.bettinalogistics.utils.DataConstant.Companion.PRODUCT_DES
import com.example.bettinalogistics.utils.DataConstant.Companion.PRODUCT_IMAGE_URL
import com.example.bettinalogistics.utils.DataConstant.Companion.PRODUCT_IS_ORDER_LCL
import com.example.bettinalogistics.utils.DataConstant.Companion.PRODUCT_MASS
import com.example.bettinalogistics.utils.DataConstant.Companion.PRODUCT_NAME
import com.example.bettinalogistics.utils.DataConstant.Companion.PRODUCT_NUMBER_OF_CARTON
import com.example.bettinalogistics.utils.DataConstant.Companion.PRODUCT_ORDER_DATE
import com.example.bettinalogistics.utils.DataConstant.Companion.PRODUCT_QUANTITY
import com.example.bettinalogistics.utils.DataConstant.Companion.PRODUCT_STATUS
import com.example.bettinalogistics.utils.DataConstant.Companion.PRODUCT_VOLUME
import com.example.bettinalogistics.utils.DataConstant.Companion.USER_ID
import com.example.bettinalogistics.utils.dateToString
import com.example.bettinalogistics.utils.stringToDate
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage


interface OrderRepository {
    suspend fun getAllOrder(): MutableLiveData<List<Order>?>
    suspend fun addOrder(order: Order,onComplete: ((MutableLiveData<Boolean>) -> Unit)?)
}

class OrderRepositoryImpl : OrderRepository {
    companion object{
        val documentReference = FirebaseFirestore.getInstance().collection(ORDER_COLLECTION)
            .document()
    }

    private var addOrderLiveData = MutableLiveData<Boolean>()
    private var getAllOrdersLiveData = MutableLiveData<List<Order>?>()

    override suspend fun getAllOrder(): MutableLiveData<List<Order>?> {
        val listOrder: ArrayList<Order> = ArrayList()
        FirebaseFirestore.getInstance().collection(ORDER_COLLECTION)
            .get()
            .addOnSuccessListener { queryDocumentSnapshots: QuerySnapshot ->
                for (query in queryDocumentSnapshots) {
                    val imgUri = Uri.parse(query[PRODUCT_IMAGE_URL].toString())
                    val productName = query[PRODUCT_NAME].toString()
                    val productDes = query[PRODUCT_DES].toString()
                    val quantity = query[PRODUCT_QUANTITY].toString()
                    val volume = query[PRODUCT_VOLUME].toString()
                    val mass = query[PRODUCT_MASS].toString()
                    val numberOfCarton = query[PRODUCT_NUMBER_OF_CARTON].toString()
                    val isOrderLCL = query[PRODUCT_IS_ORDER_LCL].toString()
                    val status = query[PRODUCT_STATUS].toString()
                    val orderDate = query[PRODUCT_ORDER_DATE].toString()
                    if (quantity.isNotEmpty() && volume.isNotEmpty() && mass.isNotEmpty() && numberOfCarton.isNotEmpty()) {
                        val order = Order(
                            imgUri = imgUri,
                            productName = productName,
                            productDes = productDes,
                            quantity = quantity.toLong(),
                            volume = volume.toDouble(),
                            mass = mass.toDouble(),
                            numberOfCarton = numberOfCarton.toLong(),
                            isOrderLCL = isOrderLCL.toBoolean(),
                            status = status,
                            orderDate = stringToDate(orderDate)
                        )
                        listOrder.add(order)
                    }
                    else{
                        Log.d(TAG, "getAllOrder: gia tri null")
                    }
                }
                if (listOrder.isNotEmpty()) {
                    getAllOrdersLiveData.postValue(listOrder)
                } else {
                    getAllOrdersLiveData.postValue(listOf())
                }
            }
            .addOnFailureListener {
                getAllOrdersLiveData.postValue(listOf())
            }
        return getAllOrdersLiveData
    }

    override suspend fun addOrder(order: Order,onComplete: ((MutableLiveData<Boolean>) -> Unit)?){
        val values: HashMap<String, String?> = HashMap()
        values[PRODUCT_IMAGE_URL] = ""
        values[PRODUCT_NAME] = order.productName
        values[PRODUCT_DES] = order.productDes
        values[PRODUCT_QUANTITY] = order.quantity.toString()
        values[PRODUCT_VOLUME] = order.volume.toString()
        values[PRODUCT_MASS] = order.mass.toString()
        values[PRODUCT_NUMBER_OF_CARTON] = order.numberOfCarton.toString()
        values[PRODUCT_IS_ORDER_LCL] = order.isOrderLCL.toString()
        values[PRODUCT_STATUS] = order.status
        values[PRODUCT_ORDER_DATE] = order.orderDate?.let { dateToString(it) }
        values[USER_ID] = AppData.g().userId
        documentReference.set(values, SetOptions.merge()).addOnCompleteListener {
            if (it.isSuccessful) {
                if (order.imgUri != null) {
                    upLoadPhotos(order.imgUri!!, documentReference.id,onComplete)
                } else {
                    addOrderLiveData.value = false
                    onComplete?.invoke(addOrderLiveData)
                }
            } else {
                addOrderLiveData.value = false
                onComplete?.invoke(addOrderLiveData)
            }
        }

    }

    private fun upLoadPhotos(uri: Uri, orderId: String,onComplete: ((MutableLiveData<Boolean>) -> Unit)?) {
        val storage = FirebaseStorage.getInstance()
        val name = "product_image"
        val storageRef = storage.reference.child(ORDER_IMAGE_STORAGE).child(orderId).child(name)
        val uploadTask = storageRef.putFile(uri)
        uploadTask.addOnSuccessListener {
            storageRef.downloadUrl
                .addOnSuccessListener { url: Uri ->
                    updateProductImage(url.toString(), orderId,onComplete)
                }

        }
        uploadTask.addOnFailureListener { e: Exception? ->
            print(e?.message)
            addOrderLiveData.value = false
            onComplete?.invoke(addOrderLiveData)
        }
        uploadTask.addOnCanceledListener {
            addOrderLiveData.value = false
            onComplete?.invoke(addOrderLiveData)
        }
    }

    private fun updateProductImage(url: String, orderId: String,onComplete: ((MutableLiveData<Boolean>) -> Unit)?) {
        val map: MutableMap<String, String> = HashMap()
        map[PRODUCT_IMAGE_URL] = url
        FirebaseFirestore.getInstance().collection(ORDER_COLLECTION)
            .document(orderId)
            .set(map, SetOptions.merge())
            .addOnCompleteListener { task: Task<Void?> ->
                if (task.isSuccessful) {
                    addOrderLiveData.value = true
                    onComplete?.invoke(addOrderLiveData)
                } else {
                    addOrderLiveData.value = false
                    onComplete?.invoke(addOrderLiveData)
                }
            }
    }
}