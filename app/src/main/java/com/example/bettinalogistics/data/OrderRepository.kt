package com.example.bettinalogistics.data

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.example.baseapp.di.Common
import com.example.bettinalogistics.di.AppData
import com.example.bettinalogistics.model.OrderTransaction
import com.example.bettinalogistics.model.Product
import com.example.bettinalogistics.model.UserCompany
import com.example.bettinalogistics.utils.AppConstant
import com.example.bettinalogistics.utils.AppConstant.Companion.ORDER_COLLECTION
import com.example.bettinalogistics.utils.AppConstant.Companion.ORDER_IMAGE_STORAGE
import com.example.bettinalogistics.utils.AppConstant.Companion.USER_COMPANY_COLLECTION
import com.example.bettinalogistics.utils.DataConstant
import com.example.bettinalogistics.utils.DataConstant.Companion.AMOUNT_AFTER_DISCOUNT
import com.example.bettinalogistics.utils.DataConstant.Companion.AMOUNT_BEFORE_DISCOUNT
import com.example.bettinalogistics.utils.DataConstant.Companion.COMPANY_ADDRESS
import com.example.bettinalogistics.utils.DataConstant.Companion.COMPANY_BUSINESS_TYPE
import com.example.bettinalogistics.utils.DataConstant.Companion.COMPANY_ID
import com.example.bettinalogistics.utils.DataConstant.Companion.COMPANY_NAME
import com.example.bettinalogistics.utils.DataConstant.Companion.COMPANY_TEX_CODE
import com.example.bettinalogistics.utils.DataConstant.Companion.DISCOUNT
import com.example.bettinalogistics.utils.DataConstant.Companion.ORDER_ADDRESS
import com.example.bettinalogistics.utils.DataConstant.Companion.ORDER_CODE
import com.example.bettinalogistics.utils.DataConstant.Companion.ORDER_COMPANY
import com.example.bettinalogistics.utils.DataConstant.Companion.ORDER_DATE
import com.example.bettinalogistics.utils.DataConstant.Companion.ORDER_STATUS
import com.example.bettinalogistics.utils.DataConstant.Companion.ORDER_TRANSPORT_METHOD
import com.example.bettinalogistics.utils.DataConstant.Companion.ORDER_TRANSPORT_TYPE
import com.example.bettinalogistics.utils.DataConstant.Companion.USER_ID
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage


interface OrderRepository {
    suspend fun getAllOrderTransactions(): MutableLiveData<List<OrderTransaction>?>
    suspend fun addOrderTransaction(orderTransaction: OrderTransaction, onComplete: ((Boolean) -> Unit)?)
    suspend fun getUserCompany( onComplete: ((UserCompany?) -> Unit)?)
    suspend fun addUserCompany(userCompany: UserCompany, onComplete: ((Boolean) -> Unit)?)
}

class OrderRepositoryImpl : OrderRepository {

    private var getAllOrdersLiveData = MutableLiveData<List<OrderTransaction>?>()

