package com.example.bettinalogistics.ui.fragment.ship.person

import android.content.Intent
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.example.baseapp.BaseFragment
import com.example.baseapp.view.setSafeOnClickListener
import com.example.bettinalogistics.R
import com.example.bettinalogistics.databinding.FragmentShipAccountBinding
import com.example.bettinalogistics.di.AppData
import com.example.bettinalogistics.ui.fragment.bottom_sheet.ConfirmBottomSheetFragment
import com.example.bettinalogistics.ui.fragment.bottom_sheet.ConnectCardBottomSheet
import com.example.bettinalogistics.ui.fragment.user.person.EditUserAccountActivity
import com.example.bettinalogistics.ui.fragment.user.person.EditUserAccountActivity.Companion.IS_EDIT_ACCOUNT
import com.google.firebase.auth.FirebaseAuth
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class ShipAccountFragment : BaseFragment() {
    override val viewModel: ShipPersonViewModel by sharedViewModel()

    override val binding: FragmentShipAccountBinding by lazy {
        FragmentShipAccountBinding.inflate(layoutInflater)
    }

    override fun initView() {
        val user = AppData.g().currentUser
        Glide.with(this).load(user?.image).into(binding.ivUserPersonAvt)
        binding.tvUserPersonName.text = user?.fullName
        binding.tvUserPersonEmail.text = user?.email
        binding.headerUserPerson.tvHeaderTitle.text = getString(R.string.str_account)
        binding.headerUserPerson.ivHeaderBack.isVisible = false
    }

    override fun initListener() {
        binding.rlViewInfoUser.setSafeOnClickListener {
            val intent = Intent(requireContext(), EditUserAccountActivity::class.java)
            intent.putExtra(IS_EDIT_ACCOUNT, false)
            startActivity(intent)
        }
        binding.rlConnectCard.setSafeOnClickListener {
            showLoading()
            viewModel.getCard()
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