package com.example.bettinalogistics.ui.home

import android.content.Intent
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.baseapp.BaseFragment
import com.example.bettinalogistics.R
import com.example.bettinalogistics.databinding.FragmentHomeBinding
import com.example.bettinalogistics.di.AppData
import com.example.bettinalogistics.ui.signup.SignUpActivity
import com.example.bettinalogistics.utils.dateToString
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*


class HomeFragment : BaseFragment() {
    private lateinit var homeTransportMethodAdapter: HomeTransportMethodAdapter
    private lateinit var homeTransportTypeAdapter: HomeTransportTypeAdapter

    override val layoutId: Int
        get() = R.layout.fragment_home

    override val viewModel: HomeViewModel by viewModel()

    override val binding: FragmentHomeBinding by lazy {
        FragmentHomeBinding.inflate(layoutInflater)
    }

    override fun initView() {
        checkLogin()
        setAdapter()
        val slideModel = viewModel.getSliderModels(requireContext())
        binding.sliderHome.setImageList(slideModel)
    }

    override fun initListener() {
        binding.tvHomeSignUp.setOnClickListener {
            startActivity(Intent(requireContext(), SignUpActivity::class.java))
        }
    }

    override fun observerData() {

    }

    private fun checkLogin(){
        if (AppData.g().isLogined()) {
            binding.tvHomeUserName.visibility = View.VISIBLE
            binding.tvHomeDate.visibility = View.VISIBLE
            binding.tvHomeUserName.text = AppData.g().fullName
            binding.tvHomeDate.text = dateToString(Date())
            Glide.with(this)
                .load(AppData.g().uri)
                .transform(CircleCrop())
                .into(binding.imgHomeAvatar)
            binding.tvHomeSignUp.visibility = View.GONE
            binding.imgHomeAvatar.visibility = View.VISIBLE
        } else {
            binding.tvHomeSignUp.visibility = View.VISIBLE
            binding.imgHomeAvatar.visibility = View.GONE
            binding.tvHomeUserName.visibility = View.GONE
            binding.tvHomeDate.visibility = View.GONE
        }
    }

    private fun setAdapter() {
        val transportMethodList = viewModel.getTransportMethodList(requireContext())
        val transportTypeList = viewModel.getTransportTypeList(requireContext())
        homeTransportMethodAdapter = HomeTransportMethodAdapter(transportMethodList)
        binding.rvHomeTransportMethods.adapter = homeTransportMethodAdapter
        homeTransportTypeAdapter = HomeTransportTypeAdapter(transportTypeList)
        binding.rvHomeTransportType.adapter = homeTransportTypeAdapter
    }

}