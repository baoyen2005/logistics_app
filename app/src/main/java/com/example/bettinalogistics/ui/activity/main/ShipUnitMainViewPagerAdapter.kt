package com.example.bettinalogistics.ui.activity.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.bettinalogistics.ui.fragment.ship.noti.ShipNotificationFragment
import com.example.bettinalogistics.ui.fragment.ship.order_list.ShipOrderListFragment
import com.example.bettinalogistics.ui.fragment.ship.person.ShipAccountFragment
import com.example.bettinalogistics.ui.fragment.user.followtrask.UserFollowTrackingFragment
import com.example.bettinalogistics.ui.fragment.user.home.UserHomeFragment
import com.example.bettinalogistics.ui.fragment.user.notification.NotificationFragment
import com.example.bettinalogistics.ui.fragment.user.person.PersonFragment

class ShipUnitMainViewPagerAdapter (fragmentActivity: FragmentActivity, private var totalCount: Int) :
    FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return totalCount
    }
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> UserHomeFragment()
            1 -> ShipOrderListFragment()
            2 -> ShipNotificationFragment()
            else -> ShipAccountFragment()
        }
    }
}
