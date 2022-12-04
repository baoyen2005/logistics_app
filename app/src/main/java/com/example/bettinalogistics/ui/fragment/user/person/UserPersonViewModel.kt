package com.example.bettinalogistics.ui.fragment.user.person

import android.net.Uri
import com.example.baseapp.BaseViewModel
import com.example.bettinalogistics.data.AuthenticationRepository

class UserPersonViewModel(val authenticationRepository: AuthenticationRepository) : BaseViewModel() {
    var uri : Uri? = null
    var isChangeAvt : Boolean = false
}