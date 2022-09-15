package com.example.bettinalogistics.ui.signup

import android.app.Application
import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.example.baseapp.BaseViewModel
import com.example.bettinalogistics.AuthenticationRepository
import com.example.bettinalogistics.utils.State
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.*

class SignUpViewModel(val authenticationRepository: AuthenticationRepository) : BaseViewModel() {
    var stateSignInFlow: MutableStateFlow<State<Any>?> = MutableStateFlow(null)

    private var uri : Uri? = null
    fun signUp(image: Uri,
               fullName:String,
               phone: String,
               dateOfBirth : Date,
               password: String,
               email: String,
               address: String) {
        viewModelScope.launch(iODispatchers){
            val state = authenticationRepository.signUp(image, fullName, phone, dateOfBirth, password, email, address)
            stateSignInFlow = state
        }
    }

    fun setUri(uri: Uri){
        this.uri = uri
    }

    fun getUri(): Uri?{
        return uri
    }
}