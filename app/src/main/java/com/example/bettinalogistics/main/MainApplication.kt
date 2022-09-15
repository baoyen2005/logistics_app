package com.example.bettinalogistics.main

import androidx.multidex.MultiDexApplication
import com.example.bettinalogistics.di.modules
import org.koin.android.ext.android.startKoin

class MainApplication : MultiDexApplication() {
    init {
        instance = this
    }

    override fun onCreate() {
        super.onCreate()
        startKoin(this, modules)
    }

    companion object {
        private var instance: MainApplication? = null

        fun getInstanceApp(): MainApplication {
            return instance as MainApplication
        }
    }
}
