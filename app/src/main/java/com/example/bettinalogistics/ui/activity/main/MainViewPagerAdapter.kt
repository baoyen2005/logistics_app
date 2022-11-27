package com.example.bettinalogistics.ui.activity.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.bettinalogistics.ui.fragment.followtrask.FollowTrackingFragment
import com.example.bettinalogistics.ui.fragment.home.HomeFragment
import com.example.bettinalogistics.ui.fragment.notification.NotificationFragment
import com.example.bettinalogistics.ui.fragment.person.PersonFragment

class MainViewPagerAdapter (fragmentActivity: FragmentActivity, private var totalCount: Int) :
    FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return totalCount
    }
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HomeFragment()
            1 -> FollowTrackingFragment()
            2 -> NotificationFragment()
            else -> PersonFragment()
        }
    }
}
