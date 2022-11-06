package com.example.bettinalogistics.ui.activity.login

import android.app.AlertDialog
import android.content.Intent
import android.view.View
import com.example.baseapp.BaseActivity
import com.example.bettinalogistics.R
import com.example.bettinalogistics.databinding.ActivityLoginBinding
import com.example.bettinalogistics.ui.activity.main.MainActivity
import com.example.bettinalogistics.ui.activity.main.MainActivity.Companion.CHANGED_PASSWORD
import com.example.bettinalogistics.ui.activity.main.MainActivity.Companion.CHECK_FORGOT_PASSWORD
import com.example.bettinalogistics.ui.activity.signup.SignUpActivity
import com.example.bettinalogistics.utils.DataConstant.Companion.USER_EMAIL
import kotlinx.android.synthetic.main.dialog_forgot_password.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class LoginActivity : BaseActivity() {
    override val viewModel: LoginViewModel by viewModel()

    override val binding: ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    override fun initView() {
    }

    override fun initListener() {
        binding.txtLoginRegister.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.btnLogin.setOnClickListener {
            areFieldReady()
        }
        binding.txtForgotPassLogin.setOnClickListener {
            forgotPassword()
        }
    }

    override fun observeData() {
        viewModel.loginLiveData.observe(this){
            if(it){
                val intent =  Intent(this@LoginActivity, MainActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                intent.putExtra(CHECK_FORGOT_PASSWORD, viewModel.forgotPassword)
                intent.putExtra(CHANGED_PASSWORD, binding.edtPasswordLogin.text.toString())
                intent.putExtra(USER_EMAIL, binding.edtEmailLogin.text.toString())
                startActivity(intent)
                finish()
            }
            else {
                confirm.newBuild().setNotice(getString(R.string.ERRO_LOGIN))
            }
        }

        viewModel.forgetLiveData.observe(this){
            if(it){
                viewModel.forgotPassword = true
                confirm.newBuild().setNotice(getString(R.string.pass_reset_sent_email))
            }
            else{
                viewModel.forgotPassword = false
                confirm.newBuild().setNotice(getString(R.string.pass_reset_fail))
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
            else -> {
                showLoading()
                viewModel.login(email = email, password = password)
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
            } else {
                viewModel.forget(email.toString())
                builder.dismiss()
            }
        }
        view.btnForgotPassCancelDal.setOnClickListener {
            builder.dismiss()
        }
        builder.setCanceledOnTouchOutside(false)
        builder.show()
    }
}