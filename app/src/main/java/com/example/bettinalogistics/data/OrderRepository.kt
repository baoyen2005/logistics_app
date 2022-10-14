package com.example.bettinalogistics.data

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.example.bettinalogistics.di.AppData
import com.example.bettinalogistics.model.Order
import com.example.bettinalogistics.utils.AppConstant.Companion.ORDER_COLLECTION
import com.example.bettinalogistics.utils.AppConstant.Companion.ORDER_IMAGE_STORAGE
import com.example.bettinalogistics.utils.DataConstant.Companion.PRODUCT_DES
import com.example.bettinalogistics.utils.DataConstant.Companion.PRODUCT_IMAGE_URL
import com.example.bettinalogistics.utils.DataConstant.Companion.PRODUCT_IS_ORDER_LCL
import com.example.bettinalogistics.utils.DataConstant.Companion.PRODUCT_MASS
import com.example.bettinalogistics.utils.DataConstant.Companion.PRODUCT_NAME
import com.example.bettinalogistics.utils.DataConstant.Companion.PRODUCT_NUMBER_OF_CARTON
import com.example.bettinalogistics.utils.DataConstant.Companion.PRODUCT_QUANTITY
import com.example.bettinalogistics.utils.DataConstant.Companion.PRODUCT_VOLUME
import com.example.bettinalogistics.utils.DataConstant.Companion.USER_ID
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage


interface OrderRepository {
    suspend fun addOrder(order: Order): MutableLiveData<Boolean>
}

class OrderRepositoryImpl : OrderRepository {
    private var addOrderLiveData = MutableLiveData<Boolean>()
    override suspend fun addOrder(order: Order): MutableLiveData<Boolean> {
        val documentReference = FirebaseFirestore.getInstance().collection(ORDER_COLLECTION)
            .document();
        val values: HashMap<String, String?> = HashMap<String, String?>();
        values[PRODUCT_NAME] = order.productName;
        values[PRODUCT_DES] = order.productDes;
        values[PRODUCT_QUANTITY] = order.quantity.toString();
        values[PRODUCT_VOLUME] = order.volume.toString();
        values[PRODUCT_MASS] = order.mass.toString();
        values[PRODUCT_NUMBER_OF_CARTON] = order.numberOfCarton.toString();
        values[PRODUCT_IS_ORDER_LCL] = order.isOrderLCL.toString();
        values[USER_ID] = AppData.g().userId
        documentReference.set(values, SetOptions.merge()).addOnCompleteListener {
            if (it.isSuccessful) {
                if (order.imgUri != null) {
                    upLoadPhotos(order.imgUri, documentReference.id);
                } else {
                    addOrderLiveData.postValue(false)
                }
            } else {
                addOrderLiveData.postValue(false)
            }
        };
        return addOrderLiveData
    }

    private fun upLoadPhotos(uri: Uri?, orderId: String) {
        val storage = FirebaseStorage.getInstance()
        val name = "product_image"
        val storageRef = storage.reference.child(ORDER_IMAGE_STORAGE).child(orderId).child(name)
        val uploadTask = storageRef.putFile(uri!!)
        uploadTask.addOnSuccessListener {
            storageRef.downloadUrl
                .addOnSuccessListener { url: Uri ->
                    updateProductImage(url.toString(), orderId)
                }
        }
        uploadTask.addOnFailureListener { e: Exception? ->
            print(e?.message)
            addOrderLiveData.postValue(false)
        }
        uploadTask.addOnCanceledListener {
            addOrderLiveData.postValue(false)
        }
    }

    private fun updateProductImage(url: String, orderId: String) {
        val map: MutableMap<String, String> = HashMap()
        map[PRODUCT_IMAGE_URL] = url
        FirebaseFirestore.getInstance().collection(ORDER_COLLECTION)
            .document(orderId)
            .set(map, SetOptions.merge())
            .addOnCompleteListener { task: Task<Void?> ->
                if (task.isSuccessful) {
                    addOrderLiveData.postValue(true)
                } else {
                    addOrderLiveData.postValue(false)
                }
            }
    }
}