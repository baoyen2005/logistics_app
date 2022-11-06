package com.example.bettinalogistics.ui.activity.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.baseapp.BaseViewModel
import com.example.bettinalogistics.data.AuthenticationRepository
import com.example.bettinalogistics.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(val authenticationRepo: AuthenticationRepository) : BaseViewModel() {
    var changedPassword :String? = null
    var email :String? = null

    var getUserLiveData = MutableLiveData<User?>()
    fun getUser(email: String) = viewModelScope.launch(Dispatchers.IO){
        authenticationRepo.getUser(email){
            getUserLiveData.postValue(it)
        }
    }

    var updatePasswordLiveData = MutableLiveData<Boolean>()
    fun updatePassword(password: String) = viewModelScope.launch(Dispatchers.IO){
        authenticationRepo.updatePassword(password){
            updatePasswordLiveData.postValue(it)
        }
    }
}