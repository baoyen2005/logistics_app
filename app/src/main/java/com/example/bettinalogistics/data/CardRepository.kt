package com.example.bettinalogistics.data

import android.net.Uri
import android.util.Log
import com.example.baseapp.di.Common
import com.example.bettinalogistics.di.AppData
import com.example.bettinalogistics.model.Card
import com.example.bettinalogistics.model.Order
import com.example.bettinalogistics.model.Payment
import com.example.bettinalogistics.utils.AppConstant
import com.example.bettinalogistics.utils.AppConstant.Companion.TAG
import com.example.bettinalogistics.utils.DataConstant
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage

interface CardRepository {
    suspend fun addCard(card: Card, onComplete: ((Boolean?) -> Unit)?)

    suspend fun updateCard(card: Card, onComplete: ((Boolean?) -> Unit)?)

    suspend fun deleteCard(card: Card, onComplete: ((Boolean?) -> Unit)?)

    suspend fun getAllCards(uid: String, onComplete: ((List<Card>?) -> Unit)?)

    suspend fun getPayment(order:Order, onComplete: ((Payment?) -> Unit)?)

    suspend fun addPayment(payment: Payment, onComplete: ((Boolean) -> Unit)?)
}
class CardRepoImpl : CardRepository{
    override suspend fun addCard(card: Card, onComplete: ((Boolean?) -> Unit)?) {
        val documentReference = FirebaseFirestore.getInstance().collection(AppConstant.CARD_COLLECTION)
            .document()
        val values: HashMap<String, Any?> = HashMap()
        values[DataConstant.BANK_NAME] = card.name
        values[DataConstant.CARD_ID] = documentReference.id
        values[DataConstant.ACCOUNT_NUMBER] = card.accountNumber
        values[DataConstant.CARD_NUMBER] = card.cardNumber
        values[DataConstant.DARE_OF_EXPIRED] = card.dateOfExpired
        values[DataConstant.USER] = AppData.g().currentUser
        documentReference.set(values, SetOptions.merge())
            .addOnSuccessListener {
                if (Common.currentActivity!!.isDestroyed || Common.currentActivity!!.isFinishing) {
                    return@addOnSuccessListener
                }
                onComplete?.invoke(true)
            }.addOnFailureListener {
                onComplete?.invoke(false)
            }
    }

    override suspend fun updateCard(card: Card, onComplete: ((Boolean?) -> Unit)?) {
        card.id?.let {
            FirebaseFirestore.getInstance().collection(AppConstant.CARD_COLLECTION)
                .document(it)
                .set(card, SetOptions.merge())
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
    }

    override suspend fun deleteCard(card: Card, onComplete: ((Boolean?) -> Unit)?) {
        card.id?.let {
            FirebaseFirestore.getInstance().collection(AppConstant.CARD_COLLECTION)
                .document(it)
                .delete()
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
    }

    override suspend fun getAllCards(uid: String, onComplete: ((List<Card>?) -> Unit)?) {
        FirebaseFirestore.getInstance().collection(AppConstant.CARD_COLLECTION)
            .whereEqualTo(DataConstant.USER, AppData.g().currentUser)
            .get()
            .addOnCompleteListener {
                if (Common.currentActivity!!.isDestroyed || Common.currentActivity!!.isFinishing) {
                    return@addOnCompleteListener
                }
                val card: List<Card> = it.result.toObjects(Card::class.java)
                if (card.isEmpty()) {
                    onComplete?.invoke(null)
                } else {
                    onComplete?.invoke(card)
                }
            }.addOnFailureListener {
                onComplete?.invoke(null)
            }
    }

    override suspend fun getPayment(order:Order, onComplete: ((Payment?) -> Unit)?) {
        FirebaseFirestore.getInstance().collection(AppConstant.PAYMENT_COLLECTION)
            .whereEqualTo(DataConstant.PAYMENT_ORDER, order)
            .get()
            .addOnCompleteListener {
                if (Common.currentActivity!!.isDestroyed || Common.currentActivity!!.isFinishing) {
                    return@addOnCompleteListener
                }
                val payments: List<Payment> = it.result.toObjects(Payment::class.java)
                if (payments.isEmpty()) {
                    onComplete?.invoke(null)
                } else {
                    onComplete?.invoke(payments.firstOrNull())
                }
            }.addOnFailureListener {
                onComplete?.invoke(null)
            }
    }

    override suspend fun addPayment(payment: Payment, onComplete: ((Boolean) -> Unit)?) {
        Log.d(TAG, "addPayment: ${payment.imgUrlPayment}")
        val documentReference = FirebaseFirestore.getInstance().collection(AppConstant.PAYMENT_COLLECTION)
            .document()
        val values: HashMap<String, Any?> = HashMap()
        values[DataConstant.PAYMENT_CONTENT] = payment.contentPayment
        values[DataConstant.PAYMENT_ID] = documentReference.id
        values[DataConstant.PAYMENT_ORDER] = payment.order
        values[DataConstant.PAYMENT_USER] = payment.user
        values[DataConstant.PAYMENT_CARD] = payment.card
        values[DataConstant.PAYMENT_DATE] = payment.datePayment
        documentReference.set(values, SetOptions.merge())
            .addOnSuccessListener {
                if (Common.currentActivity!!.isDestroyed || Common.currentActivity!!.isFinishing) {
                    return@addOnSuccessListener
                }
                uploadImage(documentReference.id, payment.imgUrlPayment, onComplete)
            }.addOnFailureListener {
                onComplete?.invoke(false)
            }
    }
    private fun uploadImage(uid: String, uri: String?, onComplete: ((Boolean) -> Unit)?) {
        val storage = FirebaseStorage.getInstance()
        val name = "banking"
        val storageRef = storage.reference.child("photos_bank_$uid").child(uid).child(name)
        val uploadTask = storageRef.putFile(Uri.parse(uri))
        uploadTask.addOnSuccessListener {
            if (Common.currentActivity!!.isDestroyed || Common.currentActivity!!.isFinishing) {
                return@addOnSuccessListener
            }
            storageRef.downloadUrl
                .addOnSuccessListener { url: Uri ->
                    updateUserImageToFireStorage(url.toString(), uid, onComplete)
                }
        }
        uploadTask.addOnFailureListener { e: java.lang.Exception? ->
            Log.d(TAG, "uploadImage: $e")
            onComplete?.invoke(false)
        }
        uploadTask.addOnCanceledListener {
            onComplete?.invoke(false)
        }
    }

    private fun updateUserImageToFireStorage(
        url: String,
        uid: String,
        onComplete: ((Boolean) -> Unit)?
    ) {
        val map: MutableMap<String, Any> = HashMap()
        map[DataConstant.PAYMENT_IMG_URL] = url
        FirebaseFirestore.getInstance().collection(AppConstant.PAYMENT_COLLECTION)
            .document(uid)
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
}