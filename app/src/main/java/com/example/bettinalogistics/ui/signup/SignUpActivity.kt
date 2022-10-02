package com.example.bettinalogistics.ui.signup

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.example.baseapp.BaseActivity
import com.example.bettinalogistics.R
import com.example.bettinalogistics.databinding.ActivitySignUpBinding
import com.example.bettinalogistics.ui.main.MainActivity
import com.example.bettinalogistics.utils.AppConstant.Companion.CHOOSE_IMAGE
import com.example.bettinalogistics.utils.AppConstant.Companion.SIGN_UP_FAIL_VAL
import com.example.bettinalogistics.utils.AppConstant.Companion.TAG
import com.example.bettinalogistics.utils.AppPermissionsUtils
import com.example.bettinalogistics.utils.State
import com.example.bettinalogistics.utils.dateToString
import com.example.bettinalogistics.utils.stringToDate
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*


class SignUpActivity : BaseActivity() {
    private lateinit var appPermissions: AppPermissionsUtils

    override val layoutId: Int
        get() = R.layout.activity_login

    override val viewModel: SignUpViewModel by viewModel()

    override val binding: ActivitySignUpBinding by lazy {
        ActivitySignUpBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        appPermissions = AppPermissionsUtils()
        Log.d(TAG, "onReady: binding = $binding")
        binding.imgClickCamera.setOnClickListener {
            if (appPermissions.isStorageOk(applicationContext))
                pickImage()
            else
                appPermissions.requestStoragePermission(this)
        }

        binding.imgDatePicker.setOnClickListener {
            chooseDate()
        }

        binding.btnSignup.setOnClickListener {
            saveUser()
        }
    }

    private fun pickImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        launcher.launch(intent)
    }

    private val launcher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK
            && result.data != null
        ) {
            val photoUri: Uri? = result.data!!.data
            viewModel.setUriPhoto(photoUri!!)
            binding.imgAvt.visibility = View.VISIBLE
            binding.imgClickCamera.visibility = View.GONE
            binding.imgAvt.setImageURI(photoUri)
        } else {
            binding.imgAvt.visibility = View.GONE
            binding.imgClickCamera.visibility = View.VISIBLE
            showToast(CHOOSE_IMAGE)
        }
    }


    @SuppressLint("SetTextI18n", "ResourceType")
    private fun chooseDate() {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(this@SignUpActivity.getString(R.string.TITLE_DATE_PICKER))
            .build()
        datePicker.show(supportFragmentManager,"DatePicker")
        datePicker.addOnPositiveButtonClickListener {
            val date = dateToString(Date(it))
            binding.tvDateOfBirthSignUp.text = date
        }
        datePicker.addOnNegativeButtonClickListener {
            showToast("${datePicker.headerText} is cancelled")
        }
    }

    private fun areFieldReady(): Boolean {
        val fullName = binding.edtFullNameSignUp.text?.trim().toString()
        val phone = binding.edtPhoneSignup.text?.trim().toString()
        val dateOfBirth = binding.tvDateOfBirthSignUp.text.trim().toString()
        val email = binding.edtEmailSignUp.text?.trim().toString()
        val password = binding.edtPasswordSignup.text?.trim().toString()
        val address = binding.edtAddressSignUp.text?.trim().toString()

        var view: View? = null
        var flag = false

        when {
            fullName.isEmpty() -> {
                binding.edtFullNameSignUp.error = getString(R.string.invalid_field)
                view = binding.edtFullNameSignUp
                flag = true
            }

            phone.isEmpty() -> {
                binding.edtPhoneSignup.error = getString(R.string.invalid_field)
                view = binding.edtPhoneSignup
                flag = true
            }

            dateOfBirth.isEmpty() -> {
                binding.tvDateOfBirthSignUp.error = getString(R.string.invalid_field)
                view = binding.tvDateOfBirthSignUp
                flag = true
            }

            email.isEmpty() -> {
                binding.edtEmailSignUp.error = getString(R.string.invalid_field)
                view = binding.edtEmailSignUp
                flag = true
            }
            password.isEmpty() -> {
                binding.edtPasswordSignup.error = getString(R.string.invalid_field)
                view = binding.edtPasswordSignup
                flag = true
            }
            password.contains(" ") ->{
                binding.edtPasswordSignup.error = getString(R.string.invalid_pass)
                view = binding.edtPasswordSignup
                flag = true
            }
            password.length < 8   -> {
                binding.edtPasswordSignup.error = getString(R.string.invalid_pass)
                view = binding.edtPasswordSignup
                flag = true
            }

            address.isEmpty() -> {
                binding.edtAddressSignUp.error = getString(R.string.invalid_field)
                view = binding.edtAddressSignUp
                flag = true
            }
        }

        return if (flag) {
            view?.requestFocus()
            false
        } else
            true
    }

    private fun saveUser() {
        if (areFieldReady()) {
            val uri = viewModel.uri
            val fullName = binding.edtFullNameSignUp.text?.trim().toString()
            val phone = binding.edtPhoneSignup.text?.trim().toString()
            val dateOfBirth = binding.tvDateOfBirthSignUp.text.trim().toString()
            val email = binding.edtEmailSignUp.text?.trim().toString()
            val password = binding.edtPasswordSignup.text?.trim().toString()
            val address = binding.edtAddressSignUp.text?.trim().toString()
            val dateOB = stringToDate(dateOfBirth)
            Log.d(TAG, "saveUser: uri = $uri")
            if (uri != null) {
                lifecycleScope.launchWhenCreated {
                    viewModel.signUp(
                        uri, fullName, phone, dateOB!!,
                        password, email, address
                    ).collectLatest {
                        when (it) {
                            is State.Loading -> {
                                if (it.flag == true)
                                    showLoading()
                            }

                            is State.Success -> {
                                hiddenLoading()
                                startActivity(Intent(this@SignUpActivity, MainActivity::class.java))
                                finish()
                            }
                            is State.Failed -> {
                                hiddenLoading()
                                confirm.newBuild().setNotice(it.error)
                            }
                            else -> {
                                showToast(SIGN_UP_FAIL_VAL)
                            }
                        }
                    }
                }
            }
        }
    }
}