package com.example.bettinalogistics.ui.fragment.admin.person

import android.content.Context
import android.content.Intent
import com.bumptech.glide.Glide
import com.example.baseapp.BaseActivity
import com.example.bettinalogistics.databinding.ActivityEditUserAccountBinding
import com.example.bettinalogistics.model.User
import com.example.bettinalogistics.utils.Utils
import org.koin.androidx.viewmodel.ext.android.viewModel

class UserDetailAdminActivity : BaseActivity() {
    companion object {
        const val DETAIL_USER = "detailUser"

        fun startDetailOrderActivity(context: Context, user: User): Intent {
            val intent = Intent(context, UserDetailAdminActivity::class.java)
            intent.putExtra(
                DETAIL_USER,
                Utils.g().getJsonFromObject(user)
            )
            return intent
        }
    }

    override val viewModel: AdminAccountViewModel by viewModel()

    override val binding: ActivityEditUserAccountBinding by lazy {
        ActivityEditUserAccountBinding.inflate(layoutInflater)
    }

    override fun initView() {
        val user = intent.getStringExtra(DETAIL_USER)
            ?.let { Utils.g().getObjectFromJson(it, User::class.java) }
        viewModel.user = user
        Glide.with(this).load(user?.image).into(binding.ivEditUserAvatar)
        binding.edtEditUserEmail.setEnableEdittext(false)
        binding.edtEditUserName.setValueContent(user?.fullName)
        binding.edtEditUserPhone.setValueContent(user?.phone)
        binding.tvEditUserDateOfBirth.text = user?.dateOfBirth ?: ""
        binding.edtEditUserEmail.setValueContent(user?.email)
        binding.edtEditUserPassword.setValueContent(user?.password)
        binding.edtEditUserPassword.setEnableEdittext(false)
        binding.edtEditUserAddress.setValueContent(user?.address)
    }

    override fun initListener() {

    }

    override fun observeData() {

    }
}