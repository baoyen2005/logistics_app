package com.example.bettinalogistics.ui.main

import android.os.Bundle
import com.example.baseapp.BaseActivity
import com.example.bettinalogistics.R
import com.example.bettinalogistics.databinding.ActivityMainBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity() {
    override val layoutId: Int
        get() = R.layout.activity_main

    override val viewModel: MainViewModel by viewModel()

    override val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViewPager()
        initView()
        initListener()
    }

    private fun initView() {
        binding.viewPagerMain.currentItem = 0
        binding.ivMainHome.setImageResource(R.drawable.ic_baseline_home_24_clicked)
        binding.ivMainFollowTrack.setImageResource(R.drawable.ic_baseline_source_24)
        binding.ivMainNotification.setImageResource(R.drawable.ic_baseline_notifications_24)
        binding.ivMainPerson.setImageResource(R.drawable.ic_baseline_person_24)
    }

    private fun initListener() {
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
    }

    private fun setupViewPager() {
        val adapter = MainViewPagerAdapter(this, 4)
        binding.viewPagerMain.adapter = adapter
        when(binding.viewPagerMain.currentItem){
            0 -> {
                setFirstFragmentItem()
            }
            1-> {
                setSecondFragmentItem()
            }
            2 -> {
                setThirdFragmentItem()
            }
            3-> {
                setFourthFragmentItem()
            }
        }
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
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed()
        } else {
            // Otherwise, select the previous step.
            viewPager.currentItem = viewPager.currentItem - 1
        }
    }

}