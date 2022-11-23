package com.example.bettinalogistics.ui.activity.splash

import com.example.baseapp.BaseViewModel
import com.example.baseapp.di.Common
import com.example.bettinalogistics.data.AddedProductToDbRepo
import com.example.bettinalogistics.data.database.ProductDatabase
import com.example.bettinalogistics.di.AppData
import com.example.bettinalogistics.ui.activity.splash.SplashScreen.Companion.LOGIN_SCREEN
import com.example.bettinalogistics.ui.activity.splash.SplashScreen.Companion.MAIN_SCREEN

class SplashViewModel : BaseViewModel() {

    private var isLogin: Boolean = false

    fun decideNextScreen(): String {
        if (AppData.g().isSignUp().isNullOrEmpty()){
            isLogin = false
            return LOGIN_SCREEN
        }
        return MAIN_SCREEN
    }
}