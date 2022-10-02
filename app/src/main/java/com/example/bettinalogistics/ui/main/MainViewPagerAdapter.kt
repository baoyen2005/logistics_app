package com.example.bettinalogistics.ui.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.bettinalogistics.ui.followtrask.FollowTraskFragment
import com.example.bettinalogistics.ui.home.HomeFragment
import com.example.bettinalogistics.ui.notification.NotificationFragment
import com.example.bettinalogistics.ui.person.PersonFragment

class MainViewPagerAdapter (fragmentActivity: FragmentActivity, private var totalCount: Int) :
    FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return totalCount
    }
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HomeFragment()
            1 -> FollowTraskFragment()
            2 -> NotificationFragment()
            else -> PersonFragment()
        }
    }
}
