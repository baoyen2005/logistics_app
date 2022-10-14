package com.example.bettinalogistics.ui.addorder

import com.example.baseapp.BaseActivity
import com.example.bettinalogistics.R
import com.example.bettinalogistics.databinding.ActivityLoginBinding
import com.example.bettinalogistics.ui.login.LoginViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class OrderActivity : BaseActivity() {
    override val layoutId: Int
        get() = R.layout.activity_order
    override val viewModel: LoginViewModel by viewModel()

    override val binding: ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }


}