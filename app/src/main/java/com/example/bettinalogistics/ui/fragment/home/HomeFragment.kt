package com.example.bettinalogistics.ui.fragment.home

import android.content.Intent
import android.net.Uri
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.example.baseapp.BaseFragment
import com.example.bettinalogistics.R
import com.example.bettinalogistics.databinding.FragmentHomeBinding
import com.example.bettinalogistics.di.AppData
import com.example.bettinalogistics.ui.activity.login.LoginActivity
import com.example.bettinalogistics.utils.DataConstant.Companion.USER_FULL_NAME
import com.example.bettinalogistics.utils.DataConstant.Companion.USER_IMAGE
import com.example.bettinalogistics.utils.Utils
import com.example.bettinalogistics.utils.dateToString
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*


class HomeFragment : BaseFragment() {
    private var homeTransportMethodAdapter: HomeTransportMethodAdapter? = null
    private var homeTransportTypeAdapter: HomeTransportTypeAdapter? = null

    override val viewModel: HomeViewModel by viewModel()

    override val binding: FragmentHomeBinding by lazy {
        FragmentHomeBinding.inflate(layoutInflater)
    }

    override fun initView() {
        checkLogin()
        setAdapter()
        val slideModel = viewModel.getSliderModels(requireContext())
        binding.sliderHome.setImageList(slideModel, ScaleTypes.CENTER_CROP)
    }

    override fun initListener() {
        binding.tvHomeSignUp.setOnClickListener {
            startActivity(Intent(requireContext(), LoginActivity::class.java))
        }
        homeTransportMethodAdapter?.itemTransportMethodListener = {
           if(!AppData.g().isLogin()){
               confirm.newBuild().setNotice(getString(R.string.str_please_signup))
                   .addButtonAgree(R.string.close) {
                       confirm.dismiss()
                       startActivity(Intent(requireContext(), LoginActivity::class.java))
                   }.setAction(true)
           }
        }
    }

    override fun observerData() {

    }

    private fun checkLogin(){
        if (!AppData.g().isSignUp().isNullOrEmpty()) {
            binding.tvHomeUserName.visibility = View.VISIBLE
            binding.tvHomeDate.visibility = View.VISIBLE
            binding.tvHomeUserName.text = String.format(getString(R.string.str_hello), Utils.g().getDataString(USER_FULL_NAME))
            binding.tvHomeDate.text = dateToString(Date())
            binding.imgHomeAvatar.visibility = View.VISIBLE
            binding.tvHomeSignUp.visibility = View.GONE
            Glide.with(this)
                .load(Uri.parse(Utils.g().getDataString(USER_IMAGE)))
                .transform(CircleCrop())
                .into(binding.imgHomeAvatar)
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