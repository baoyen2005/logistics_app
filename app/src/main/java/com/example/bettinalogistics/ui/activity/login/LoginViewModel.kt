package com.example.bettinalogistics.ui.activity.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.baseapp.BaseViewModel
import com.example.bettinalogistics.data.AuthenticationRepository
import com.example.bettinalogistics.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel(val authenticationRepo: AuthenticationRepository) : BaseViewModel() {
    var loginLiveData = MutableLiveData<Boolean>()
    fun login(email: String, password: String) = viewModelScope.launch(Dispatchers.IO){
        authenticationRepo.login(email, password){
            loginLiveData.postValue(it)
        }
    }

    var forgetLiveData = MutableLiveData<Boolean>()
    fun forget(email: String) = viewModelScope.launch(Dispatchers.IO){
        authenticationRepo.forgetPassword(email){
            forgetLiveData.postValue(it)
        }
    }

    var getUserLiveData = MutableLiveData<User?>()
    fun getUser(email: String) = viewModelScope.launch(Dispatchers.IO){
        val data = authenticationRepo.getUser(email)
        data.let {
            getUserLiveData.postValue(it)
        }
    }
}