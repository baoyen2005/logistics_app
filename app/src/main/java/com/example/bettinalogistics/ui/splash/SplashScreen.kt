package com.example.bettinalogistics.ui.splash

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.viewModels
import com.example.baseapp.BaseActivity
import com.example.bettinalogistics.R
import com.example.bettinalogistics.databinding.ActivitySplashScreenBinding

class SplashScreen : BaseActivity<ActivitySplashScreenBinding, SplashViewModel>() {
    private val viewModel: SplashViewModel by viewModels()

    private val handler = Handler(Looper.getMainLooper())
    private var runnable: Runnable = Runnable {
        decideNextScreen(viewModel.decideNextScreen())
    }

    override val layoutId: Int
        get() = R.layout.activity_splash_screen

    override fun getVM(): SplashViewModel = viewModel

    override fun onReady(savedInstanceState: Bundle?) {
        handler.postDelayed(runnable, 1200)
    }

    override fun onResume() {
        super.onResume()
    }

    private fun decideNextScreen(screen: String) {
        when (screen) {
            MAIN_SCREEN -> openMainScreen()
            LOGIN_SCREEN -> openLoginScreen()
        }
    }

    private fun openLoginScreen() {
//        val intent = Intent(this, LoginActivity::class.java)
//        startActivity(intent)
//        finish()
    }

    private fun openMainScreen() {
//        val intent = Intent(this, MainSampleActivity::class.java)
//        startActivity(intent)
//        finish()
    }

    companion object {
        const val MAIN_SCREEN = "MAIN_SCREEN"
        const val LOGIN_SCREEN = "LOGIN_SCREEN"
    }
}