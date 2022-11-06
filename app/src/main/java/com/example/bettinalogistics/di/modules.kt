package com.example.bettinalogistics.di

import com.example.baseapp.di.AndroidResourceProvider
import com.example.baseapp.di.ResourceProvider
import com.example.baseapp.di.dialogs
import com.example.bettinalogistics.data.AuthenticationRepository
import com.example.bettinalogistics.data.AuthenticationRepositoryImpl
import com.example.bettinalogistics.data.OrderRepository
import com.example.bettinalogistics.data.OrderRepositoryImpl
import com.example.bettinalogistics.ui.activity.add_new_order.AddNewOrderViewModel
import com.example.bettinalogistics.ui.activity.addorder.OrderViewModel
import com.example.bettinalogistics.ui.fragment.home.HomeViewModel
import com.example.bettinalogistics.ui.activity.login.LoginViewModel
import com.example.bettinalogistics.ui.activity.main.MainViewModel
import com.example.bettinalogistics.ui.activity.signup.SignUpViewModel
import com.example.bettinalogistics.ui.activity.splash.SplashViewModel
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
        MainViewModel(get())
    }
    viewModel {
        HomeViewModel()
    }
    viewModel {
        OrderViewModel(get())
    }
    viewModel {
        AddNewOrderViewModel(get())
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