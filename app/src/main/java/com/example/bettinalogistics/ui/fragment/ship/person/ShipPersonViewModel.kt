package com.example.bettinalogistics.ui.fragment.ship.person

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.baseapp.BaseViewModel
import com.example.bettinalogistics.data.AuthenticationRepository
import com.example.bettinalogistics.data.CardRepository
import com.example.bettinalogistics.di.AppData
import com.example.bettinalogistics.model.Card
import com.example.bettinalogistics.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ShipPersonViewModel(val authenticationRepository: AuthenticationRepository, val cardRepository: CardRepository) : BaseViewModel() {
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
    var getCardLiveData = MutableLiveData<Card>()
    fun getCard() = viewModelScope.launch(Dispatchers.IO) {
        AppData.g().userId?.let {
            cardRepository.getCard(it) {
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