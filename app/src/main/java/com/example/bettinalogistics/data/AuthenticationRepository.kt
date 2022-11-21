package com.example.bettinalogistics.data

import android.net.Uri
import com.example.baseapp.BaseRepository
import com.example.baseapp.di.Common
import com.example.bettinalogistics.di.AppData
import com.example.bettinalogistics.model.User
import com.example.bettinalogistics.utils.AppConstant.Companion.USER_COLLECTION
import com.example.bettinalogistics.utils.DataConstant.Companion.USER_ADDRESS
import com.example.bettinalogistics.utils.DataConstant.Companion.USER_DATE_OF_BIRTH
import com.example.bettinalogistics.utils.DataConstant.Companion.USER_EMAIL
import com.example.bettinalogistics.utils.DataConstant.Companion.USER_FULL_NAME
import com.example.bettinalogistics.utils.DataConstant.Companion.USER_ID
import com.example.bettinalogistics.utils.DataConstant.Companion.USER_IMAGE
import com.example.bettinalogistics.utils.DataConstant.Companion.USER_PASSWORD
import com.example.bettinalogistics.utils.DataConstant.Companion.USER_PHONE
import com.example.bettinalogistics.utils.DataConstant.Companion.USER_ROLE
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage


interface AuthenticationRepository {
    suspend fun login(email: String, password: String, onComplete: ((Boolean) -> Unit)?)
    suspend fun signUp(
        user: User,
        onComplete: ((Boolean) -> Unit)?
    )

    suspend fun forgetPassword(
        email: String, onComplete: ((Boolean) -> Unit)?
    )

    suspend fun getUser(email: String,onComplete: ((User?) -> Unit)?)

    suspend fun updatePassword(password: String,onComplete: ((Boolean) -> Unit)?)
}

class AuthenticationRepositoryImpl : BaseRepository(), AuthenticationRepository {
    private val context = Common.currentActivity
    override suspend fun login(
        email: String,
        password: String,
        onComplete: ((Boolean) -> Unit)?
    ) {
        val firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                onComplete?.invoke(true)
            }.addOnFailureListener {
                onComplete?.invoke(false)
            }
    }

    override suspend fun signUp(
        user: User,
        onComplete: ((Boolean) -> Unit)?
    ) {
        val firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.createUserWithEmailAndPassword(user.email!!, user.password!!)
            .addOnSuccessListener {
                val currentUser = firebaseAuth.currentUser
                val uid = currentUser?.uid
                uid?.let { uid ->
                    createUser(
                        uid,
                        user, onComplete
                    )
                }
            }
            .addOnFailureListener { e: java.lang.Exception ->
                onComplete?.invoke(false)
            }
    }

    private fun createUser(
        uid: String,
        user: User,
        onComplete: ((Boolean) -> Unit)?
    ) {
        val documentReference = FirebaseFirestore.getInstance().collection(USER_COLLECTION)
            .document(uid)
        val values: HashMap<String, String?> = HashMap()
        values[USER_ID] = uid
        values[USER_FULL_NAME] = user.fullName
        values[USER_PHONE] = user.phone
        values[USER_DATE_OF_BIRTH] = user.dateOfBirth
        values[USER_EMAIL] = user.email
        values[USER_PASSWORD] = user.password
        values[USER_ADDRESS] = user.address
        values[USER_ROLE] = user.role
        documentReference.set(values, SetOptions.merge())
            .addOnSuccessListener {
                if (Common.currentActivity!!.isDestroyed || Common.currentActivity!!.isFinishing) {
                    return@addOnSuccessListener
                }
                uploadImage(uid, user.image, onComplete)
            }.addOnFailureListener {
                onComplete?.invoke(false)
            }
    }

    private fun uploadImage(uid: String, uri: String?, onComplete: ((Boolean) -> Unit)?) {
        val storage = FirebaseStorage.getInstance()
        val name = "avatar"
        val storageRef = storage.reference.child("photos_$uid").child(uid).child(name)
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
            if (context!!.isDestroyed || context.isFinishing) {
                return@addOnFailureListener
            }
            onComplete?.invoke(false)
        }
        uploadTask.addOnCanceledListener {
            if (context!!.isDestroyed || context.isFinishing) {
                return@addOnCanceledListener
            }
            onComplete?.invoke(false)
        }
    }

    private fun updateUserImageToFireStorage(
        url: String,
        uid: String,
        onComplete: ((Boolean) -> Unit)?
    ) {
        val map: MutableMap<String, Any> = HashMap()
        map[USER_IMAGE] = url
        FirebaseFirestore.getInstance().collection(USER_COLLECTION)
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


    override suspend fun forgetPassword(email: String, onComplete: ((Boolean) -> Unit)?) {
        val auth = Firebase.auth
        auth.sendPasswordResetEmail(email).addOnCompleteListener {
            if (it.isSuccessful)
                onComplete?.invoke(true)
            else
                onComplete?.invoke(false)
        }.addOnFailureListener {
            onComplete?.invoke(false)
        }
    }

    override suspend fun getUser(email: String,onComplete: ((User?) -> Unit)?) {
        FirebaseFirestore.getInstance().collection(USER_COLLECTION)
            .whereEqualTo(USER_EMAIL, email)
            .get()
            .addOnCompleteListener {
                if (Common.currentActivity!!.isDestroyed || Common.currentActivity!!.isFinishing) {
                    return@addOnCompleteListener
                }
                val users: List<User> = it.result.toObjects(User::class.java)
                if(users.isEmpty()){
                    onComplete?.invoke(null)
                }
                else{
                    onComplete?.invoke(users[0])
                }
            }.addOnFailureListener {
                val user: User? = null
                onComplete?.invoke(user)
            }
    }

    override suspend fun updatePassword(password: String,onComplete: ((Boolean) -> Unit)?) {
        val map: MutableMap<String, Any> = HashMap()
        map[USER_PASSWORD] = password
        AppData.g().userId?.let {
            FirebaseFirestore.getInstance().collection(USER_COLLECTION)
                .document(it)
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
}