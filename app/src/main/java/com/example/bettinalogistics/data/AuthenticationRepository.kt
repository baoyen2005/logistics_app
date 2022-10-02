package com.example.bettinalogistics.data

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.baseapp.BaseRepository
import com.example.baseapp.di.Common
import com.example.bettinalogistics.R
import com.example.bettinalogistics.model.User
import com.example.bettinalogistics.utils.AppConstant
import com.example.bettinalogistics.utils.AppConstant.Companion.TAG
import com.example.bettinalogistics.utils.State
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import java.util.*

interface AuthenticationRepository {
    suspend fun login(email: String, password: String): Flow<State<Any>>
    suspend fun signUp(
        image: Uri,
        fullName: String,
        phone: String,
        dateOfBirth: Date,
        password: String,
        email: String,
        address: String
    ): Flow<State<Any>>

    suspend fun forgetPassword(email: String, context: Context): Flow<State<Any>>
}

class AuthenticationRepositoryImpl : BaseRepository(), AuthenticationRepository {
    private val context = Common.currentActivity
    override suspend fun login(
        email: String,
        password: String
    ): Flow<State<Any>> = flow<State<Any>> {
        emit(State.loading(true))

        val auth = Firebase.auth
        val data = auth.signInWithEmailAndPassword(email, password).await()
        if(data != null){
            Log.d(TAG, "login: in repo success ")
            emit(State.success(context!!.getString(R.string.LOGIN_SUCCESS)))
        }
        else{
            Log.d(TAG, "login: in repo failde ")
            emit(State.failed(context!!.getString(R.string.ERRO_LOGIN)))
        }
    }.catch {
        Log.d(TAG, "login in repo: catchhhh")
        emit(State.failed(it.message.toString()))
    }.flowOn(
        Dispatchers.IO
    )

    override suspend fun signUp(image: Uri,
                                fullName:String,
                                phone: String,
                                dateOfBirth : Date,
                                password: String,
                                email: String,
                                address: String
    ): Flow<State<Any>> = flow<State<Any>> {
        emit(State.loading(true))
        val auth = Firebase.auth
        val data = auth.createUserWithEmailAndPassword(email, password).await()
        data.user?.let {
            val path = uploadImage(it.uid, image).toString()
            val userModel = User(path, fullName, phone, dateOfBirth, email, password, address)
            createUser(userModel, auth)
            emit(State.success("Email verification sent"))
        }

    }.catch {
        emit(State.failed(it.message!!))
    }.flowOn(Dispatchers.IO)

    private suspend fun uploadImage(uid: String, image: Uri): Uri {
        val firebaseStorage = Firebase.storage
        val storageReference = firebaseStorage.reference
        val task = storageReference.child(uid + AppConstant.PROFILE_PATH)
            .putFile(image).await()
        return task.storage.downloadUrl.await()

    }

    private suspend fun createUser(userModel: User, auth: FirebaseAuth) {
        val firebase = Firebase.database.getReference("Users")
        firebase.child(auth.uid!!).setValue(userModel).await()
        val profileChangeRequest = UserProfileChangeRequest.Builder()
            .setDisplayName(userModel.fullName)
            .setPhotoUri(Uri.parse(userModel.image))
            .build()
        auth.currentUser?.apply {
            updateProfile(profileChangeRequest).await()
            sendEmailVerification().await()
        }
    }

    override suspend fun forgetPassword(email: String, context: Context): Flow<State<Any>> = flow<State<Any>> {
        emit(State.loading(true))
        val auth = Firebase.auth
        auth.sendPasswordResetEmail(email).await()
        emit(State.success(context.getString(R.string.pass_reset_sent_email)))
    }.catch {
        emit(State.failed(it.message!!))
    }.flowOn(Dispatchers.IO)
}