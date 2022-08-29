package com.example.bettinalogistics.ui.splash

import android.app.Application
import com.example.baseapp.BaseViewModel
import com.example.bettinalogistics.ui.splash.SplashScreen.Companion.LOGIN_SCREEN
import com.example.bettinalogistics.ui.splash.SplashScreen.Companion.MAIN_SCREEN

class SplashViewModel(application: Application) : BaseViewModel(application) {
    private var isLogin: Boolean = false

    fun decideNextScreen(): String {
        if (!isLogin) {
            isLogin = true
            return LOGIN_SCREEN
        }
        return MAIN_SCREEN
    }
}