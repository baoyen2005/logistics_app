package com.example.bettinalogistics.ui.fragment.user.notification

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.baseapp.BaseViewModel
import com.example.bettinalogistics.data.OTTFirebaseRepo
import com.example.bettinalogistics.model.Notification
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserNotificationViewModel(val ottFirebaseRepo: OTTFirebaseRepo) : BaseViewModel() {
    var getAllNotificationLiveData = MutableLiveData<List<Notification>>()
    fun getAllNotification(role: String) = viewModelScope.launch(Dispatchers.IO) {
        ottFirebaseRepo.getAllNotification(role) {
            getAllNotificationLiveData.postValue(it)
        }
    }
}