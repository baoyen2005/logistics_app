package com.example.bettinalogistics

import android.content.Context
import android.net.Uri
import com.example.baseapp.BaseRepository
import com.example.bettinalogistics.model.User
import com.example.bettinalogistics.utils.AppConstant
import com.example.bettinalogistics.utils.State
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await
import java.util.*

interface AuthenticationRepository {
    suspend fun login(email: String, password: String): MutableStateFlow<State<Any>?>
    suspend fun signUp(
        image: Uri,
        fullName: String,
        phone: String,
        dateOfBirth: Date,
        password: String,
        email: String,
        address: String
    ): MutableStateFlow<State<Any>?>

    suspend fun forgetPassword(email: String, context: Context): MutableStateFlow<State<Any>?>
}

class AuthenticationRepositoryImpl : BaseRepository(), AuthenticationRepository {
    private val stateLoginFlow = MutableStateFlow<State<Any>?>(State.loading(true))
    private val stateSignInFlow = MutableStateFlow<State<Any>?>(State.loading(true))
    private val stateForgotPasswordFlow = MutableStateFlow<State<Any>?>(State.loading(true))

    override suspend fun login(
        email: String,
        password: String
    ): MutableStateFlow<State<Any>?> {
        val auth = Firebase.auth
        val data = auth.signInWithEmailAndPassword(email, password).await()
        data?.let {
            if (auth.currentUser?.isEmailVerified!!) {
                stateLoginFlow.emit(State.success("Login Successfully"))
            } else {
                auth.currentUser?.sendEmailVerification()?.await()
                stateLoginFlow.emit(State.failed("Verify email first"))
            }
        }

        stateLoginFlow.catch {
            stateLoginFlow.emit(State.failed(it.message!!))
        }
        return stateLoginFlow
    }

    override suspend fun signUp(
        image: Uri,
        fullName: String,
        phone: String,
        dateOfBirth: Date,
        password: String,
        email: String,
        address: String
    ): MutableStateFlow<State<Any>?> {

        val auth = Firebase.auth
        val data = auth.createUserWithEmailAndPassword(email, password).await()
        data.user?.let {
            val path = uploadImage(it.uid, image).toString()
            val userModel = User(path, fullName, phone, dateOfBirth, email, password, address)
            createUser(userModel, auth)
            stateSignInFlow.emit(State.success("Email verification sent"))
        }
        stateSignInFlow.catch {
            stateSignInFlow.emit(State.failed(it.message!!))
        }
        return stateSignInFlow
    }

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

    override suspend fun forgetPassword(
        email: String,
        context: Context
    ): MutableStateFlow<State<Any>?> {
        stateForgotPasswordFlow.emit(State.loading(true))
        val auth = Firebase.auth
        auth.sendPasswordResetEmail(email).await()
        stateForgotPasswordFlow.emit(State.success(context.getString(R.string.pass_reset_sent_email)))
        stateForgotPasswordFlow.catch {
            stateForgotPasswordFlow.emit(State.failed(it.message!!))
        }
        return stateForgotPasswordFlow
    }
}