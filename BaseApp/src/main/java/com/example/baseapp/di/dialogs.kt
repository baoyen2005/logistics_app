package com.example.baseapp.di

import com.example.baseapp.dialog.ConfirmDialog
import org.koin.dsl.module.module

val dialogs = module {
    factory { createConfirmDialog() }
}

fun createConfirmDialog(): ConfirmDialog {
    return ConfirmDialog(Common.currentActivity!!)
}