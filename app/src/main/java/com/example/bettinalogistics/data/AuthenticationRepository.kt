package com.example.bettinalogistics.data

import android.net.Uri
import com.example.baseapp.BaseRepository
import com.example.baseapp.di.Common
import com.example.bettinalogistics.di.AppData
import com.example.bettinalogistics.model.TokenOtt
import com.example.bettinalogistics.model.User
import com.example.bettinalogistics.model.UserCompany
import com.example.bettinalogistics.utils.AppConstant.Companion.TOKEN_OTT
import com.example.bettinalogistics.utils.AppConstant.Companion.USER_COLLECTION
import com.example.bettinalogistics.utils.AppConstant.Companion.USER_COMPANY_COLLECTION
import com.example.bettinalogistics.utils.DataConstant
import com.example.bettinalogistics.utils.DataConstant.Companion.TOKEN
import com.example.bettinalogistics.utils.DataConstant.Companion.TOKEN_ID
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
    suspend fun signUp(user: User, onComplete: ((Boolean) -> Unit)?)

    suspend fun forgetPassword(email: String, onComplete: ((Boolean) -> Unit)?)

    suspend fun getUser(email: String, onComplete: ((User?) -> Unit)?)

    suspend fun getAllUsers(onComplete: ((List<User>?) -> Unit)?)

    suspend fun deleteUser(user: User, onComplete: ((Boolean) -> Unit)?)

    suspend fun updatePassword(password: String, onComplete: ((Boolean) -> Unit)?)

    suspend fun saveTokenByUser(token: String?, onComplete: ((Boolean) -> Unit)?)

    suspend fun editUser(user: User?, isChangeImage: Boolean, onComplete: ((Boolean) -> Unit)?)

    suspend fun getCompany(uid: String, onComplete: ((UserCompany?) -> Unit)?)

    suspend fun updateCompany(company: UserCompany, onComplete: ((Boolean?) -> Unit)?)
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

    override suspend fun getAllUsers(onComplete: ((List<User>?) -> Unit)?) {
        FirebaseFirestore.getInstance().collection(USER_COLLECTION)
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
                    onComplete?.invoke(users)
                }
            }.addOnFailureListener {
                onComplete?.invoke(null)
            }
    }

    override suspend fun deleteUser(user: User, onComplete: ((Boolean) -> Unit)?) {
        user.uid?.let {
            FirebaseFirestore.getInstance().collection(USER_COLLECTION).document(
                it
            ).delete().addOnSuccessListener {
                onComplete?.invoke(true)
            }.addOnFailureListener {
                onComplete?.invoke(false)
            }
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

    override suspend fun saveTokenByUser(token: String?, onComplete: ((Boolean) -> Unit)?) {
        val documentReference = FirebaseFirestore.getInstance().collection(TOKEN_OTT)
            .document()
        FirebaseFirestore.getInstance().collection(TOKEN_OTT)
            .whereEqualTo(USER_ID, AppData.g().userId)
            .get()
            .addOnCompleteListener {
                if (Common.currentActivity!!.isDestroyed || Common.currentActivity!!.isFinishing) {
                    return@addOnCompleteListener
                }
                val tokenOttList: List<TokenOtt> = it.result.toObjects(TokenOtt::class.java)
                if (tokenOttList.isEmpty()) {
                    val values: HashMap<String, String?> = HashMap()
                    values[USER_ID] = AppData.g().userId
                    values[TOKEN_ID] = documentReference.id
                    values[TOKEN] = token
                    values[USER_ROLE] = AppData.g().currentUser?.role ?: ""
                    documentReference.set(values, SetOptions.merge())
                        .addOnSuccessListener {
                            if (Common.currentActivity!!.isDestroyed || Common.currentActivity!!.isFinishing) {
                                onComplete?.invoke(false)
                                return@addOnSuccessListener
                            } else onComplete?.invoke(true)
                        }.addOnFailureListener {
                            onComplete?.invoke(false)
                        }
                } else {
                    val token = tokenOttList.firstOrNull()
                    token?.token = AppData.g().token
                    val doc = token?.id?.let { it1 ->
                        FirebaseFirestore.getInstance().collection(TOKEN_OTT)
                            .document(it1)
                    }
                    doc?.set(token, SetOptions.merge())?.addOnSuccessListener {
                        onComplete?.invoke(true)
                    }?.addOnFailureListener {
                        onComplete?.invoke(false)
                    }
                }
            }.addOnFailureListener {
                onComplete?.invoke(false)
            }
    }

    override suspend fun editUser(
        user: User?,
        isChangeImage: Boolean,
        onComplete: ((Boolean) -> Unit)?
    ) {
        if (!isChangeImage) {
            val documentReference = user?.uid?.let {
                FirebaseFirestore.getInstance().collection(USER_COLLECTION)
                    .document(it)
            }
            documentReference?.set(user, SetOptions.merge())
                ?.addOnCompleteListener { task: Task<Void?> ->
                    if (Common.currentActivity!!.isDestroyed || Common.currentActivity!!.isFinishing) {
                        return@addOnCompleteListener
                    }
                    if (task.isSuccessful) {
                        onComplete?.invoke(true)
                    } else {
                        onComplete?.invoke(false)
                    }
                }
        } else {
            user?.uid?.let {
                createUser(it, user, onComplete)
            }
        }
    }

    override suspend fun getCompany(uid: String, onComplete: ((UserCompany?) -> Unit)?) {
        FirebaseFirestore.getInstance().collection(USER_COMPANY_COLLECTION)
            .whereEqualTo(USER_ID, uid)
            .get()
            .addOnCompleteListener {
                if (Common.currentActivity!!.isDestroyed || Common.currentActivity!!.isFinishing) {
                    return@addOnCompleteListener
                }
                val companies: List<UserCompany> = it.result.toObjects(UserCompany::class.java)
                if (companies.isEmpty()) {
                    onComplete?.invoke(null)
                } else {
                    onComplete?.invoke(companies.firstOrNull())
                }
            }.addOnFailureListener {
                onComplete?.invoke(null)
            }
    }

    override suspend fun updateCompany(company: UserCompany, onComplete: ((Boolean?) -> Unit)?) {
        if (company.id == null) {
            val documentReference =
                FirebaseFirestore.getInstance().collection(USER_COMPANY_COLLECTION)
                    .document()
            val values: java.util.HashMap<String, String?> = java.util.HashMap()
            values[DataConstant.COMPANY_ID] = documentReference.id
            values[DataConstant.COMPANY_NAME] = company.name
            values[DataConstant.COMPANY_ADDRESS] = company.address
            values[DataConstant.COMPANY_TEX_CODE] = company.texCode
            values[DataConstant.COMPANY_BUSINESS_TYPE] = company.businessType
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
        } else {
            FirebaseFirestore.getInstance().collection(USER_COMPANY_COLLECTION)
                .document(company.id!!)
                .set(company, SetOptions.merge())
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