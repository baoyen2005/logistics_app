package com.example.bettinalogistics.ui.activity.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.bettinalogistics.ui.fragment.user.followtrask.UserFollowTrackingFragment
import com.example.bettinalogistics.ui.fragment.user.home.UserHomeFragment
import com.example.bettinalogistics.ui.fragment.user.notification.NotificationFragment
import com.example.bettinalogistics.ui.fragment.user.person.UserPersonFragment

class MainViewPagerAdapter (fragmentActivity: FragmentActivity, private var totalCount: Int) :
    FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return totalCount
    }
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> UserHomeFragment()
            1 -> UserFollowTrackingFragment()
            2 -> NotificationFragment()
            else -> UserPersonFragment()
        }
    }
}
