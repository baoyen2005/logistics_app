package com.example.baseapp.di

import com.example.baseapp.dialog.ConfirmDialog
import com.example.baseapp.dialog.LoadingDialog
import org.koin.dsl.module.module

val dialogs = module {
    factory { createLoadingDialog() }
    factory { createConfirmDialog() }
}

fun createLoadingDialog(): LoadingDialog {
    return LoadingDialog(Common.currentActivity)
}

fun createConfirmDialog(): ConfirmDialog {
    return ConfirmDialog(Common.currentActivity!!)
}