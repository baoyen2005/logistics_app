package com.example.bettinalogistics.ui.fragment.admin.person

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.baseapp.BaseViewModel
import com.example.bettinalogistics.data.AuthenticationRepository
import com.example.bettinalogistics.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AdminAccountViewModel(val authenticationRepository: AuthenticationRepository) :
    BaseViewModel() {
    var user : User?= null
    var getAllUserLiveData = MutableLiveData<List<User>?>()
    fun getAllUser() = viewModelScope.launch(Dispatchers.IO) {
        authenticationRepository.getAllUsers {
            getAllUserLiveData.postValue(it)
        }
    }

    var deleteUserLiveData = MutableLiveData<Boolean>()
    fun deleteUser(user: User) = viewModelScope.launch(Dispatchers.IO) {
        authenticationRepository.deleteUser(user) {
            deleteUserLiveData.postValue(it)
        }
    }
}