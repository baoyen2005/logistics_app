package com.example.bettinalogistics.data

import android.net.Uri
import android.util.Log
import com.example.baseapp.di.Common
import com.example.bettinalogistics.di.AppData
import com.example.bettinalogistics.model.*
import com.example.bettinalogistics.utils.AppConstant
import com.example.bettinalogistics.utils.AppConstant.Companion.ORDER_COLLECTION
import com.example.bettinalogistics.utils.AppConstant.Companion.SEND_NOTIFICATION
import com.example.bettinalogistics.utils.AppConstant.Companion.TAG
import com.example.bettinalogistics.utils.AppConstant.Companion.TERM_PRODUCT_LIST
import com.example.bettinalogistics.utils.AppConstant.Companion.USER_COMPANY_COLLECTION
import com.example.bettinalogistics.utils.DataConstant
import com.example.bettinalogistics.utils.DataConstant.Companion.AMOUNT_AFTER_DISCOUNT
import com.example.bettinalogistics.utils.DataConstant.Companion.AMOUNT_BEFORE_DISCOUNT
import com.example.bettinalogistics.utils.DataConstant.Companion.COMPANY_ADDRESS
import com.example.bettinalogistics.utils.DataConstant.Companion.COMPANY_BUSINESS_TYPE
import com.example.bettinalogistics.utils.DataConstant.Companion.COMPANY_EMAIL
import com.example.bettinalogistics.utils.DataConstant.Companion.COMPANY_ID
import com.example.bettinalogistics.utils.DataConstant.Companion.COMPANY_NAME
import com.example.bettinalogistics.utils.DataConstant.Companion.COMPANY_PHONE
import com.example.bettinalogistics.utils.DataConstant.Companion.COMPANY_TEX_CODE
import com.example.bettinalogistics.utils.DataConstant.Companion.CONFIRM_DATE_NOTIFICATION
import com.example.bettinalogistics.utils.DataConstant.Companion.CONTENT_NOTIFICATION
import com.example.bettinalogistics.utils.DataConstant.Companion.DISCOUNT
import com.example.bettinalogistics.utils.DataConstant.Companion.ID_NOTIFICATION
import com.example.bettinalogistics.utils.DataConstant.Companion.NOTIFICATION_TYPE
import com.example.bettinalogistics.utils.DataConstant.Companion.ORDER_ADDRESS
import com.example.bettinalogistics.utils.DataConstant.Companion.ORDER_CODE
import com.example.bettinalogistics.utils.DataConstant.Companion.ORDER_COMPANY
import com.example.bettinalogistics.utils.DataConstant.Companion.ORDER_DATE
import com.example.bettinalogistics.utils.DataConstant.Companion.ORDER_ID
import com.example.bettinalogistics.utils.DataConstant.Companion.ORDER_NOTIFICATION
import com.example.bettinalogistics.utils.DataConstant.Companion.ORDER_STATUS
import com.example.bettinalogistics.utils.DataConstant.Companion.ORDER_TRANSPORT_TYPE
import com.example.bettinalogistics.utils.DataConstant.Companion.ORDER_USER
import com.example.bettinalogistics.utils.DataConstant.Companion.PAYMENT_STATUS
import com.example.bettinalogistics.utils.DataConstant.Companion.PERSON_RECEIVER_NOTIFICATION
import com.example.bettinalogistics.utils.DataConstant.Companion.PRODUCT_CONT_TYPE
import com.example.bettinalogistics.utils.DataConstant.Companion.PRODUCT_DES
import com.example.bettinalogistics.utils.DataConstant.Companion.PRODUCT_DOCUMENT_ID
import com.example.bettinalogistics.utils.DataConstant.Companion.PRODUCT_ID
import com.example.bettinalogistics.utils.DataConstant.Companion.PRODUCT_IMAGE_URL
import com.example.bettinalogistics.utils.DataConstant.Companion.PRODUCT_IS_ORDER_LCL
import com.example.bettinalogistics.utils.DataConstant.Companion.PRODUCT_MASS
import com.example.bettinalogistics.utils.DataConstant.Companion.PRODUCT_NAME
import com.example.bettinalogistics.utils.DataConstant.Companion.PRODUCT_NUMBER_OF_CARTON
import com.example.bettinalogistics.utils.DataConstant.Companion.PRODUCT_QUANTITY
import com.example.bettinalogistics.utils.DataConstant.Companion.PRODUCT_SUPPLIER
import com.example.bettinalogistics.utils.DataConstant.Companion.PRODUCT_TYPE
import com.example.bettinalogistics.utils.DataConstant.Companion.PRODUCT_VOLUME
import com.example.bettinalogistics.utils.DataConstant.Companion.REQUEST_DATE_NOTIFICATION
import com.example.bettinalogistics.utils.DataConstant.Companion.TIME_ESTIMATE_NOTIFICATION
import com.example.bettinalogistics.utils.DataConstant.Companion.USER_ID
import com.example.bettinalogistics.utils.dateToString
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


