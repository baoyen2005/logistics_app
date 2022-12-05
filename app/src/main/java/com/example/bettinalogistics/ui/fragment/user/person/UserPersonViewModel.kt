package com.example.bettinalogistics.ui.fragment.user.person

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.baseapp.BaseViewModel
import com.example.bettinalogistics.data.AuthenticationRepository
import com.example.bettinalogistics.di.AppData
import com.example.bettinalogistics.model.Card
import com.example.bettinalogistics.model.User
import com.example.bettinalogistics.model.UserCompany
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserPersonViewModel(val authenticationRepository: AuthenticationRepository) : BaseViewModel() {
    var uri: Uri? = null
    var isChangeAvt: Boolean = false
    var company: UserCompany? = null

    var editUserLiveData = MutableLiveData<Boolean>()
    fun editUser(user: User) = viewModelScope.launch(Dispatchers.IO) {
        authenticationRepository.editUser(user, isChangeAvt) {
            editUserLiveData.postValue(it)
        }
    }

    var getCompanyLiveData = MutableLiveData<UserCompany>()
    fun getCompany() = viewModelScope.launch(Dispatchers.IO) {
        AppData.g().userId?.let {
            authenticationRepository.getCompany(it) {
                getCompanyLiveData.postValue(it)
            }
        }
    }

    var updateCompanyLiveData = MutableLiveData<Boolean>()
    fun updateCompany(companyEdit: UserCompany) = viewModelScope.launch(Dispatchers.IO) {
        authenticationRepository.updateCompany(companyEdit) {
            updateCompanyLiveData.postValue(it)
        }
    }

    var addCardLiveData = MutableLiveData<Boolean>()
    fun addCard(card: Card) = viewModelScope.launch(Dispatchers.IO) {
        authenticationRepository.addCard(card) {
            addCardLiveData.postValue(it)
        }
    }

    var updateCardLiveData = MutableLiveData<Boolean>()
    fun updateCard(card: Card) = viewModelScope.launch(Dispatchers.IO) {
        authenticationRepository.updateCard(card) {
            updateCardLiveData.postValue(it)
        }
    }
    var getCardLiveData = MutableLiveData<Card>()
    fun getCard() = viewModelScope.launch(Dispatchers.IO) {
        AppData.g().userId?.let {
            authenticationRepository.getCard(it) {
                getCardLiveData.postValue(it)
            }
        }
    }

    var getNewUserLiveData = MutableLiveData<User>()
    fun getNewUser() = viewModelScope.launch(Dispatchers.IO) {
        AppData.g().currentUser?.email?.let {
            authenticationRepository.getUser(it) {
                getNewUserLiveData.postValue(it)
            }
        }
    }
}