package com.example.bettinalogistics.ui.login

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.example.baseapp.BaseViewModel
import com.example.bettinalogistics.AuthenticationRepository
import com.example.bettinalogistics.utils.State
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(val authenticationRepo: AuthenticationRepository) : BaseViewModel() {
    var stateLoginFlow: MutableStateFlow<State<Any>?> = MutableStateFlow(null)
    var stateForgotPasswordFlow: MutableStateFlow<State<Any>?> = MutableStateFlow(null)

    fun login(email: String, password: String) {
        viewModelScope.launch(iODispatchers) {
            val state = authenticationRepo.login(email, password)
            state.let {
                stateLoginFlow = state
            }
        }
    }

    fun forget(email: String, context: Context) {
        viewModelScope.launch(iODispatchers) {
            val state =  authenticationRepo.forgetPassword(email, context)
            state.let {
                stateForgotPasswordFlow = state
            }
        }
    }
}