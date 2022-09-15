package com.example.baseapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.MutableStateFlow

abstract class BaseViewModel : ViewModel() {
    //init ViewModel Scope
    var viewModelJob = SupervisorJob()
    var uiDispatchers = Dispatchers.Main
    var iODispatchers = Dispatchers.IO
    var uiScope = CoroutineScope(uiDispatchers + viewModelJob)
    var ioScope = CoroutineScope(iODispatchers + viewModelJob)

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
        uiScope.coroutineContext.cancelChildren()
        ioScope.coroutineContext.cancelChildren()
    }


}