package com.example.bettinalogistics.ui.login

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.example.baseapp.BaseActivity
import com.example.bettinalogistics.R
import com.example.bettinalogistics.databinding.ActivityLoginBinding
import com.example.bettinalogistics.ui.home.HomeActivity
import com.example.bettinalogistics.ui.signup.SignUpActivity.Companion.startSignUpActivity
import com.example.bettinalogistics.utils.State
import kotlinx.android.synthetic.main.dialog_forgot_password.view.*
import kotlinx.coroutines.flow.collectLatest

class LoginActivity : BaseActivity<ActivityLoginBinding, LoginViewModel>() {
    private val viewModel: LoginViewModel by viewModels()

    companion object{
        private final val TAG = "LoginActivity"
    }

    override val layoutId: Int = R.layout.activity_login

    override fun getVM(): LoginViewModel {
        return viewModel
    }

    override fun onReady(savedInstanceState: Bundle?) {
         binding.txtRegister.setOnClickListener {
             startSignUpActivity(this)
         }
        binding.btnLogin.setOnClickListener {
            areFieldReady()
            checkLogin()
        }
        binding.txtForgotPassLogin.setOnClickListener {
            forgotPassword()
        }
    }

    private fun checkLogin() {
        val email = binding.edtEmailLogin.text.trim().toString()
        val password = binding.edtPasswordLogin.text.trim().toString()
        lifecycleScope.launchWhenStarted {
            viewModel.login(email, password).collectLatest {
                when (it) {
                    is State.Loading -> {
                        if (it.flag == true)
                            showLoading()
                    }

                    is State.Success -> {
                        hiddenLoading()
                        confirm.newBuild().setNotice(it.data.toString())
                            .addButtonRight(R.string.close) {
                                startActivity(
                                    Intent(this@LoginActivity, HomeActivity::class.java)
                                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                )
                                confirm.dismiss()
                                finish()
                                ActivityCompat.finishAffinity(this@LoginActivity)
                            }.setAction(true)
                    }
                    is State.Failed -> {
                        hiddenLoading()
                        confirm.newBuild().setNotice(it.error)
                    }
                }
            }
        }
    }

    private fun areFieldReady(): Boolean {
        val email = binding.edtEmailLogin.text.trim().toString()
        val password = binding.edtPasswordLogin.text.trim().toString()

        var view: View? = null
        var flag = false
        when {
            email.isEmpty() -> {
                binding.edtEmailLogin.error = getString(R.string.invalid_field)
                view = binding.edtEmailLogin
                flag = true
            }
            password.isEmpty() -> {
                binding.edtPasswordLogin.error = getString(R.string.invalid_field)
                view = binding.edtPasswordLogin
                flag = true
            }
            password.length < 8 -> {
                binding.edtPasswordLogin.error = getString(R.string.invalid_pass)
                view = binding.edtPasswordLogin
                flag = true
            }
            password.contains(" ") -> {
                binding.edtPasswordLogin.error = getString(R.string.invalid_pass)
                view = binding.edtPasswordLogin
                flag = true
            }
        }
        return if (flag) {
            view?.requestFocus()
            false
        } else
            true
    }

    private fun forgotPassword() {
        val builder = AlertDialog.Builder(this)
            .create()
        val view = layoutInflater.inflate(R.layout.dialog_forgot_password, null)
        builder.setView(view)
        view.btnForgotPassAgreeDal.setOnClickListener {
            val email = view.edtForgotPassEmailInputDlo.text
            if (email == null || email.isEmpty()) {
                showAlertDialog(getString(R.string.EURROR_EMAIL))
                builder.dismiss()
            } else {
                lifecycleScope.launchWhenStarted {
                    viewModel.forget(email.toString(), this@LoginActivity).collectLatest {
                        when (it) {
                            is State.Loading -> {
                                if (it.flag == true)
                                    showLoading()
                            }

                            is State.Success -> {
                                hiddenLoading()
                                confirm.newBuild().setNotice(it.data.toString())
                                builder.dismiss()
                            }
                            is State.Failed -> {
                                hiddenLoading()
                                confirm.newBuild().setNotice(it.error)
                                builder.dismiss()
                            }
                        }
                    }
                }
            }
        }
        builder.setCanceledOnTouchOutside(false)
        builder.show()
    }
}