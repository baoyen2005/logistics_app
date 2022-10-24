package com.example.bettinalogistics.ui.splash

import com.example.baseapp.BaseViewModel
import com.example.bettinalogistics.di.AppData
import com.example.bettinalogistics.ui.splash.SplashScreen.Companion.LOGIN_SCREEN
import com.example.bettinalogistics.ui.splash.SplashScreen.Companion.MAIN_SCREEN

class SplashViewModel : BaseViewModel() {
    private var isLogin: Boolean = false

    fun decideNextScreen(): String {
        if (!AppData.g().isLogined()){
            isLogin = false
            return LOGIN_SCREEN
        }
        return MAIN_SCREEN
    }
}