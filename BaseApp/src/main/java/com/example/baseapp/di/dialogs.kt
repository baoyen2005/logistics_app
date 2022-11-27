package com.example.baseapp.di

import com.example.baseapp.dialog.ConfirmDialog
import com.example.baseapp.dialog.LoadingDialog
import com.example.baseapp.dialog.LoadingDialogVn
import org.koin.dsl.module.module

val dialogs = module {
    factory { createConfirmDialog() }
    factory { createLoadingDialog() }
}

fun createConfirmDialog(): ConfirmDialog {
    return ConfirmDialog(Common.currentActivity!!)
}

fun createLoadingDialog(): LoadingDialogVn {
    return LoadingDialogVn(Common.currentActivity)
}