interface OrderRepository {
    suspend fun addProduct(product: Product, onComplete: ((Boolean) -> Unit)?)
    suspend fun getAllProduct(onComplete: ((List<Product>?) -> Unit)?)
    suspend fun deleteAddedProduct(sizeListProduct: Int)
    suspend fun deleteProduct(product: Product, onComplete: ((Boolean) -> Unit)?)
    suspend fun updateProduct(product: Product, onComplete: ((Boolean) -> Unit)?)
    suspend fun getAllOrderTransactions(onComplete: ((List<Order>?) -> Unit)?)
    suspend fun getAllOrderTransactionsSuccess(onComplete: ((List<Order>?) -> Unit)?)
    suspend fun addOrderTransaction(order: Order, onComplete: ((Boolean) -> Unit)?)
    suspend fun updateOrder(order: Order, onComplete: ((Boolean) -> Unit)?)
    suspend fun getUserCompany(onComplete: ((UserCompany?) -> Unit)?)
    suspend fun addUserCompany(userCompany: UserCompany, onComplete: ((Boolean) -> Unit)?)
    suspend fun sendNotiRequest(notification: Notification, onComplete: ((Boolean) -> Unit)?)
    suspend fun sendOtt(ottRequest: OttRequest, onComplete: ((OttResponse?) -> Unit)?)
}

