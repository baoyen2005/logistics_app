package com.example.bettinalogistics.ui.activity.signup

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.baseapp.BaseViewModel
import com.example.bettinalogistics.data.AuthenticationRepository
import com.example.bettinalogistics.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SignUpViewModel(val authenticationRepository: AuthenticationRepository) : BaseViewModel() {
    var uri : Uri? = null
    var terminalUser : User? = null
    var signUpLiveData = MutableLiveData<Boolean>()

    fun signUp(user: User) = viewModelScope.launch(Dispatchers.IO){
        authenticationRepository.signUp(user = user){
            signUpLiveData.postValue(it)
        }
    }
}