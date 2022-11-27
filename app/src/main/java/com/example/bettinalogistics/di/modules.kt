package com.example.bettinalogistics.di

import com.example.baseapp.di.AndroidResourceProvider
import com.example.baseapp.di.ResourceProvider
import com.example.baseapp.di.dialogs
import com.example.bettinalogistics.data.*
import com.example.bettinalogistics.ui.activity.add_new_order.AddNewProductViewModel
import com.example.bettinalogistics.ui.activity.addorder.OrderViewModel
import com.example.bettinalogistics.ui.activity.confirm_order.ConfirmOrderViewModel
import com.example.bettinalogistics.ui.activity.gg_map.GoogleMapViewmodel
import com.example.bettinalogistics.ui.activity.login.LoginViewModel
import com.example.bettinalogistics.ui.activity.main.MainViewModel
import com.example.bettinalogistics.ui.activity.signup.SignUpViewModel
import com.example.bettinalogistics.ui.activity.splash.SplashViewModel
import com.example.bettinalogistics.ui.fragment.followtrask.FollowTrackingViewModel
import com.example.bettinalogistics.ui.fragment.home.HomeViewModel
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
        AddNewProductViewModel(get())
    }
    viewModel {
        GoogleMapViewmodel(get())
    }
    viewModel {
        ConfirmOrderViewModel(get())
    }
    viewModel {
        FollowTrackingViewModel()
    }
}

val impls = module {
    single<AuthenticationRepository>  {
        AuthenticationRepositoryImpl()
    }

    single<OrderRepository>  {
        OrderRepositoryImpl()
    }

    single<GoogleMapRepo>  {
        GoogleMapRepoImpl()
    }
}

val utilities = module{
    single<ResourceProvider> { AndroidResourceProvider(get()) }
}

val modules = listOf(utilities, models,dialogs, impls)