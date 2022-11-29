package com.example.bettinalogistics.ui.fragment.admin.order

import android.view.View
import androidx.core.view.isVisible
import com.example.baseapp.BaseFragment
import com.example.bettinalogistics.R
import com.example.bettinalogistics.databinding.FragmentAdminOrderListBinding
import com.example.bettinalogistics.utils.DataConstant
import org.koin.android.viewmodel.ext.android.sharedViewModel

class AdminOrderListFragment : BaseFragment() {
    private val adminTabOrderListAdapter: AdminTabOrderListAdapter by lazy { AdminTabOrderListAdapter() }

    override val viewModel: AdminOrderListViewModel by sharedViewModel()

    override val binding: FragmentAdminOrderListBinding by lazy {
        FragmentAdminOrderListBinding.inflate(layoutInflater)
    }

    override fun initView() {
        binding.adminOrderListHeader.ivHeaderBack.visibility = View.GONE
        binding.adminOrderListHeader.tvHeaderTitle.text = getString(R.string.str_order_list)
        binding.rvAdminTabOrderList.adapter = adminTabOrderListAdapter
        val listTab = viewModel.getAdminOrderListTab()
        adminTabOrderListAdapter.reset(listTab)
        viewModel.getListOrderByStatus(DataConstant.ORDER_STATUS_PENDING)
    }

    override fun initListener() {
        adminTabOrderListAdapter.onItemClickListener = {
            viewModel.getListOrderByStatus(it.title)
        }
    }

    override fun observerData() {
        viewModel.getListOrderByStatusLiveData.observe(this) {
            if (it.isNullOrEmpty()) {
                binding.emptyAdminListOrder.root.isVisible = true
                binding.rvAdminOrderList.isVisible = false
            } else {
                binding.emptyAdminListOrder.root.isVisible = false
                binding.rvAdminOrderList.isVisible = true
                adminTabOrderListAdapter.reset(it)
            }
        }
    }

}