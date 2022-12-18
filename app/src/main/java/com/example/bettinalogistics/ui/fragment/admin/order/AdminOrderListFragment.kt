package com.example.bettinalogistics.ui.fragment.admin.order

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
import com.example.bettinalogistics.databinding.FragmentAdminOrderListBinding
import com.example.bettinalogistics.model.CommonEntity
import com.example.bettinalogistics.model.Order
import com.example.bettinalogistics.ui.fragment.admin.detail_order.AdminDetailOrderActivity
import com.example.bettinalogistics.utils.AppConstant
import com.example.bettinalogistics.utils.DataConstant
import com.example.bettinalogistics.utils.Utils
import com.example.bettinalogistics.utils.Utils_Date
import org.koin.android.viewmodel.ext.android.sharedViewModel

class AdminOrderListFragment : BaseFragment() {
    private val adminTabOrderListAdapter: AdminTabOrderListAdapter by lazy { AdminTabOrderListAdapter() }
    private val orderListAdapter: AdminListOrderAdapter by lazy { AdminListOrderAdapter() }
    override val viewModel: AdminOrderListViewModel by sharedViewModel()

    override val binding: FragmentAdminOrderListBinding by lazy {
        FragmentAdminOrderListBinding.inflate(layoutInflater)
    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == AppCompatActivity.RESULT_OK
                && result.data != null
            ) {
                showLoading()
                viewModel.getAdminOrderListTab()
            } else {
                Log.d(AppConstant.TAG, "result = null: ")
            }
        }


    override fun initView() {
        binding.adminOrderListHeader.ivHeaderBack.visibility = View.GONE
        binding.adminOrderListHeader.tvHeaderTitle.text = getString(R.string.str_order_list)
        binding.rvAdminTabOrderList.adapter = adminTabOrderListAdapter
        val listTab = viewModel.getAdminOrderListTab()
        adminTabOrderListAdapter.reset(listTab)
        binding.rvAdminOrderList.adapter = orderListAdapter
        viewModel.getListOrderByStatus(DataConstant.ORDER_STATUS_PENDING)
    }

    override fun initListener() {
        adminTabOrderListAdapter.onItemClickListener = {
            viewModel.tabSelected = it.title ?: ""
            viewModel.getListOrderByStatus(it.title)
        }
        orderListAdapter.onItemClickListener = {
            launcher.launch(AdminDetailOrderActivity.startDetailOrderActivity(requireContext(), it))
        }
        binding.edtAdminOrderListSearch.onTextChange = {
            orderListAdapter.filter.filter(it)
        }
        orderListAdapter.onSearchResult = {
            binding.tvResultSearchTitle.isVisible =
                it != 0 && binding.edtAdminOrderListSearch.getContentText().isNotEmpty()
            binding.tvResultSearch.isVisible =
                it != 0 && binding.edtAdminOrderListSearch.getContentText().isNotEmpty()
            binding.tvResultSearch.text =
                getString(R.string.str_result_search, UtilsBase.g().getBeautyNumber(it))
            if (it == 0) {
                binding.rvAdminOrderList.isVisible = false
                binding.emptyAdminListOrder.root.isVisible = true
            } else {
                binding.rvAdminOrderList.isVisible = true
                binding.emptyAdminListOrder.root.isVisible = false
            }
        }
    }

    override fun observerData() {
        viewModel.getListOrderByStatusLiveData.observe(this) {
            hiddenLoading()
            if (it.isNullOrEmpty()) {
                binding.emptyAdminListOrder.root.isVisible = true
                binding.rvAdminOrderList.isVisible = false
            } else {
                binding.emptyAdminListOrder.root.isVisible = false
                binding.rvAdminOrderList.isVisible = true
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
        viewModel.getListOrderByStatus(viewModel.tabSelected)
    }
}