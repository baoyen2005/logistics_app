package com.example.bettinalogistics.ui.activity.signup

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.example.baseapp.BaseActivity
import com.example.bettinalogistics.R
import com.example.bettinalogistics.databinding.ActivitySignUpBinding
import com.example.bettinalogistics.di.AppData
import com.example.bettinalogistics.model.User
import com.example.bettinalogistics.ui.activity.main.MainActivity
import com.example.bettinalogistics.utils.AppConstant.Companion.CHOOSE_IMAGE
import com.example.bettinalogistics.utils.AppPermissionsUtils
import com.example.bettinalogistics.utils.dateToString
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.auth.FirebaseAuth
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*


class SignUpActivity : BaseActivity() {
    private lateinit var appPermissions: AppPermissionsUtils

    override val viewModel: SignUpViewModel by viewModel()

    override val binding: ActivitySignUpBinding by lazy {
        ActivitySignUpBinding.inflate(layoutInflater)
    }

    override fun initView() {
        appPermissions = AppPermissionsUtils()
        if (!appPermissions.isStorageOk(applicationContext)) {
            appPermissions.requestStoragePermission(this)
        }
    }

    override fun initListener() {
        binding.imgClickCamera.setOnClickListener {
            pickImage()
        }

        binding.imgDatePicker.setOnClickListener {
            chooseDate()
        }

        binding.btnSignup.setOnClickListener {
            saveUser()
        }
    }

    override fun observeData() {
        viewModel.signUpLiveData.observe(this){
            if(it) {
                AppData.g().saveUser(
                    User(
                        email = viewModel.terminalUser?.email,
                        phone = viewModel.terminalUser?.phone,
                        image = viewModel.terminalUser?.image,
                        fullName = viewModel.terminalUser?.fullName,
                        uid = FirebaseAuth.getInstance().currentUser?.uid,
                        dateOfBirth = viewModel.terminalUser?.dateOfBirth,
                        address = viewModel.terminalUser?.address,
                        password = viewModel.terminalUser?.password
                    )
                )
                confirm.newBuild().setNotice(getString(R.string.str_sign_in_success))
                    .addButtonAgree {
                        startActivity(Intent(this, MainActivity::class.java))
                    }
            }
            else confirm.newBuild().setNotice(getString(R.string.sign_in_failed))
        }
    }

    private fun pickImage() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).also {
            it.addCategory(Intent.CATEGORY_OPENABLE)
            it.type = "image/*"
            it.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
            it.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        launcher.launch(intent)
    }

    private val launcher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK
            && result.data != null
        ) {
            val photoUri: Uri? = result.data!!.data
            viewModel.uri = photoUri
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
            val user = com.example.bettinalogistics.model.User(
                image = uri.toString(), fullName = fullName,
                phone = phone, dateOfBirth = dateOfBirth, email = email, password = password,
                address = address
            )
            viewModel.terminalUser = user
           viewModel.signUp(user)
        }
    }
}