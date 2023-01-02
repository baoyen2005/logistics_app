package com.example.bettinalogistics.ui.activity.login

import android.app.AlertDialog
import android.content.Intent
import android.view.View
import android.widget.Button
import androidx.core.view.isVisible
import com.example.baseapp.BaseActivity
import com.example.baseapp.UtilsBase
import com.example.baseapp.view.EditText
import com.example.bettinalogistics.R
import com.example.bettinalogistics.databinding.ActivityLoginBinding
import com.example.bettinalogistics.ui.activity.main.MainActivity
import com.example.bettinalogistics.ui.activity.main.MainActivity.Companion.CHANGED_PASSWORD
import com.example.bettinalogistics.ui.activity.main.MainActivity.Companion.CHECK_FORGOT_PASSWORD
import com.example.bettinalogistics.ui.activity.signup.SignUpActivity
import com.example.bettinalogistics.utils.DataConstant.Companion.USER_EMAIL
import org.koin.androidx.viewmodel.ext.android.viewModel


class LoginActivity : BaseActivity() {
    override val viewModel: LoginViewModel by viewModel()

    override val binding: ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    override fun initView() {
        binding.edtLoginInputPassword.onTextChange = {
            binding.tvLoginPasswordError.isVisible = false
        }
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
            hiddenLoading()
            if(it){
                val intent =  Intent(this@LoginActivity, MainActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                intent.putExtra(CHECK_FORGOT_PASSWORD, viewModel.forgotPassword)
                intent.putExtra(CHANGED_PASSWORD, binding.edtLoginInputPassword.getTextEdit())
                intent.putExtra(USER_EMAIL, binding.edtEmailLogin.getContentText())
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
        val email = binding.edtEmailLogin.getContentText()
        val password = binding.edtLoginInputPassword.getTextEdit()

        var view: View? = null
        var flag = false
        when {
            email.isEmpty() -> {
                binding.edtEmailLogin.setVisibleMessageError(getString(R.string.invalid_field))
                view = binding.edtEmailLogin
                flag = true
            }
            !UtilsBase.g().isValidEmail(email) -> {
                binding.edtEmailLogin.setVisibleMessageError(getString(R.string.invalid_email))
                view = binding.edtEmailLogin
                flag = true
            }
            password.isEmpty() -> {
                binding.tvLoginPasswordError.isVisible = true
                binding.tvLoginPasswordError.text = getString(R.string.invalid_field)
                view = binding.edtLoginInputPassword
                flag = true
            }
            !UtilsBase.g().verifyPassword(password) -> {
                binding.tvLoginPasswordError.isVisible = true
                binding.tvLoginPasswordError.text = getString(R.string.invalid_pass)
                view = binding.edtLoginInputPassword
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
        val btnForgotPassAgreeDal = view.findViewById<Button>(R.id.btnForgotPassAgreeDal)
        val btnForgotPassCancelDal = view.findViewById<Button>(R.id.btnForgotPassCancelDal)
        val edtForgotPassEmailInputDlo = view.findViewById<EditText>(R.id.edtForgotPassEmailInputDlo)
        btnForgotPassAgreeDal.setOnClickListener {
            val email = edtForgotPassEmailInputDlo.text
            if (email == null || email.isEmpty()) {
                showAlertDialog(getString(R.string.EURROR_EMAIL))
            } else {
                viewModel.forget(email.toString())
                builder.dismiss()
            }
        }
        btnForgotPassCancelDal.setOnClickListener {
            builder.dismiss()
        }
        builder.setCanceledOnTouchOutside(false)
        builder.show()
    }
}