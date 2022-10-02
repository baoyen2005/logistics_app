package com.example.bettinalogistics.ui.signup

import android.net.Uri
import com.example.baseapp.BaseViewModel
import com.example.bettinalogistics.data.AuthenticationRepository
import com.example.bettinalogistics.utils.State
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.*

class SignUpViewModel(val authenticationRepository: AuthenticationRepository) : BaseViewModel() {
    var stateSignInFlow: MutableStateFlow<State<Any>?> = MutableStateFlow(null)

    var uri : Uri? = null
    suspend fun signUp(image: Uri,
                       fullName:String,
                       phone: String,
                       dateOfBirth : Date,
                       password: String,
                       email: String,
                       address: String) =
        authenticationRepository.signUp(image, fullName, phone, dateOfBirth, password, email, address)

    fun setUriPhoto(uri: Uri?){
        this.uri = uri
    }
}