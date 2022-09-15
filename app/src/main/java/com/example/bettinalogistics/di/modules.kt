package com.example.bettinalogistics.di

import com.example.baseapp.di.AndroidResourceProvider
import com.example.baseapp.di.ResourceProvider
import com.example.baseapp.di.dialogs
import com.example.bettinalogistics.AuthenticationRepository
import com.example.bettinalogistics.AuthenticationRepositoryImpl
import com.example.bettinalogistics.ui.login.LoginViewModel
import com.example.bettinalogistics.ui.signup.SignUpViewModel
import org.koin.dsl.module.module
import org.koin.androidx.viewmodel.ext.koin.viewModel

val models = module {

    viewModel {
        LoginViewModel(get())
    }
    viewModel {
        SignUpViewModel(get())
    }
}

val impls = module {
    single<AuthenticationRepository>  {
        AuthenticationRepositoryImpl()
    }
}

val utilities = module{
    single<ResourceProvider> { AndroidResourceProvider(get()) }
}

val modules = listOf(utilities, models,dialogs, impls)