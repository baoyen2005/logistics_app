package com.example.bettinalogistics.ui.activity.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Handler
import android.os.Looper
import com.example.baseapp.BaseActivity
import com.example.bettinalogistics.databinding.ActivitySplashScreenBinding
import com.example.bettinalogistics.ui.activity.login.LoginActivity
import com.example.bettinalogistics.ui.activity.main.MainActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

@SuppressLint("CustomSplashScreen")
class SplashScreen : BaseActivity() {
    override val viewModel: SplashViewModel by viewModel()

    override val binding: ActivitySplashScreenBinding by lazy {
        ActivitySplashScreenBinding.inflate(layoutInflater)
    }
    private val handler = Handler(Looper.getMainLooper())
    private var runnable: Runnable = Runnable {
        decideNextScreen(viewModel.decideNextScreen())
    }


    override fun initView() {
        handler.postDelayed(runnable, 1200)
    }

    override fun initListener() {
    }

    override fun observeData() {
    }

    private fun decideNextScreen(screen: String) {
        when (screen) {
            MAIN_SCREEN -> openMainScreen()
            LOGIN_SCREEN -> openLoginScreen()
        }
    }

    private fun openLoginScreen() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun openMainScreen() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    companion object {
        const val MAIN_SCREEN = "MAIN_SCREEN"
        const val LOGIN_SCREEN = "LOGIN_SCREEN"
    }
}