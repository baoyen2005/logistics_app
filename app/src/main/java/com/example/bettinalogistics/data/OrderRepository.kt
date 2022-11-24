package com.example.bettinalogistics.data

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.example.baseapp.di.Common
import com.example.bettinalogistics.di.AppData
import com.example.bettinalogistics.model.Order
import com.example.bettinalogistics.model.Product
import com.example.bettinalogistics.model.UserCompany
import com.example.bettinalogistics.utils.AppConstant
import com.example.bettinalogistics.utils.AppConstant.Companion.ORDER_COLLECTION
import com.example.bettinalogistics.utils.AppConstant.Companion.ORDER_IMAGE_STORAGE
import com.example.bettinalogistics.utils.AppConstant.Companion.TERM_PRODUCT_LIST
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
import com.example.bettinalogistics.utils.DataConstant.Companion.PRODUCT_CONT_TYPE
import com.example.bettinalogistics.utils.DataConstant.Companion.PRODUCT_DES
import com.example.bettinalogistics.utils.DataConstant.Companion.PRODUCT_IMAGE_URL
import com.example.bettinalogistics.utils.DataConstant.Companion.PRODUCT_IS_ORDER_LCL
import com.example.bettinalogistics.utils.DataConstant.Companion.PRODUCT_MASS
import com.example.bettinalogistics.utils.DataConstant.Companion.PRODUCT_NAME
import com.example.bettinalogistics.utils.DataConstant.Companion.PRODUCT_NUMBER_OF_CARTON
import com.example.bettinalogistics.utils.DataConstant.Companion.PRODUCT_QUANTITY
import com.example.bettinalogistics.utils.DataConstant.Companion.PRODUCT_TYPE
import com.example.bettinalogistics.utils.DataConstant.Companion.PRODUCT_VOLUME
import com.example.bettinalogistics.utils.DataConstant.Companion.USER_ID
import com.example.bettinalogistics.utils.Utils
import com.example.bettinalogistics.utils.dateToString
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


interface OrderRepository {
    suspend fun addProduct(product: Product, onComplete: ((Boolean) -> Unit)?)
    suspend fun getAllProduct(onComplete: ((List<Product>?) -> Unit)?)
    suspend fun getAllOrderTransactions(onComplete: ((List<Order>?) -> Unit)?)
    suspend fun addOrderTransaction(order: Order, onComplete: ((Boolean) -> Unit)?)
    suspend fun getUserCompany(onComplete: ((UserCompany?) -> Unit)?)
    suspend fun addUserCompany(userCompany: UserCompany, onComplete: ((Boolean) -> Unit)?)
}

class OrderRepositoryImpl : OrderRepository {
    override suspend fun addProduct(product: Product, onComplete: ((Boolean) -> Unit)?) {
        val values: HashMap<String, Any?> = HashMap()
        values[PRODUCT_NAME] = product.productName
        values[PRODUCT_DES] = product.productDes
        values[PRODUCT_QUANTITY] = product.quantity
        values[PRODUCT_VOLUME] = product.volume
        values[PRODUCT_MASS] = product.mass
        values[PRODUCT_NUMBER_OF_CARTON] = product.numberOfCarton
        values[PRODUCT_IS_ORDER_LCL] = product.isOrderLCL
        values[PRODUCT_TYPE] = product.type
        values[PRODUCT_CONT_TYPE] = product.contType
        values[USER_ID] = AppData.g().userId
        val document = FirebaseFirestore.getInstance().collection(TERM_PRODUCT_LIST)
            .document()
        document.set(values, SetOptions.merge())
            .addOnSuccessListener {
                if (Common.currentActivity!!.isDestroyed || Common.currentActivity!!.isFinishing) {
                    return@addOnSuccessListener
                }
                uploadProductImage(document.id, product.imgUri, onComplete)
            }.addOnFailureListener {
                onComplete?.invoke(false)
            }
    }

    private fun uploadProductImage(id: String, imgUri: String?, onComplete: ((Boolean) -> Unit)?) {
        val storage = FirebaseStorage.getInstance()
        val name = "products${dateToString(Date())}"
        val storageRef = storage.reference.child("product_${Date().time}").child(id).child(name)
        val uploadTask = storageRef.putFile(Uri.parse(imgUri))
        uploadTask.addOnSuccessListener {
            if (Common.currentActivity!!.isDestroyed || Common.currentActivity!!.isFinishing) {
                return@addOnSuccessListener
            }
            storageRef.downloadUrl
                .addOnSuccessListener { url: Uri ->
                    updateProductImageToFireStorage(url.toString(), id, onComplete)
                }
        }
        uploadTask.addOnFailureListener { e: java.lang.Exception? ->
            onComplete?.invoke(false)
        }
        uploadTask.addOnCanceledListener {
            onComplete?.invoke(false)
        }
    }

    private fun updateProductImageToFireStorage(url: String, id: String, onComplete: ((Boolean) -> Unit)?) {
        val map: MutableMap<String, Any> = HashMap()
        map[PRODUCT_IMAGE_URL] = url
        FirebaseFirestore.getInstance().collection(AppConstant.TERM_PRODUCT_LIST)
            .document(id)
            .set(map, SetOptions.merge())
            .addOnCompleteListener { task: Task<Void?> ->
                if (Common.currentActivity!!.isDestroyed || Common.currentActivity!!.isFinishing) {
                    return@addOnCompleteListener
                }
                if (task.isSuccessful) {
                    onComplete?.invoke(true)
                } else {
                    onComplete?.invoke(false)
                }
            }
    }
    override suspend fun getAllProduct(onComplete: ((List<Product>?) -> Unit)?) {
        val listProduct: ArrayList<Product> = ArrayList()
        FirebaseFirestore.getInstance().collection(TERM_PRODUCT_LIST)
            .get()
            .addOnSuccessListener { queryDocumentSnapshots: QuerySnapshot ->
                for (query in queryDocumentSnapshots) {
                    val product = query.toObject(Product::class.java);
                    listProduct.add(product)
                }
                if (listProduct.isNotEmpty()) {
                    onComplete?.invoke(listProduct)
                } else {
                    onComplete?.invoke(null)
                }
            }
            .addOnFailureListener {
                onComplete?.invoke(null)
            }
    }

    override suspend fun getAllOrderTransactions(onComplete: ((List<Order>?) -> Unit)?) {
        val listOrder: ArrayList<Order> = ArrayList()
        FirebaseFirestore.getInstance().collection(ORDER_COLLECTION)
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
    var countUpLoad = 0
    override suspend fun addOrderTransaction(order: Order, onComplete: ((Boolean) -> Unit)?) {
        val values: HashMap<String, Any?> = HashMap()
        values[ORDER_CODE] = order.code
        values[ORDER_ADDRESS] = order.address
        values[ORDER_COMPANY] = order.company
        values[ORDER_STATUS] = order.status
        values[ORDER_DATE] = order.orderDate
        values[AMOUNT_BEFORE_DISCOUNT] = order.amountBeforeDiscount
        values[DISCOUNT] = order.discount
        values[AMOUNT_AFTER_DISCOUNT] = order.amountAfterDiscount
        values[ORDER_TRANSPORT_TYPE] = order.typeTransportation
        values[ORDER_TRANSPORT_METHOD] = order.methodTransport
        values[USER_ID] = AppData.g().userId

        val documentReference = FirebaseFirestore.getInstance().collection(ORDER_COLLECTION)
            .document()
        documentReference.set(values, SetOptions.merge()).addOnCompleteListener { it ->
            if (it.isSuccessful) {
                order.product?.let { listProduct->
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