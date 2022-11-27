package com.example.bettinalogistics.ui.fragment.followtrask

import android.view.View
import com.example.baseapp.BaseFragment
import com.example.bettinalogistics.R
import com.example.bettinalogistics.databinding.FragmentFollowTrackingBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class FollowTrackingFragment : BaseFragment() {
    private val tabFollowTrackingAdapter: TabFollowTrackingAdapter by lazy { TabFollowTrackingAdapter() }
    override val viewModel: FollowTrackingViewModel by viewModel()

    override val binding: FragmentFollowTrackingBinding by lazy {
        FragmentFollowTrackingBinding.inflate(layoutInflater)
    }

    override fun initView() {
        binding.layoutFollowTrackHeader.ivHeaderBack.visibility = View.GONE
        binding.layoutFollowTrackHeader.tvHeaderTitle.text = getString(R.string.str_tracking)
        binding.rvTabTracking.adapter = tabFollowTrackingAdapter
        val listTab = viewModel.getLisTrackingTab()
        tabFollowTrackingAdapter.reset(listTab)
    }

    override fun initListener() {

    }

    override fun observerData() {

    }

}