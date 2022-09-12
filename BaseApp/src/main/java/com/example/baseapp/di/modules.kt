package com.example.baseapp.di

import com.vnpay.base.di.AndroidResourceProvider
import com.vnpay.base.di.ResourceProvider
import org.koin.dsl.module.module


val utilities = module{
    single<ResourceProvider> { AndroidResourceProvider(get()) }
}

val modules = listOf(utilities, dialogs)