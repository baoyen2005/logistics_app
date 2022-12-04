package com.example.bettinalogistics.ui.fragment.user.person

import com.example.baseapp.BaseFragment
import com.example.baseapp.view.setSafeOnClickListener
import com.example.bettinalogistics.databinding.FragmentPersonBinding
import com.example.bettinalogistics.ui.fragment.user.notification.UserNotificationViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class UserPersonFragment : BaseFragment() {
    override val viewModel: UserNotificationViewModel by sharedViewModel()

    override val binding: FragmentPersonBinding by lazy {
        FragmentPersonBinding.inflate(layoutInflater)
    }

    override fun initView() {

    }

    override fun initListener() {

    }

    override fun observerData() {
        binding.rlEditInfoUser.setSafeOnClickListener {

        }
    }
}