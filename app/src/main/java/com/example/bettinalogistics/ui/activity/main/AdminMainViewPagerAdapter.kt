package com.example.bettinalogistics.ui.activity.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.bettinalogistics.ui.fragment.admin.noti.AdminNotificationFragment
import com.example.bettinalogistics.ui.fragment.admin.order.AdminOrderListFragment
import com.example.bettinalogistics.ui.fragment.admin.person.AdminAccountFragment
import com.example.bettinalogistics.ui.fragment.user.home.UserHomeFragment

class AdminMainViewPagerAdapter (fragmentActivity: FragmentActivity, private var totalCount: Int) :
    FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return totalCount
    }
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> UserHomeFragment()
            1 -> AdminOrderListFragment()
            2 -> AdminNotificationFragment()
            else -> AdminAccountFragment()
        }
    }
}
