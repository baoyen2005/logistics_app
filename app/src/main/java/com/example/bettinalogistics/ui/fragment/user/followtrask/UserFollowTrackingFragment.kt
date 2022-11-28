package com.example.bettinalogistics.ui.fragment.user.followtrask

import android.view.View
import com.example.baseapp.BaseFragment
import com.example.bettinalogistics.R
import com.example.bettinalogistics.databinding.FragmentFollowTrackingBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class UserFollowTrackingFragment : BaseFragment() {
    private val userTabFollowTrackingAdapter: UserTabFollowTrackingAdapter by lazy { UserTabFollowTrackingAdapter() }
    override val viewModel: UserFollowTrackingViewModel by viewModel()

    override val binding: FragmentFollowTrackingBinding by lazy {
        FragmentFollowTrackingBinding.inflate(layoutInflater)
    }

    override fun initView() {
        binding.layoutFollowTrackHeader.ivHeaderBack.visibility = View.GONE
        binding.layoutFollowTrackHeader.tvHeaderTitle.text = getString(R.string.str_tracking)
        binding.rvTabTracking.adapter = userTabFollowTrackingAdapter
        val listTab = viewModel.getLisTrackingTab()
        userTabFollowTrackingAdapter.reset(listTab)
    }

    override fun initListener() {

    }

    override fun observerData() {

    }

}