package com.example.bettinalogistics.ui.activity.main

import android.content.Intent
import android.util.Log
import androidx.core.view.isVisible
import com.example.baseapp.BaseActivity
import com.example.bettinalogistics.R
import com.example.bettinalogistics.databinding.ActivityMainBinding
import com.example.bettinalogistics.di.AppData
import com.example.bettinalogistics.ui.activity.addorder.OrderActivity
import com.example.bettinalogistics.utils.AppConstant.Companion.TAG
import com.example.bettinalogistics.utils.DataConstant.Companion.USER_EMAIL
import com.example.bettinalogistics.utils.Utils
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity() {
    companion object{
        const val CHECK_FORGOT_PASSWORD  = "check_forgot_password"
        const val CHANGED_PASSWORD  = "changed_password"
    }
    override val viewModel: MainViewModel by viewModel()

    override val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun initView() {
        binding.viewPagerMain.isUserInputEnabled = false
        viewModel.changedPassword = intent.getStringExtra(CHANGED_PASSWORD)
        val checkForgotPassword = intent.getBooleanExtra(CHECK_FORGOT_PASSWORD,false)
        val email = intent.getStringExtra(USER_EMAIL)
        viewModel.email = email
        if(AppData.g().currentUser == null){
            if(checkForgotPassword){
                showLoading()
                viewModel.changedPassword?.let { viewModel.updatePassword(password = it) }
            }
            else{
                if (email != null) {
                    showLoading()
                    viewModel.getUser(email)
                }
                else{
                    showLoading()
                    viewModel.getUser(Utils.g().getDataString(USER_EMAIL).toString())
                }
            }
        }
      else{
            viewModel.role = AppData.g().currentUser?.role
            setupViewPager()
            binding.viewPagerMain.currentItem = 0
            viewModel.saveTokenByUser()
            binding.ivMainHome.setImageResource(R.drawable.ic_baseline_home_24_clicked)
            binding.ivMainFollowTrack.setImageResource(R.drawable.ic_baseline_source_24)
            binding.ivMainNotification.setImageResource(R.drawable.ic_baseline_notifications_24)
            binding.ivMainPerson.setImageResource(R.drawable.ic_baseline_person_24)
        }
    }

    override fun initListener() {
        binding.ivMainHome.setOnClickListener {
           setFirstFragmentItem()
        }
        binding.ivMainFollowTrack.setOnClickListener {
            setSecondFragmentItem()
        }
        binding.ivMainNotification.setOnClickListener {
            setThirdFragmentItem()
        }
        binding.ivMainPerson.setOnClickListener {
           setFourthFragmentItem()
        }
        binding.btnFloatTingMainAdd.setOnClickListener {
            startActivity(Intent(this, OrderActivity::class.java))
        }
    }

    override fun observeData() {
        viewModel.updatePasswordLiveData.observe(this){
            if(it){
                viewModel.email?.let { viewModel.getUser(it) }
            }
        }
        viewModel.getUserLiveData.observe(this) {
            if (it != null) {
                hiddenLoading()
                AppData.g().saveUser(it)
                AppData.g().currentUserAuth = Firebase.auth.currentUser.toString()
                setupViewPager()
                viewModel.saveTokenByUser()
                binding.viewPagerMain.currentItem = 0
                binding.ivMainHome.setImageResource(R.drawable.ic_baseline_home_24_clicked)
                binding.ivMainFollowTrack.setImageResource(R.drawable.ic_baseline_source_24)
                binding.ivMainNotification.setImageResource(R.drawable.ic_baseline_notifications_24)
                binding.ivMainPerson.setImageResource(R.drawable.ic_baseline_person_24)
            } else {
                confirm.newBuild().setNotice(R.string.HANDLE_ERROR).addButtonAgree{
                    hiddenLoading()
                    confirm.dismiss()
                }
            }
        }
        viewModel.saveTokenByUserLiveData.observe(this){
            Log.d(TAG, "saveTokenByUserLiveData:  $it")
        }
    }

    private fun setupViewPager() {
        when (viewModel.role) {
            "user" -> {
                val adapter = MainViewPagerAdapter(this, 4)
                binding.viewPagerMain.adapter = adapter
                binding.btnFloatTingMainAdd.isVisible = true
                binding.ivMainNotification.isVisible = true
            }
            "ship" -> {
                val adapter = ShipUnitMainViewPagerAdapter(this, 3)
                binding.viewPagerMain.adapter = adapter
                binding.btnFloatTingMainAdd.isVisible = false
                binding.ivMainNotification.isVisible = false
            }
            else -> {
                val adapter = AdminMainViewPagerAdapter(this, 4)
                binding.viewPagerMain.adapter = adapter
                binding.btnFloatTingMainAdd.isVisible = false
                binding.ivMainNotification.isVisible = true
            }
        }
        /*
        binding.viewPagerMain.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> {
                        setFirstFragmentItem()
                    }
                    1 -> {
                        setSecondFragmentItem()
                    }
                    2 -> {
                        setThirdFragmentItem()
                    }
                    else -> {
                        setFourthFragmentItem()
                    }
                }
                super.onPageSelected(position)
            }
        })

         */
//        when(binding.viewPagerMain.currentItem){
//            0 -> {
//                setFirstFragmentItem()
//            }
//            1-> {
//                setSecondFragmentItem()
//            }
//            2 -> {
//                setThirdFragmentItem()
//            }
//            3-> {
//                setFourthFragmentItem()
//            }
 //       }
    }

    private fun setFirstFragmentItem() {
        binding.viewPagerMain.currentItem = 0
        binding.ivMainHome.setImageResource(R.drawable.ic_baseline_home_24_clicked)
        binding.ivMainFollowTrack.setImageResource(R.drawable.ic_baseline_source_24)
        binding.ivMainNotification.setImageResource(R.drawable.ic_baseline_notifications_24)
        binding.ivMainPerson.setImageResource(R.drawable.ic_baseline_person_24)
    }

    private fun setThirdFragmentItem(){
        binding.viewPagerMain.currentItem = 2
        binding.ivMainNotification.setImageResource(R.drawable.ic_baseline_notifications_24_clicked)
        binding.ivMainHome.setImageResource(R.drawable.ic_baseline_home_24)
        binding.ivMainFollowTrack.setImageResource(R.drawable.ic_baseline_source_24)
        binding.ivMainPerson.setImageResource(R.drawable.ic_baseline_person_24)
    }

    private fun setSecondFragmentItem(){
        binding.viewPagerMain.currentItem = 1
        binding.ivMainFollowTrack.setImageResource(R.drawable.ic_baseline_source_24_clicked)
        binding.ivMainHome.setImageResource(R.drawable.ic_baseline_home_24)
        binding.ivMainNotification.setImageResource(R.drawable.ic_baseline_notifications_24)
        binding.ivMainPerson.setImageResource(R.drawable.ic_baseline_person_24)
    }

    private fun setFourthFragmentItem(){
        binding.viewPagerMain.currentItem = 3
        binding.ivMainPerson.setImageResource(R.drawable.ic_baseline_person_24_clicked)
        binding.ivMainHome.setImageResource(R.drawable.ic_baseline_home_24)
        binding.ivMainFollowTrack.setImageResource(R.drawable.ic_baseline_source_24)
        binding.ivMainNotification.setImageResource(R.drawable.ic_baseline_notifications_24)
    }

    override fun onBackPressed() {
        val viewPager = binding.viewPagerMain
        if (viewPager.currentItem == 0) {
            super.onBackPressed()
        } else {
            // Otherwise, select the previous step.
            viewPager.currentItem = viewPager.currentItem - 1
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.viewPagerMain.adapter = null
    }
}