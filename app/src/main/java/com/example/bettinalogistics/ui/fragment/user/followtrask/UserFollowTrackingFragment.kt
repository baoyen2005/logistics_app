package com.example.bettinalogistics.ui.fragment.user.followtrask

import android.text.format.DateUtils
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.baseapp.BaseFragment
import com.example.baseapp.UtilsBase
import com.example.baseapp.view.getTimeInMillisecond
import com.example.bettinalogistics.R
import com.example.bettinalogistics.databinding.FragmentFollowTrackingBinding
import com.example.bettinalogistics.model.CommonEntity
import com.example.bettinalogistics.model.Order
import com.example.bettinalogistics.ui.fragment.admin.order.AdminListOrderAdapter
import com.example.bettinalogistics.ui.fragment.user.detail_order.UserDetailOrderActivity
import com.example.bettinalogistics.utils.AppConstant
import com.example.bettinalogistics.utils.DataConstant
import com.example.bettinalogistics.utils.Utils
import com.example.bettinalogistics.utils.Utils_Date
import org.koin.androidx.viewmodel.ext.android.viewModel

class UserFollowTrackingFragment : BaseFragment() {
    private val userTabFollowTrackingAdapter: UserTabFollowTrackingAdapter by lazy { UserTabFollowTrackingAdapter() }
    private val orderListAdapter: AdminListOrderAdapter by lazy { AdminListOrderAdapter() }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == AppCompatActivity.RESULT_OK
                && result.data != null
            ) {
                showLoading()
                viewModel.getLisTrackingTab()
            } else {
                Log.d(AppConstant.TAG, "result = null: ")
            }
        }

    override val viewModel: UserFollowTrackingViewModel by viewModel()

    override val binding: FragmentFollowTrackingBinding by lazy {
        FragmentFollowTrackingBinding.inflate(layoutInflater)
    }

    override fun initView() {
        binding.layoutFollowTrackHeader.ivHeaderBack.visibility = View.GONE
        binding.layoutFollowTrackHeader.tvHeaderTitle.text = getString(R.string.str_tracking)
        binding.rvTabTracking.adapter = userTabFollowTrackingAdapter
        val listTab = viewModel.getLisTrackingTab()
        userTabFollowTrackingAdapter.reset(listTab)
        binding.rvFollowTrackOrder.adapter = orderListAdapter
        showLoading()
        viewModel.getAllOrderByStatusAndUser(DataConstant.ORDER_STATUS_PENDING)
    }

    override fun initListener() {
        userTabFollowTrackingAdapter.onItemClickListener = {
            showLoading()
            viewModel.tabSelected = it.title
            viewModel.getAllOrderByStatusAndUser(it.title)
        }
        orderListAdapter.onItemClickListener = {
            launcher.launch(UserDetailOrderActivity.startDetailOrderActivity(requireContext(), it))
        }
        binding.edtFollowTrackingSearch.onTextChange = {
            orderListAdapter.filter.filter(it)
        }
        orderListAdapter.onSearchResult = {
            binding.tvResultSearchTitle.isVisible = it != 0 && binding.edtFollowTrackingSearch.getContentText().isNotEmpty()
            binding.tvResultSearch.isVisible = it != 0 && binding.edtFollowTrackingSearch.getContentText().isNotEmpty()
            binding.tvResultSearch.text = getString(R.string.str_result_search, UtilsBase.g().getBeautyNumber(it))
            if(it == 0){
                binding.rvFollowTrackOrder.isVisible = false
                binding.emptyOrderTracking.root.isVisible = true
            }
            else{
                binding.rvFollowTrackOrder.isVisible = true
                binding.emptyOrderTracking.root.isVisible = false
            }
        }
    }

    override fun observerData() {
        viewModel.getAllOrderByStatusAndUserLiveData.observe(this) {
            hiddenLoading()
            if (it.isNullOrEmpty()) {
                binding.emptyOrderTracking.root.isVisible = true
                binding.rvFollowTrackOrder.isVisible = false
            } else {
                binding.emptyOrderTracking.root.isVisible = false
                binding.rvFollowTrackOrder.isVisible = true
                orderListAdapter.setData(convertToListData(it))
            }
        }
    }
    private fun convertToListData(listEntity: List<Order>): List<Any> {
        val list = mutableListOf<Any>()
        val listDate = listEntity.map {
            it.orderDate?.let { date ->
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
                listEntity.filter { it.orderDate?.contains(stringDate.toString()) == true }
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
        viewModel.getAllOrderByStatusAndUser(viewModel.tabSelected)
    }
}