package com.example.bettinalogistics.ui.fragment.admin.person

import android.content.Intent
import androidx.core.view.isVisible
import com.example.baseapp.BaseFragment
import com.example.baseapp.view.setSafeOnClickListener
import com.example.bettinalogistics.R
import com.example.bettinalogistics.databinding.FragmentAdminAccountBinding
import com.example.bettinalogistics.di.AppData
import com.example.bettinalogistics.ui.fragment.bottom_sheet.ConfirmBottomSheetFragment
import com.google.firebase.auth.FirebaseAuth
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class AdminAccountFragment : BaseFragment() {
    override val viewModel: AdminAccountViewModel by sharedViewModel()

    override val binding: FragmentAdminAccountBinding by lazy {
        FragmentAdminAccountBinding.inflate(layoutInflater)
    }

    override fun initView() {
        binding.ivAdminAccAvt.setBackgroundResource(R.drawable.ic_user)
        binding.tvAdminName.text = AppData.g().currentUser?.fullName
        binding.tvAdminEmail.text = AppData.g().currentUser?.email
        binding.headerUserPerson.ivHeaderBack.isVisible = false
        binding.headerUserPerson.tvHeaderTitle.text = getString(R.string.str_admin_info)
        hiddenLoading()
    }

    override fun initListener() {
        binding.rlAdminManageUsers.setSafeOnClickListener {
            startActivity(Intent(requireContext(), ManageUsersActivity::class.java))
        }

        binding.rlAdminLogout.setSafeOnClickListener {
            val confirmBottomSheetFragment = ConfirmBottomSheetFragment().setTitle(getString(R.string.str_title_logout)).setContent(getString(R.string.str_confirm_logout))
            confirmBottomSheetFragment.setConfirmListener {
                FirebaseAuth.getInstance().signOut()
                AppData.g().logout()
            }
            confirmBottomSheetFragment.show(requireActivity().supportFragmentManager, "ssssssss")
        }
    }

    override fun observerData() {
    }
}