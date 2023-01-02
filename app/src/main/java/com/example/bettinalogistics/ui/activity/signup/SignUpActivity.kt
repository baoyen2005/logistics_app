package com.example.bettinalogistics.ui.activity.signup

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.text.InputType
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import com.example.baseapp.BaseActivity
import com.example.baseapp.UtilsBase
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
        binding.edtPasswordSignup.onTextChange = {
            binding.tvSignUpPasswordError.isVisible = false
        }
        binding.edtFullNameSignUp.setInputType(
            InputType.TYPE_CLASS_TEXT
                    or InputType.TYPE_TEXT_FLAG_MULTI_LINE
                    or InputType.TYPE_TEXT_FLAG_CAP_WORDS
        )
        binding.edtPhoneSignUp.setInputType(InputType.TYPE_CLASS_NUMBER)
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
            hiddenLoading()
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
            binding.tvSignUpAvatarError.isVisible = false
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
            if (binding.tvSignUpDateOfBirthError.isVisible) {
                binding.tvSignUpDateOfBirthError.isVisible = false
            }
            binding.tvDateOfBirthSignUp.text = date
        }
        datePicker.addOnNegativeButtonClickListener {
            showToast("${datePicker.headerText} is cancelled")
        }
    }

    private fun areFieldReady(): Boolean {
        val fullName = binding.edtFullNameSignUp.getContentText()
        val phone = binding.edtPhoneSignUp.getContentText()
        val dateOfBirth = binding.tvDateOfBirthSignUp.text.trim().toString()
        val email = binding.edtEmailSignUp.getContentText()
        val password = binding.edtPasswordSignup.getTextEdit()
        val address = binding.edtAddressSignUp.getContentText()
        if (fullName.isEmpty()) {
            binding.edtFullNameSignUp.setVisibleMessageError(getString(R.string.invalid_field))
        }
        if (phone.isEmpty()) {
            binding.edtPhoneSignUp.setVisibleMessageError(getString(R.string.invalid_field))
        } else if (!UtilsBase.g().isValidPhone(phone)) {
            binding.edtPhoneSignUp.setVisibleMessageError(getString(R.string.invalid_phone))
        }
        if (dateOfBirth.isEmpty()) {
            binding.tvSignUpDateOfBirthError.isVisible = true
            binding.tvSignUpDateOfBirthError.text = getString(R.string.invalid_field)
        }
        if (email.isEmpty()) {
            binding.edtEmailSignUp.setVisibleMessageError(getString(R.string.invalid_field))
        } else if (!UtilsBase.g().isValidEmail(email)) {
            binding.edtEmailSignUp.setVisibleMessageError(getString(R.string.invalid_email))
        }
        if (password.isEmpty()) {
            binding.tvSignUpPasswordError.isVisible = true
            binding.tvSignUpPasswordError.text = getString(R.string.invalid_field)
        } else if (!UtilsBase.g().verifyPassword(password)) {
            binding.tvSignUpPasswordError.isVisible = true
            binding.tvSignUpPasswordError.text = getString(R.string.invalid_pass)
        }
        if (address.isEmpty()) {
            binding.edtAddressSignUp.setVisibleMessageError(getString(R.string.invalid_field))
        }
        if (viewModel.uri == null) {
            binding.tvSignUpAvatarError.isVisible = true
            binding.tvSignUpAvatarError.text = getString(R.string.invalid_field)
        }
        return !binding.edtEmailSignUp.isTvErrorVisible()
                && !binding.edtFullNameSignUp.isTvErrorVisible()
                && !binding.edtPhoneSignUp.isTvErrorVisible()
                && !binding.edtAddressSignUp.isTvErrorVisible()
                && !binding.tvSignUpDateOfBirthError.isVisible
                && !binding.tvSignUpDateOfBirthError.isVisible
                && !binding.tvSignUpAvatarError.isVisible
    }

    private fun saveUser() {
        if (areFieldReady()) {
            val uri = viewModel.uri
            val fullName = binding.edtFullNameSignUp.getContentText()
            val phone = binding.edtPhoneSignUp.getContentText()
            val dateOfBirth = binding.tvDateOfBirthSignUp.text.trim().toString()
            val email = binding.edtEmailSignUp.getContentText()
            val password = binding.edtPasswordSignup.getTextEdit()
            val address = binding.edtAddressSignUp.getContentText()
            val user = User(
                image = uri.toString(),
                fullName = fullName,
                phone = phone,
                dateOfBirth = dateOfBirth,
                email = email,
                password = password,
                address = address
            )
            viewModel.terminalUser = user
            showLoading()
            viewModel.signUp(user)
        }
    }
}