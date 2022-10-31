package com.example.bettinalogistics.data

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.bettinalogistics.di.AppData
import com.example.bettinalogistics.model.Order
import com.example.bettinalogistics.model.Product
import com.example.bettinalogistics.utils.AppConstant.Companion.ORDER_COLLECTION
import com.example.bettinalogistics.utils.AppConstant.Companion.ORDER_IMAGE_STORAGE
import com.example.bettinalogistics.utils.AppConstant.Companion.TAG
import com.example.bettinalogistics.utils.DataConstant
import com.example.bettinalogistics.utils.DataConstant.Companion.ORDER_CONT_NUMBER
import com.example.bettinalogistics.utils.DataConstant.Companion.ORDER_DATE
import com.example.bettinalogistics.utils.DataConstant.Companion.ORDER_ID
import com.example.bettinalogistics.utils.DataConstant.Companion.ORDER_STATUS
import com.example.bettinalogistics.utils.DataConstant.Companion.ORDER_TRANSPORT_METHOD
import com.example.bettinalogistics.utils.DataConstant.Companion.ORDER_TRANSPORT_TYPE
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
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage


interface OrderRepository {
    suspend fun getAllOrder(): MutableLiveData<List<Product>?>
    suspend fun addOrder(order: Order, onComplete: ((MutableLiveData<Boolean>) -> Unit)?)
}

class OrderRepositoryImpl : OrderRepository {
    companion object {
        val documentReference = FirebaseFirestore.getInstance().collection(ORDER_COLLECTION)
            .document()
    }

    private var addOrderLiveData = MutableLiveData<Boolean>()
    private var getAllOrdersLiveData = MutableLiveData<List<Product>?>()

    override suspend fun getAllOrder(): MutableLiveData<List<Product>?> {
        val listProduct: ArrayList<Product> = ArrayList()
        FirebaseFirestore.getInstance().collection(ORDER_COLLECTION)
            .get()
            .addOnSuccessListener { queryDocumentSnapshots: QuerySnapshot ->
                for (query in queryDocumentSnapshots) {
                    val imgUri = query[PRODUCT_IMAGE_URL].toString()
                    val productName = query[PRODUCT_NAME].toString()
                    val productDes = query[PRODUCT_DES].toString()
                    val quantity = query[PRODUCT_QUANTITY].toString()
                    val volume = query[PRODUCT_VOLUME].toString()
                    val mass = query[PRODUCT_MASS].toString()
                    val numberOfCarton = query[PRODUCT_NUMBER_OF_CARTON].toString()
                    val isOrderLCL = query[PRODUCT_IS_ORDER_LCL].toString()
                    if (quantity.isNotEmpty() && volume.isNotEmpty() && mass.isNotEmpty() && numberOfCarton.isNotEmpty()) {
                        val product = Product(
                            imgUri = imgUri,
                            productName = productName,
                            productDes = productDes,
                            quantity = quantity.toLong(),
                            volume = volume.toDouble(),
                            mass = mass.toDouble(),
                            numberOfCarton = numberOfCarton.toLong(),
                            isOrderLCL = isOrderLCL.toBoolean(),
                        )
                        listProduct.add(product)
                    } else {
                        Log.d(TAG, "getAllOrder: gia tri null")
                    }
                }
                if (listProduct.isNotEmpty()) {
                    getAllOrdersLiveData.postValue(listProduct)
                } else {
                    getAllOrdersLiveData.postValue(listOf())
                }
            }
            .addOnFailureListener {
                getAllOrdersLiveData.postValue(listOf())
            }
        return getAllOrdersLiveData
    }
    var countUpLoad = 0
    override suspend fun addOrder(order: Order, onComplete: ((MutableLiveData<Boolean>) -> Unit)?) {
        val values: HashMap<String, String?> = HashMap()
        values[ORDER_ID] = documentReference.id
        values[ORDER_STATUS] = order.status
        values[ORDER_DATE] = order.orderDate
        values[ORDER_TRANSPORT_TYPE] = order.transportType
        values[ORDER_TRANSPORT_METHOD] = order.transportMethod
        values[ORDER_CONT_NUMBER] = order.contNum.toString()
        values[USER_ID] = AppData.g().userId

        documentReference.set(values, SetOptions.merge()).addOnCompleteListener { it ->
            if (it.isSuccessful) {
                order.productList?.let {listProduct->
                    if(listProduct.isNotEmpty()){
                        upLoadPhotos(listProduct, documentReference.id,onComplete)
                    }
                    else{
                        addOrderLiveData.value = true
                        onComplete?.invoke(addOrderLiveData)
                    }
                }
            } else {
                addOrderLiveData.value = false
                onComplete?.invoke(addOrderLiveData)
            }
        }
    }

    private fun upLoadPhotos(
        products: List<Product>,
        orderId: String,
        onComplete: ((MutableLiveData<Boolean>) -> Unit)?
    ) {
        val product = products[countUpLoad]
        val name = "product_image $orderId $countUpLoad"
        val storageRef = FirebaseStorage.getInstance().reference.child(ORDER_IMAGE_STORAGE).child(orderId).child(name)
        val uri = Uri.parse(product.imgUri)
        val uploadTask = storageRef.putFile(uri)
        uploadTask.addOnSuccessListener {
            storageRef.downloadUrl
                .addOnSuccessListener { url: Uri ->
                    products[countUpLoad].imgUri = url.toString()
                    updateProduct(products, orderId, onComplete)
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

    private fun updateProduct(newProducts: List<Product>, orderId: String, onComplete: ((MutableLiveData<Boolean>) -> Unit)?) {
        countUpLoad++
        if(countUpLoad == newProducts.size){
            val map: MutableMap<String, Any> = java.util.HashMap()
            map[DataConstant.PRODUCT_LIST] = newProducts
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
        else{
            upLoadPhotos(newProducts,orderId,onComplete)
        }
    }
}