class OrderRepositoryImpl : OrderRepository {
    override suspend fun addProduct(product: Product, onComplete: ((Boolean) -> Unit)?) {
        val document = FirebaseFirestore.getInstance().collection(TERM_PRODUCT_LIST)
            .document()
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
        values[PRODUCT_ID] = product.productId
        values[PRODUCT_DOCUMENT_ID] = document.id
        values[USER_ID] = AppData.g().userId
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

    override suspend fun getAllProduct(onComplete: ((List<Product>?) -> Unit)?) {
        val listProduct: ArrayList<Product> = ArrayList()
        FirebaseFirestore.getInstance().collection(TERM_PRODUCT_LIST)
            .get()
            .addOnSuccessListener { queryDocumentSnapshots: QuerySnapshot ->
                for (query in queryDocumentSnapshots) {
                    val product = query.toObject(Product::class.java);
                    val isOrderLcl = query.getBoolean(PRODUCT_IS_ORDER_LCL)
                    if (isOrderLcl != null) {
                        product.isOrderLCL = isOrderLcl
                    }
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

    override suspend fun deleteAddedProduct(sizeListProduct: Int) {
        deleteCollectionAddedProduct(
            FirebaseFirestore.getInstance().collection(TERM_PRODUCT_LIST),
            sizeListProduct
        )
    }

    override suspend fun deleteProduct(product: Product, onComplete: ((Boolean) -> Unit)?) {
        var productGetFirebase: Product? = null
        FirebaseFirestore.getInstance().collection(TERM_PRODUCT_LIST)
            .whereEqualTo(PRODUCT_ID, product.productId)
            .get()
            .addOnSuccessListener { queryDocumentSnapshots: QuerySnapshot ->
                for (query in queryDocumentSnapshots) {
                    productGetFirebase = query.toObject(Product::class.java)
                }
                productGetFirebase?.productDocumentId?.let {
                    if(it.isNotEmpty()){
                        FirebaseFirestore.getInstance().collection(TERM_PRODUCT_LIST).document(
                            it
                        ).delete().addOnSuccessListener {
                            onComplete?.invoke(true)
                        }.addOnFailureListener {
                            onComplete?.invoke(false)
                        }
                    }
                }
            }.addOnFailureListener {
                Log.d(TAG, "deleteProduct: $it")
            }.addOnCanceledListener {
            }

    }

    override suspend fun updateProduct(product: Product, onComplete: ((Boolean) -> Unit)?) {
        var productGetFirebase: Product? = null
        FirebaseFirestore.getInstance().collection(TERM_PRODUCT_LIST)
            .whereEqualTo(PRODUCT_ID, product.productId)
            .get()
            .addOnSuccessListener { queryDocumentSnapshots: QuerySnapshot ->
                for (query in queryDocumentSnapshots) {
                    productGetFirebase = query.toObject(Product::class.java)
                }
                productGetFirebase?.productDocumentId?.let {
                   if(it.isNotEmpty()){
                       val doc = FirebaseFirestore.getInstance().collection(TERM_PRODUCT_LIST)
                           .document(it)
                       product.imgUri = productGetFirebase?.imgUri
                       product.productDocumentId = doc.id
                       doc.set(product, SetOptions.merge()).addOnSuccessListener {
                           onComplete?.invoke(true)
                       }.addOnFailureListener {
                           onComplete?.invoke(false)
                       }
                   }
                }
            }.addOnFailureListener {
                Log.d(TAG, "updateProduct: $it")
            }.addOnCanceledListener {
            }
    }

    override suspend fun getAllOrderTransactions(onComplete: ((List<Order>?) -> Unit)?) {
        val listOrder: ArrayList<Order> = ArrayList()
        FirebaseFirestore.getInstance().collection(ORDER_COLLECTION)
            .whereEqualTo(USER_ID, AppData.g().userId)
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

    override suspend fun getAllOrderTransactionsSuccess(onComplete: ((List<Order>?) -> Unit)?) {
        val listOrder: ArrayList<Order> = ArrayList()
        FirebaseFirestore.getInstance().collection(ORDER_COLLECTION)
            .whereEqualTo(USER_ID, AppData.g().userId)
            .get()
            .addOnSuccessListener { queryDocumentSnapshots: QuerySnapshot ->
                for (query in queryDocumentSnapshots) {
                    val order = query.toObject(Order::class.java);
                    if(order.statusOrder == DataConstant.ORDER_STATUS_DELIVERED)
                    {
                        listOrder.add(order)
                    }
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
        val documentReference = FirebaseFirestore.getInstance().collection(ORDER_COLLECTION)
            .document()
        values[ORDER_CODE] = order.orderCode
        values[ORDER_ADDRESS] = order.address
        values[ORDER_COMPANY] = order.company
        values[ORDER_STATUS] = order.statusOrder
        values[PAYMENT_STATUS] = order.statusPayment
        values[ORDER_DATE] = order.orderDate
        values[AMOUNT_BEFORE_DISCOUNT] = order.amountBeforeDiscount
        values[DISCOUNT] = order.discount
        values[AMOUNT_AFTER_DISCOUNT] = order.amountAfterDiscount
        values[ORDER_TRANSPORT_TYPE] = order.typeTransportation
        values[ORDER_USER] = order.user
        values[USER_ID] = AppData.g().userId
        values[ORDER_ID] = documentReference.id
        values[ORDER_ID] = documentReference.id
        values[PRODUCT_SUPPLIER] = order.supplierCompany

        documentReference.set(values, SetOptions.merge()).addOnCompleteListener { it ->
            if (it.isSuccessful) {
                if (order.productList.isNullOrEmpty()) {
                    onComplete?.invoke(false)
                } else {
                    upLoadPhotoProductListToOrder(
                        order.productList!!,
                        documentReference.id,
                        onComplete
                    )
                }
            } else {
                onComplete?.invoke(false)
            }
        }
    }

    override suspend fun updateOrder(order: Order, onComplete: ((Boolean) -> Unit)?) {
        val doc = order.id?.let {
            FirebaseFirestore.getInstance().collection(ORDER_COLLECTION)
                .document(it)
        }
        doc?.set(order, SetOptions.merge())?.addOnSuccessListener {
            onComplete?.invoke(true)
        }?.addOnFailureListener {
            onComplete?.invoke(false)
        }
    }

    override suspend fun getUserCompany(onComplete: ((UserCompany?) -> Unit)?) {
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
                else onComplete?.invoke(company.firstOrNull())
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
                } else onComplete?.invoke(true)
            } else {
                onComplete?.invoke(false)
            }
        }
    }

    override suspend fun sendNotiRequest(
        notification: Notification,
        onComplete: ((Boolean) -> Unit)?
    ) {
        val documentReference = FirebaseFirestore.getInstance().collection(SEND_NOTIFICATION)
            .document()
        val values: HashMap<String, Any?> = HashMap()
        values[ID_NOTIFICATION] = documentReference.id
        values[CONTENT_NOTIFICATION] = notification.contentNoti ?: ""
        values[NOTIFICATION_TYPE] = notification.notificationType ?: ""
        values[ORDER_NOTIFICATION] = notification.order
        values[CONFIRM_DATE_NOTIFICATION] = notification.confirmDate
        values[REQUEST_DATE_NOTIFICATION] = notification.requestDate
        values[PERSON_RECEIVER_NOTIFICATION] = notification.notiTo
        values[USER_ID] = AppData.g().userId
        values[TIME_ESTIMATE_NOTIFICATION] = notification.timeTransactionEstimate

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

    override suspend fun sendOtt(ottRequest: OttRequest, onComplete: ((OttResponse?) -> Unit)?) {
        val apiService = ServiceBuilder.getInstance().create(OttFirebaseApi::class.java)
        val call = apiService.sendOttFirebase(ottRequest)
        Log.d(TAG, "sendOtt: $ottRequest")
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

    private fun deleteCollectionAddedProduct(collection: CollectionReference, batchSize: Int) {
        try {
            // Retrieve a small batch of documents to avoid out-of-memory errors/
            var deleted = 0
            collection
                .limit(batchSize.toLong())
                .get()
                .addOnCompleteListener {
                    for (document in it.result.documents) {
                        document.reference.delete()
                        ++deleted
                    }
                    if (deleted >= batchSize) {
                        deleteCollectionAddedProduct(collection, batchSize)
                    }
                }
        } catch (e: Exception) {
            System.err.println("Error deleting collection : " + e.message)
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

    private fun updateProductImageToFireStorage(
        url: String,
        id: String,
        onComplete: ((Boolean) -> Unit)?
    ) {
        val map: MutableMap<String, Any> = HashMap()
        map[PRODUCT_IMAGE_URL] = url
        FirebaseFirestore.getInstance().collection(TERM_PRODUCT_LIST)
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

    private fun upLoadPhotoProductListToOrder(
        products: List<Product>,
        orderId: String,
        onComplete: ((Boolean) -> Unit)?
    ) {
        updateProductOrder(products, orderId, onComplete)
    }

    private fun updateProductOrder(
        newProducts: List<Product>,
        orderId: String,
        onComplete: ((Boolean) -> Unit)?
    ) {
        countUpLoad++
        if (countUpLoad == newProducts.size) {
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
            upLoadPhotoProductListToOrder(newProducts, orderId, onComplete)
        }
    }
}