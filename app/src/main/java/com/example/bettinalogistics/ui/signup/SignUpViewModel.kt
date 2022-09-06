package com.example.bettinalogistics.ui.signup

import android.app.Application
import android.net.Uri
import com.example.baseapp.BaseViewModel
import com.example.bettinalogistics.AuthenticationRepository
import java.util.*

class SignUpViewModel(application: Application) : BaseViewModel(application) {
    private val repo = AuthenticationRepository()
    private var uri : Uri? = null
    fun signUp(image: Uri,
               fullName:String,
               phone: String,
               dateOfBirth : Date,
               password: String,
               email: String,
               address: String) =
        repo.signUp(image, fullName, phone, dateOfBirth, password, email, address)

    fun setUri(uri: Uri){
        this.uri = uri
    }

    fun getUri(): Uri?{
        return uri
    }
}