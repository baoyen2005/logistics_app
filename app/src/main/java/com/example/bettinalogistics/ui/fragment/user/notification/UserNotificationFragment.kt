package com.example.bettinalogistics.ui.fragment.user.notification

import android.text.format.DateUtils
import androidx.core.view.isVisible
import com.example.baseapp.BaseFragment
import com.example.baseapp.view.getTimeInMillisecond
import com.example.bettinalogistics.R
import com.example.bettinalogistics.databinding.FragmentNotificationBinding
import com.example.bettinalogistics.enums.NotiToDataEnum
import com.example.bettinalogistics.model.CommonEntity
import com.example.bettinalogistics.model.Notification
import com.example.bettinalogistics.utils.Utils
import com.example.bettinalogistics.utils.Utils_Date
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class NotificationFragment : BaseFragment() {
    private val adminNotificationAdapter : NotificationAdapter by lazy { NotificationAdapter() }
    override val viewModel: NotificationViewModel by sharedViewModel()

    override val binding: FragmentNotificationBinding by lazy {
        FragmentNotificationBinding.inflate(layoutInflater)
    }

    override fun initView() {
        showLoading()
        viewModel.getAllNotification(NotiToDataEnum.USER.notiTo)
        binding.emptyNotification.tvEmptyLayoutTitle.text = getString(R.string.str_emtpy_notification)
        binding.rvNotification.adapter = adminNotificationAdapter
        binding.layoutNotificationHeader.ivHeaderBack.isVisible = false
        binding.layoutNotificationHeader.tvHeaderTitle.text = getString(R.string.str_noti)
    }

    override fun initListener() {

    }

    override fun observerData() {
        viewModel.getAllNotificationLiveData.observe(this){
            if(it.isNullOrEmpty()){
                binding.rvNotification.isVisible = false
                binding.emptyNotification.root.isVisible = true
            }
            else{
                binding.rvNotification.isVisible = true
                binding.emptyNotification.root.isVisible = false
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

    override fun onResume() {
        super.onResume()
        showLoading()
        viewModel.getAllNotification(NotiToDataEnum.USER.notiTo)
    }
}