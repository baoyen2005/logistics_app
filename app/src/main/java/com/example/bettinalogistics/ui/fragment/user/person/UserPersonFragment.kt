package com.example.bettinalogistics.ui.fragment.user.person

import android.content.Intent
import android.util.Log
import com.example.baseapp.BaseFragment
import com.example.baseapp.view.setSafeOnClickListener
import com.example.bettinalogistics.R
import com.example.bettinalogistics.databinding.FragmentPersonBinding
import com.example.bettinalogistics.di.AppData
import com.example.bettinalogistics.ui.fragment.bottom_sheet.ConnectCardBottomSheet
import com.example.bettinalogistics.ui.fragment.bottom_sheet.CustomerCompanyInfoBottomSheet
import com.example.bettinalogistics.utils.AppConstant.Companion.TAG
import com.google.firebase.auth.FirebaseAuth
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class UserPersonFragment : BaseFragment() {
    override val viewModel: UserPersonViewModel by sharedViewModel()

    override val binding: FragmentPersonBinding by lazy {
        FragmentPersonBinding.inflate(layoutInflater)
    }

    override fun initView() {
        hiddenLoading()
        viewModel.getCompany()
    }

    override fun initListener() {
        binding.rlEditInfoUser.setSafeOnClickListener {
            startActivity(Intent(requireContext(), EditUserAccountActivity::class.java))
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
            showLoading()
            viewModel.getCard()
        }
        binding.rlUserLogout.setSafeOnClickListener {
            FirebaseAuth.getInstance().signOut()
            AppData.g().logout()
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
        viewModel.getCardLiveData.observe(this) {
            hiddenLoading()
            if (it == null) {
                confirm.newBuild().setNotice(R.string.st_dont_connect_card).addButtonAgree {
                    val connectCard = ConnectCardBottomSheet()
                    connectCard.onConfirmListener = { card ->
                        showLoading()
                        viewModel.addCard(card)
                    }
                    connectCard.show(requireActivity().supportFragmentManager, "aaaaaaa")
                }
            } else {
                val connectCard = ConnectCardBottomSheet()
                connectCard.card = it
                connectCard.onConfirmListener = { card ->
                    card.id = it.id
                    card.user = AppData.g().currentUser
                    showLoading()
                    viewModel.updateCard(card)
                }
                connectCard.show(requireActivity().supportFragmentManager, "aaaaaaa")
            }
        }
        viewModel.addCardLiveData.observe(this) {
            hiddenLoading()
            if (it) {
                confirm.newBuild().setNotice(getString(R.string.str_connect_card_succes))
            } else {
                confirm.newBuild().setNotice(getString(R.string.str_connect_card_fail))
            }
        }
        viewModel.updateCardLiveData.observe(this) {
            hiddenLoading()
            if (it) {
                confirm.newBuild().setNotice(getString(R.string.str_edit_user_success))
            } else {
                confirm.newBuild().setNotice(getString(R.string.str_edit_user_fail))
            }
        }
    }
}