package com.example.bettinalogistics.ui.fragment.user.person

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.example.baseapp.BaseActivity
import com.example.baseapp.UtilsBase
import com.example.bettinalogistics.R
import com.example.bettinalogistics.databinding.ActivityEditUserAccountBinding
import com.example.bettinalogistics.di.AppData
import com.example.bettinalogistics.model.User
import com.example.bettinalogistics.utils.AppConstant
import com.example.bettinalogistics.utils.AppPermissionsUtils
import com.example.bettinalogistics.utils.Utils
import com.example.bettinalogistics.utils.dateToString
import com.google.android.material.datepicker.MaterialDatePicker
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class EditUserAccountActivity : BaseActivity() {
    companion object {
        const val IS_EDIT_ACCOUNT = "isEditAccount"
    }

    private lateinit var appPermissions: AppPermissionsUtils
    override val viewModel: UserPersonViewModel by viewModel()

    override val binding: ActivityEditUserAccountBinding by lazy {
        ActivityEditUserAccountBinding.inflate(layoutInflater)
    }

    override fun initView() {
        val isEdit = intent.getBooleanExtra(IS_EDIT_ACCOUNT, false)
        viewModel.isEdit = isEdit
        binding.btnEditUserSave.isVisible = isEdit
        binding.ivEditUserChooseDob.isVisible = isEdit
        appPermissions = AppPermissionsUtils()
        if (!appPermissions.isStorageOk(applicationContext)) {
            appPermissions.requestStoragePermission(this)
        }
        binding.edtEditUserEmail.setEnableEdittext(false)
        binding.edtEditUserPassword.setEnableEdittext(false)
        if (isEdit) {
            binding.edtEditUserName.setEnableEdittext(true)
            binding.edtEditUserPhone.setEnableEdittext(true)
            binding.edtEditUserEmail.setEnableEdittext(true)
            binding.edtEditUserPassword.setEnableEdittext(true)
            binding.edtEditUserAddress.setEnableEdittext(true)
            binding.ivEditUserAvatar.isEnabled = true

            binding.edtEditUserName.isShowIconClearText(true)
            binding.edtEditUserPhone.isShowIconClearText(true)
            binding.edtEditUserEmail.isShowIconClearText(true)
            binding.edtEditUserPassword.isShowIconClearText(true)
            binding.edtEditUserAddress.isShowIconClearText(true)
        } else {
            binding.edtEditUserName.setEnableEdittext(false)
            binding.edtEditUserPhone.setEnableEdittext(false)
            binding.edtEditUserEmail.setEnableEdittext(false)
            binding.edtEditUserPassword.setEnableEdittext(false)
            binding.edtEditUserAddress.setEnableEdittext(false)
            binding.ivEditUserAvatar.isEnabled = false

            binding.edtEditUserName.isShowIconClearText(false)
            binding.edtEditUserPhone.isShowIconClearText(false)
            binding.edtEditUserEmail.isShowIconClearText(false)
            binding.edtEditUserPassword.isShowIconClearText(false)
            binding.edtEditUserAddress.isShowIconClearText(false)
        }
        val user = AppData.g().currentUser
        Glide.with(this).load(user?.image).into(binding.ivEditUserAvatar)
        binding.edtEditUserName.setValueContent(user?.fullName)
        binding.edtEditUserPhone.setValueContent(user?.phone)
        binding.tvEditUserDateOfBirth.text = user?.dateOfBirth ?: ""
        binding.edtEditUserEmail.setValueContent(user?.email)
        binding.edtEditUserPassword.setValueContent(user?.password)
        binding.edtEditUserAddress.setValueContent(user?.address)
        binding.edtEditUserName.onTextChange = {
            binding.btnEditUserSave.isVisible = true
        }
        binding.edtEditUserAddress.onTextChange = {
            binding.btnEditUserSave.isVisible = true
        }
        binding.edtEditUserPassword.onTextChange = {
            binding.btnEditUserSave.isVisible = true
        }
    }

    override fun initListener() {
        binding.ivEditUserChooseDob.setOnClickListener {
            binding.btnEditUserSave.isVisible = true
            chooseDate()
        }
        binding.cvEditUserAvartar.setOnClickListener {
            if (viewModel.isEdit) {
                pickImage()
            }
        }
        binding.btnEditUserSave.setOnClickListener {
            if (checkValidate()) {
                val currentUser = AppData.g().currentUser
                val image = if (viewModel.uri == null) {
                    currentUser?.image
                } else viewModel.uri.toString()
                val userEdit = User(
                    uid = currentUser?.uid,
                    fullName = binding.edtEditUserName.getContentText(),
                    image = image,
                    phone = binding.edtEditUserPhone.getContentText(),
                    dateOfBirth = binding.tvEditUserDateOfBirth.text.trim().toString(),
                    email = binding.edtEditUserEmail.getContentText(),
                    password = binding.edtEditUserPassword.getContentText(),
                    address = binding.edtEditUserAddress.getContentText(),
                    role = "user",
                )
                showLoading()
                viewModel.editUser(userEdit)
            }
        }
    }

    override fun observeData() {
        viewModel.editUserLiveData.observe(this){
            if(it){
                viewModel.getNewUser()
            }
            else{
                hiddenLoading()
                confirm.newBuild().setNotice(getString(R.string.str_edit_user_fail))
            }
        }
        viewModel.getNewUserLiveData.observe(this){
            hiddenLoading()
            AppData.g().clear()
            AppData.g().saveUser(it)
            AppData.g().currentUser = it
            confirm.newBuild().setNotice(getString(R.string.str_edit_user_success)).addButtonAgree {
                setResult(RESULT_OK)
                finish()
            }
        }
    }

    private fun checkValidate(): Boolean {
        if (binding.edtEditUserName.getContentText().isEmpty()) {
            binding.edtEditUserName.setVisibleMessageError(
                getString(
                    R.string.str_empty_input,
                    getString(R.string.full_name)
                )
            )
        }
        if (binding.edtEditUserPhone.getContentText().isEmpty()) {
            binding.edtEditUserPhone.setVisibleMessageError(
                getString(
                    R.string.str_empty_input,
                    getString(R.string.phone)
                )
            )
        }
        if (!UtilsBase.g().isValidPhone(binding.edtEditUserPhone.getContentText())) {
            binding.edtEditUserPhone.setVisibleMessageError(
                getString(
                    R.string.str_invalid_input,
                    getString(R.string.phone)
                )
            )
        }
        if (binding.edtEditUserPassword.getContentText().isEmpty()) {
            binding.edtEditUserPassword.setVisibleMessageError(
                getString(
                    R.string.str_empty_input,
                    getString(R.string.password)
                )
            )
        }
        if (!Utils.g().verifyPassword(binding.edtEditUserPassword.getContentText())) {
            binding.edtEditUserPassword.setVisibleMessageError(getString(R.string.str_invalid_password))
        }
        if (binding.edtEditUserAddress.getContentText().isEmpty()) {
            binding.edtEditUserAddress.setVisibleMessageError(
                getString(
                    R.string.str_empty_input,
                    getString(R.string.address)
                )
            )
        }
        if (binding.tvEditUserDateOfBirth.text.isNullOrEmpty()) {
            binding.tvDateOfbError.isVisible = true
            binding.tvDateOfbError.text =
                getString(R.string.str_empty_input, getString(R.string.date_of_birth))
        }
        else{
            binding.tvDateOfbError.isVisible = false
        }
        return !binding.tvDateOfbError.isVisible
                && !binding.edtEditUserName.isTvErrorVisible()
                && !binding.edtEditUserPassword.isTvErrorVisible()
                && !binding.edtEditUserPhone.isTvErrorVisible()
                && !binding.edtEditUserAddress.isTvErrorVisible()
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
            viewModel.isChangeAvt = true
            binding.ivEditUserAvatar.setImageURI(photoUri)
            binding.btnEditUserSave.isVisible = true
        } else {
            viewModel.isChangeAvt = false
            Glide.with(this).load(AppData.g().currentUser?.image).into(binding.ivEditUserAvatar)
            showToast(AppConstant.CHOOSE_IMAGE)
        }
    }

    @SuppressLint("SetTextI18n", "ResourceType")
    private fun chooseDate() {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(getString(R.string.TITLE_DATE_PICKER))
            .build()
        datePicker.show(supportFragmentManager, "DatePicker")
        datePicker.addOnPositiveButtonClickListener {
            val date = dateToString(Date(it))
            binding.tvEditUserDateOfBirth.text = date
            binding.tvDateOfbError.isVisible = false
        }
        datePicker.addOnNegativeButtonClickListener {
            showToast("${datePicker.headerText} is cancelled")
        }
    }
}