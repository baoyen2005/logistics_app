package com.example.bettinalogistics.di

import com.example.baseapp.di.AndroidResourceProvider
import com.example.baseapp.di.ResourceProvider
import com.example.baseapp.di.dialogs
import com.example.bettinalogistics.data.AuthenticationRepository
import com.example.bettinalogistics.data.AuthenticationRepositoryImpl
import com.example.bettinalogistics.data.OrderRepository
import com.example.bettinalogistics.data.OrderRepositoryImpl
import com.example.bettinalogistics.ui.addorder.OrderViewModel
import com.example.bettinalogistics.ui.home.HomeViewModel
import com.example.bettinalogistics.ui.login.LoginViewModel
import com.example.bettinalogistics.ui.main.MainViewModel
import com.example.bettinalogistics.ui.signup.SignUpViewModel
import com.example.bettinalogistics.ui.splash.SplashViewModel
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val models = module {
    viewModel{
        SplashViewModel()
    }
    viewModel {
        LoginViewModel(get())
    }
    viewModel {
        SignUpViewModel(get())
    }
    viewModel {
        MainViewModel()
    }
    viewModel {
        HomeViewModel()
    }
    viewModel {
        OrderViewModel(get())
    }
}

val impls = module {
    single<AuthenticationRepository>  {
        AuthenticationRepositoryImpl()
    }

    single<OrderRepository>  {
        OrderRepositoryImpl()
    }
}

val utilities = module{
    single<ResourceProvider> { AndroidResourceProvider(get()) }
}

val modules = listOf(utilities, models,dialogs, impls)