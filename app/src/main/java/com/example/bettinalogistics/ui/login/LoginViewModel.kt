package com.example.bettinalogistics.ui.login

import android.app.Application
import com.example.baseapp.BaseViewModel
import com.example.bettinalogistics.AuthenticationRepository

class LoginViewModel(application: Application) : BaseViewModel(application){
    private val repo = AuthenticationRepository()

    fun login(email: String, password: String) = repo.login(email, password)

    fun forget(email: String) = repo.forgetPassword(email)
}