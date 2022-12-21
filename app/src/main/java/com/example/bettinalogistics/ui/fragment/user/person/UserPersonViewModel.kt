package com.example.bettinalogistics.ui.fragment.user.person

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.baseapp.BaseViewModel
import com.example.bettinalogistics.data.AuthenticationRepository
import com.example.bettinalogistics.data.CardRepository
import com.example.bettinalogistics.data.OrderRepository
import com.example.bettinalogistics.di.AppData
import com.example.bettinalogistics.model.Card
import com.example.bettinalogistics.model.Order
import com.example.bettinalogistics.model.User
import com.example.bettinalogistics.model.UserCompany
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserPersonViewModel(
    val authenticationRepository: AuthenticationRepository,
    val cardRepository: CardRepository, val orderRepository: OrderRepository
) : BaseViewModel() {
    var uri: Uri? = null
    var isChangeAvt: Boolean = false
    var company: UserCompany? = null
    var isEdit: Boolean = false

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
        cardRepository.addCard(card) {
            addCardLiveData.postValue(it)
        }
    }

    var updateCardLiveData = MutableLiveData<Boolean>()
    fun updateCard(card: Card) = viewModelScope.launch(Dispatchers.IO) {
        cardRepository.updateCard(card) {
            updateCardLiveData.postValue(it)
        }
    }

    var deleteCardLiveData = MutableLiveData<Boolean>()
    fun deleteCard(card: Card) = viewModelScope.launch(Dispatchers.IO) {
        cardRepository.deleteCard(card) {
            deleteCardLiveData.postValue(it)
        }
    }

    var getAllCardLiveData = MutableLiveData<List<Card>>()
    fun getAllCard() = viewModelScope.launch(Dispatchers.IO) {
        AppData.g().userId?.let {
            cardRepository.getAllCards(it) {
                getAllCardLiveData.postValue(it)
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

    var getAllOrderLiveData = MutableLiveData<List<Order>>()
    fun getAllOrderSuccess() = viewModelScope.launch(Dispatchers.IO) {
        orderRepository.getAllOrderTransactionsSuccess {
            getAllOrderLiveData.postValue(it)
        }
    }
}