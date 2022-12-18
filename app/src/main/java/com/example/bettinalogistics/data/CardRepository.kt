package com.example.bettinalogistics.data

import com.example.baseapp.di.Common
import com.example.bettinalogistics.model.Card
import com.example.bettinalogistics.utils.AppConstant
import com.example.bettinalogistics.utils.DataConstant
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

interface CardRepository {
    suspend fun addCard(card: Card, onComplete: ((Boolean?) -> Unit)?)

    suspend fun updateCard(card: Card, onComplete: ((Boolean?) -> Unit)?)

    suspend fun getCard(uid: String, onComplete: ((Card?) -> Unit)?)
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

    override suspend fun getCard(uid: String, onComplete: ((Card?) -> Unit)?) {
        FirebaseFirestore.getInstance().collection(AppConstant.CARD_COLLECTION)
            .whereEqualTo(DataConstant.USER_ID, uid)
            .get()
            .addOnCompleteListener {
                if (Common.currentActivity!!.isDestroyed || Common.currentActivity!!.isFinishing) {
                    return@addOnCompleteListener
                }
                val card: List<Card> = it.result.toObjects(Card::class.java)
                if (card.isEmpty()) {
                    onComplete?.invoke(null)
                } else {
                    onComplete?.invoke(card.firstOrNull())
                }
            }.addOnFailureListener {
                onComplete?.invoke(null)
            }
    }
}