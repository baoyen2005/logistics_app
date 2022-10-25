package com.example.bettinalogistics.ui.activity.login

import android.content.Context
import com.example.baseapp.BaseViewModel
import com.example.bettinalogistics.data.AuthenticationRepository

class LoginViewModel(val authenticationRepo: AuthenticationRepository) : BaseViewModel() {

    suspend fun login(email: String, password: String) = authenticationRepo.login(email, password)

    suspend fun forget(email: String, context: Context) = authenticationRepo.forgetPassword(email,context)
}