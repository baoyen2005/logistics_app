package com.example.bettinalogistics.ui.fragment.user.person

import android.content.Intent
import android.util.Log
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.example.baseapp.BaseFragment
import com.example.baseapp.view.setSafeOnClickListener
import com.example.bettinalogistics.R
import com.example.bettinalogistics.databinding.FragmentPersonBinding
import com.example.bettinalogistics.di.AppData
import com.example.bettinalogistics.ui.fragment.bottom_sheet.ConfirmBottomSheetFragment
import com.example.bettinalogistics.ui.fragment.bottom_sheet.CustomerCompanyInfoBottomSheet
import com.example.bettinalogistics.ui.fragment.user.person.EditUserAccountActivity.Companion.IS_EDIT_ACCOUNT
import com.example.bettinalogistics.utils.AppConstant.Companion.TAG
import com.google.firebase.auth.FirebaseAuth
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class UserPersonFragment : BaseFragment() {
    override val viewModel: UserPersonViewModel by sharedViewModel()

    override val binding: FragmentPersonBinding by lazy {
        FragmentPersonBinding.inflate(layoutInflater)
    }

    override fun initView() {
        showLoading()
        val user = AppData.g().currentUser
        Glide.with(this).load(user?.image).into(binding.ivUserPersonAvt)
        binding.tvUserPersonName.text = user?.fullName
        binding.tvUserPersonEmail.text = user?.email
        binding.headerUserPerson.tvHeaderTitle.text = getString(R.string.str_account)
        binding.headerUserPerson.ivHeaderBack.isVisible = false
        viewModel.getCompany()
    }

    override fun initListener() {
        binding.rlEditInfoUser.setSafeOnClickListener {
            val intent = Intent(requireContext(), EditUserAccountActivity::class.java)
            intent.putExtra(IS_EDIT_ACCOUNT, true)
            startActivity(intent)
        }
        binding.rlEditInfoCompany.setSafeOnClickListener {
            val companyInfo = CustomerCompanyInfoBottomSheet()
            companyInfo.company = viewModel.company
            companyInfo.onConfirmListener = { company ->
                showLoading()
                company.id = viewModel.company?.id
                viewModel.updateCompany(company)
            }
            companyInfo.show(requireActivity().supportFragmentManager, "aaaaaaa")
        }
        binding.rlConnectCard.setSafeOnClickListener {
           startActivity(Intent(requireContext(), CardsManagerActivity::class.java))
        }
        binding.rlUserLogout.setSafeOnClickListener {
            val confirmBottomSheetFragment = ConfirmBottomSheetFragment().setTitle(getString(R.string.str_title_logout)).setContent(getString(R.string.str_confirm_logout))
            confirmBottomSheetFragment.setConfirmListener {
                FirebaseAuth.getInstance().signOut()
                AppData.g().logout()
            }
            confirmBottomSheetFragment.setCancelListener {
                confirmBottomSheetFragment.dismiss()
            }
            confirmBottomSheetFragment.show(requireActivity().supportFragmentManager, "Sss")
        }
    }

    override fun observerData() {
        viewModel.getCompanyLiveData.observe(this) {
            hiddenLoading()
            if (it != null) {
                viewModel.company = it
            } else {
                Log.d(TAG, "observerData: usercompany = null")
            }
        }
        viewModel.updateCompanyLiveData.observe(this) {
            hiddenLoading()
            if (it) {
                confirm.newBuild().setNotice(getString(R.string.str_edit_user_success))
            } else {
                confirm.newBuild().setNotice(getString(R.string.str_edit_user_fail))
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if(viewModel.company == null){
            viewModel.getCompany()
        }
    }
}