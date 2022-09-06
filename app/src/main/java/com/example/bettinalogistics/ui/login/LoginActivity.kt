package com.example.bettinalogistics.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.baseapp.BaseActivity
import com.example.bettinalogistics.R
import com.example.bettinalogistics.databinding.ActivityLoginBinding
import com.example.bettinalogistics.ui.signup.SignUpActivity.Companion.startSignUpActivity
import com.example.bettinalogistics.ui.signup.SignUpViewModel
import com.example.bettinalogistics.utils.AppConstant.Companion.SIGN_IN_FAIL_VAL
import com.example.bettinalogistics.utils.State
import com.google.android.material.snackbar.Snackbar
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
            checkLogin()
        }
        binding.txtForgotPassLogin.setOnClickListener {
            forgotPassword()
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
                binding.edtPasswordLogin.error = getString(R.string.invalid_password)
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

    private fun checkLogin() {
        if (areFieldReady()) {
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
                        Snackbar.make(
                            binding.root,
                            it.data.toString(),
                            Snackbar.LENGTH_SHORT
                        ).show()
                   //     val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    //    startActivity(intent)
                    //    finish()

                    }
                    is State.Failed -> {
                        hiddenLoading()
                        Snackbar.make(
                            binding.root,
                            it.error,
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                    else -> {
                        showToast(SIGN_IN_FAIL_VAL)
                    }
                }
            }
            }
        }
    }

    private fun forgotPassword() {
        val builder = AlertDialog.Builder(this,R.style.CustomAlertDialog)
            .create()
        val view = layoutInflater.inflate(R.layout.customView_layout,null)
        val  button = view.findViewById<Button>(R.id.dialogDismiss_button)
        builder.setView(view)
        button.setOnClickListener {
            builder.dismiss()
        }
        builder.setCanceledOnTouchOutside(false)
        builder.show()
    }
}