    override suspend fun getAllOrderTransactions(): MutableLiveData<List<OrderTransaction>?> {
        val listOrder: ArrayList<OrderTransaction> = ArrayList()
        FirebaseFirestore.getInstance().collection(ORDER_COLLECTION)
            .get()
            .addOnSuccessListener { queryDocumentSnapshots: QuerySnapshot ->
                for (query in queryDocumentSnapshots) {
                    val order = query.toObject(OrderTransaction::class.java);
                    listOrder.add(order)
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
    var countUpLoad = 0
    override suspend fun addOrderTransaction(orderTransaction: OrderTransaction, onComplete: ((Boolean) -> Unit)?) {
        val values: HashMap<String, Any?> = HashMap()
        values[ORDER_CODE] = orderTransaction.code
        values[ORDER_ADDRESS] = orderTransaction.address
        values[ORDER_COMPANY] = orderTransaction.company
        values[ORDER_STATUS] = orderTransaction.status
        values[ORDER_DATE] = orderTransaction.orderDate
        values[AMOUNT_BEFORE_DISCOUNT] = orderTransaction.amountBeforeDiscount
        values[DISCOUNT] = orderTransaction.discount
        values[AMOUNT_AFTER_DISCOUNT] = orderTransaction.amountAfterDiscount
        values[ORDER_TRANSPORT_TYPE] = orderTransaction.typeTransportation
        values[ORDER_TRANSPORT_METHOD] = orderTransaction.methodTransport
        values[USER_ID] = AppData.g().userId

        val documentReference = FirebaseFirestore.getInstance().collection(ORDER_COLLECTION)
            .document()
        documentReference.set(values, SetOptions.merge()).addOnCompleteListener { it ->
            if (it.isSuccessful) {
                orderTransaction.productList?.let {listProduct->
                    if(listProduct.isNotEmpty()){
                        upLoadPhotos(listProduct, documentReference.id,onComplete)
                    }
                    else{
                        onComplete?.invoke(true)
                    }
                }
            } else {
                onComplete?.invoke(false)
            }
        }
    }

    override suspend fun getUserCompany( onComplete: ((UserCompany?) -> Unit)?) {
        FirebaseFirestore.getInstance().collection(AppConstant.USER_COMPANY_COLLECTION)
            .whereEqualTo(USER_ID, AppData.g().userId)
            .get()
            .addOnCompleteListener {
                if (Common.currentActivity!!.isDestroyed || Common.currentActivity!!.isFinishing) {
                    return@addOnCompleteListener
                }
                val company: List<UserCompany> = it.result.toObjects(UserCompany::class.java)
                if(company.isEmpty()){
                    onComplete?.invoke(null)
                }
                else onComplete?.invoke(company[0])
            }.addOnFailureListener {
                val company: UserCompany? = null
                onComplete?.invoke(company)
            }
    }

    override suspend fun addUserCompany(
        userCompany: UserCompany,
        onComplete: ((Boolean) -> Unit)?
    ) {
        val documentReference = FirebaseFirestore.getInstance().collection(USER_COMPANY_COLLECTION)
            .document()
        val values: HashMap<String, String?> = HashMap()
        values[COMPANY_ID] = documentReference.id
        values[COMPANY_NAME] = userCompany.name
        values[COMPANY_ADDRESS] = userCompany.address
        values[COMPANY_TEX_CODE] = userCompany.texCode
        values[COMPANY_BUSINESS_TYPE] = userCompany.businessType
        values[USER_ID] = AppData.g().userId

        documentReference.set(values, SetOptions.merge()).addOnCompleteListener { it ->
            if (it.isSuccessful) {
                if (Common.currentActivity!!.isDestroyed || Common.currentActivity!!.isFinishing) {
                    return@addOnCompleteListener
                }
                else onComplete?.invoke(true)
            } else {
                onComplete?.invoke(false)
            }
        }
    }

    private fun upLoadPhotos(
        products: List<Product>,
        orderId: String,
        onComplete: ((Boolean) -> Unit)?
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
            onComplete?.invoke(false)
        }
        uploadTask.addOnCanceledListener {
            onComplete?.invoke(false)
        }
    }

    private fun updateProduct(newProducts: List<Product>, orderId: String, onComplete: ((Boolean) -> Unit)?) {
        countUpLoad++
        if(countUpLoad == newProducts.size){
            countUpLoad = 0
            val map: MutableMap<String, Any> = java.util.HashMap()
            map[DataConstant.PRODUCT_LIST] = newProducts
            FirebaseFirestore.getInstance().collection(ORDER_COLLECTION)
                .document(orderId)
                .set(map, SetOptions.merge())
                .addOnCompleteListener { task: Task<Void?> ->
                    if (task.isSuccessful) {
                        onComplete?.invoke(true)
                    } else {
                        onComplete?.invoke(false)
                    }
                }
        }
        else{
            upLoadPhotos(newProducts,orderId,onComplete)
        }
    }
}