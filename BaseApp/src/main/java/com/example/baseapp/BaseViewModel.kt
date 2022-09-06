package com.example.baseapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.MutableStateFlow

abstract class BaseViewModel (application : Application) : AndroidViewModel(application) {
    //init ViewModel Scope
    var viewModelJob = SupervisorJob()
    var uiDispatchers = Dispatchers.Main
    var bgDispatchers = Dispatchers.IO
    var uiScope = CoroutineScope(uiDispatchers + viewModelJob)
    var bgScope = CoroutineScope(bgDispatchers + viewModelJob)

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
        uiScope.coroutineContext.cancelChildren()
        bgScope.coroutineContext.cancelChildren()
    }


}