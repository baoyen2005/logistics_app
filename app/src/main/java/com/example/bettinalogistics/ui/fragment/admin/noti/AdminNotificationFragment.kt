package com.example.bettinalogistics.ui.fragment.admin.noti

import android.text.format.DateUtils
import androidx.core.view.isVisible
import com.example.baseapp.BaseFragment
import com.example.baseapp.view.getTimeInMillisecond
import com.example.bettinalogistics.R
import com.example.bettinalogistics.databinding.FragmentAdminNotificationBinding
import com.example.bettinalogistics.model.CommonEntity
import com.example.bettinalogistics.model.Notification
import com.example.bettinalogistics.ui.fragment.user.notification.NotificationAdapter
import com.example.bettinalogistics.ui.fragment.user.notification.NotificationViewModel
import com.example.bettinalogistics.utils.Utils
import com.example.bettinalogistics.utils.Utils_Date
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class AdminNotificationFragment : BaseFragment() {
    private val adminNotificationAdapter : NotificationAdapter by lazy { NotificationAdapter() }
    override val viewModel: NotificationViewModel by sharedViewModel()

    override val binding: FragmentAdminNotificationBinding by lazy {
        FragmentAdminNotificationBinding.inflate(layoutInflater)
    }

    override fun initView() {
        showLoading()
        viewModel.getAllNotification("admin")
        binding.emptyAdminNotification.tvEmptyLayoutTitle.text = getString(R.string.str_emtpy_notification)
        binding.rvAdminNotification.adapter = adminNotificationAdapter
        binding.layoutAdminNotificationHeader.ivHeaderBack.isVisible =false
        binding.layoutAdminNotificationHeader.tvHeaderTitle.text = getString(R.string.str_noti)
    }

    override fun initListener() {

    }

    override fun observerData() {
        viewModel.getAllNotificationLiveData.observe(this){
            if(it.isNullOrEmpty()){
                binding.rvAdminNotification.isVisible = false
                binding.emptyAdminNotification.root.isVisible = true
            }
            else{
                binding.rvAdminNotification.isVisible = true
                binding.emptyAdminNotification.root.isVisible = false
                adminNotificationAdapter.reset(convertToListData(it))
            }
        }
    }

    private fun convertToListData(listEntity: List<Notification>): List<Any> {
        val list = mutableListOf<Any>()
        val listDate = listEntity.map {
            it.confirmDate?.let { date ->
                Utils.g()
                    .convertDate(
                        Utils_Date.DATE_PATTERN_DD_MM_YYYY_HH_MM_SS,
                        Utils_Date.DATE_PATTERN_ddMMYYYY,
                        date
                    )
            }
        }.toSet()
        listDate.forEach { stringDate ->
            val longDate = stringDate.getTimeInMillisecond(Utils_Date.DATE_PATTERN_ddMMYYYY)
            val listTranInThisDay =
                listEntity.filter { it.confirmDate?.contains(stringDate.toString()) == true }
            list.add(
                CommonEntity(
                    when {
                        DateUtils.isToday(longDate) -> getString(R.string.today_date, stringDate)
                        DateUtils.isToday(longDate + DateUtils.DAY_IN_MILLIS) -> getString(
                            R.string.yesterday_date,
                            stringDate
                        )
                        else -> stringDate.toString()
                    },
                    getString(R.string.number_of_transaction, listTranInThisDay.size.toString())
                )
            )
            list.addAll(listTranInThisDay)
        }
        return list
    }
}