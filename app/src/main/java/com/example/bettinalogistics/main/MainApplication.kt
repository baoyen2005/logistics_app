package com.example.bettinalogistics.main

import android.app.Application
import androidx.multidex.MultiDexApplication
import com.example.bettinalogistics.di.modules
import org.koin.android.ext.android.startKoin

class MainApplication : Application() {
    init {
        instance = this
    }

    override fun onCreate() {
        super.onCreate()
        startKoin(this@MainApplication, modules)
    }

    companion object {
        private var instance: MainApplication? = null

        fun getInstanceApp(): MainApplication {
            return instance as MainApplication
        }
    }
}
