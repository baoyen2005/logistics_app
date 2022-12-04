package com.example.bettinalogistics.ui.fragment.user.person

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.example.baseapp.BaseActivity
import com.example.bettinalogistics.R
import com.example.bettinalogistics.databinding.ActivityEditUserAccountBinding
import com.example.bettinalogistics.di.AppData
import com.example.bettinalogistics.model.User
import com.example.bettinalogistics.utils.AppConstant
import com.example.bettinalogistics.utils.AppPermissionsUtils
import com.example.bettinalogistics.utils.dateToString
import com.google.android.material.datepicker.MaterialDatePicker
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class EditUserAccountActivity : BaseActivity() {
    private lateinit var appPermissions: AppPermissionsUtils
    override val viewModel: UserPersonViewModel by viewModel()

    override val binding: ActivityEditUserAccountBinding by lazy {
        ActivityEditUserAccountBinding.inflate(layoutInflater)
    }

    override fun initView() {
        appPermissions = AppPermissionsUtils()
        if (!appPermissions.isStorageOk(applicationContext)) {
            appPermissions.requestStoragePermission(this)
        }
        val user = AppData.g().currentUser
        Glide.with(this).load(user?.image).into(binding.ivEditUserAvatar)
        binding.edtEditUserEmail.setEnableEdittext(false)
        binding.edtEditUserName.setValueContent(user?.fullName)
        binding.tvEditUserDateOfBirth.text = user?.phone ?: ""
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
            pickImage()
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
            }
        }
    }

    override fun observeData() {

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
        } else {
            viewModel.isChangeAvt = false
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
        }
        datePicker.addOnNegativeButtonClickListener {
            showToast("${datePicker.headerText} is cancelled")
        }
    }
}