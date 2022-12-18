package com.example.bettinalogistics.ui.fragment.ship.order_list

import android.content.ContentValues
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
import com.example.baseapp.view.setSafeOnClickListener
import com.example.bettinalogistics.R
import com.example.bettinalogistics.databinding.FragmentShipOrderListBinding
import com.example.bettinalogistics.model.CommonEntity
import com.example.bettinalogistics.model.Notification
import com.example.bettinalogistics.model.Order
import com.example.bettinalogistics.model.OttRequest
import com.example.bettinalogistics.ui.fragment.admin.order.AdminListOrderAdapter
import com.example.bettinalogistics.ui.fragment.bottom_sheet.DetailConfirmOrderBottomSheet
import com.example.bettinalogistics.utils.AppConstant
import com.example.bettinalogistics.utils.AppConstant.Companion.TAG
import com.example.bettinalogistics.utils.DataConstant
import com.example.bettinalogistics.utils.Utils
import com.example.bettinalogistics.utils.Utils_Date
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.*

class ShipOrderListFragment : BaseFragment() {
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

    override val viewModel: ShipOrderViewModel by sharedViewModel()

    override val binding: FragmentShipOrderListBinding by lazy {
        FragmentShipOrderListBinding.inflate(layoutInflater)
    }

    override fun initView() {
        binding.layoutAdminOrderListkHeader.ivHeaderBack.visibility = View.GONE
        binding.layoutAdminOrderListkHeader.tvHeaderTitle.text = getString(R.string.str_tracking)
        binding.rvShipOrderList.adapter = orderListAdapter
        showLoading()
        viewModel.getOttTokenList()
        viewModel.getListOrderByStatus(DataConstant.ORDER_STATUS_CONFIRM)
    }

    override fun initListener() {
        binding.tvShipTrackingTabConfirmed.setSafeOnClickListener {
            showLoading()
            viewModel.getListOrderByStatus(DataConstant.ORDER_STATUS_CONFIRM)
            viewModel.tabSelected = DataConstant.ORDER_STATUS_CONFIRM
            binding.viewTrackingTabConfirmed.isVisible = true
            binding.viewTrackingTabDelivered.isVisible = false
            binding.viewTrackingTabDelivering.isVisible = false
        }

        binding.tvShipTrackingTabDelivering.setSafeOnClickListener {
            showLoading()
            viewModel.getListOrderByStatus(DataConstant.ORDER_STATUS_DELIVERING)
            viewModel.tabSelected = DataConstant.ORDER_STATUS_DELIVERING
            binding.viewTrackingTabConfirmed.isVisible = false
            binding.viewTrackingTabDelivered.isVisible = false
            binding.viewTrackingTabDelivering.isVisible = true
        }
        binding.tvShipTrackingTabDelivered.setSafeOnClickListener {
            showLoading()
            viewModel.getListOrderByStatus(DataConstant.ORDER_STATUS_DELIVERED)
            viewModel.tabSelected = DataConstant.ORDER_STATUS_DELIVERED
            binding.viewTrackingTabConfirmed.isVisible = false
            binding.viewTrackingTabDelivered.isVisible = true
            binding.viewTrackingTabDelivering.isVisible = false
        }

        orderListAdapter.onItemClickListener = {
            if (viewModel.tabSelected == DataConstant.ORDER_STATUS_CONFIRM) {
                val detailConfirmOrderBottomSheet = DetailConfirmOrderBottomSheet()
                detailConfirmOrderBottomSheet.order = it
                detailConfirmOrderBottomSheet.onConfirmDelivering = {
                    showLoading()
                    it.statusOrder =  DataConstant.ORDER_STATUS_DELIVERING
                    viewModel.updateOrderToDelivering(it)
                }
                detailConfirmOrderBottomSheet.show(requireActivity().supportFragmentManager, "s")
            } else {
                launcher.launch(
                    ShipUnitListTrackOrderActivity.startDetailOrderActivity(
                        requireContext(),
                        it
                    )
                )
            }
        }
        binding.edtShipOrderListSearch.onTextChange = {
            orderListAdapter.filter.filter(it)
        }
        orderListAdapter.onSearchResult = {
            binding.tvResultSearchTitle.isVisible =
                it != 0 && binding.edtShipOrderListSearch.getContentText().isNotEmpty()
            binding.tvResultSearch.isVisible =
                it != 0 && binding.edtShipOrderListSearch.getContentText().isNotEmpty()
            binding.tvResultSearch.text =
                getString(R.string.str_result_search, UtilsBase.g().getBeautyNumber(it))
            if (it == 0) {
                binding.rvShipOrderList.isVisible = false
                binding.emptyOrderTracking.root.isVisible = true
            } else {
                binding.rvShipOrderList.isVisible = true
                binding.emptyOrderTracking.root.isVisible = false
            }
        }
    }

    override fun observerData() {
        viewModel.getOttTokenListLiveData.observe(this) {
            hiddenLoading()
            if (it.isNullOrEmpty()) {
                Log.e(ContentValues.TAG, "observeData:tokenlist roongx ")
            } else {
                Log.d(TAG, "observerData: ship order list $it")
                viewModel.allTokenList.addAll(it)
            }
        }
        viewModel.getListOrderByStatusLiveData.observe(this) {
            hiddenLoading()
            if (it.isNullOrEmpty()) {
                binding.emptyOrderTracking.root.isVisible = true
                binding.rvShipOrderList.isVisible = false
            } else {
                binding.emptyOrderTracking.root.isVisible = false
                binding.rvShipOrderList.isVisible = true
                orderListAdapter.setData(convertToListData(it))
            }
        }
        viewModel.updateOrderToDeliveringLiveData.observe(this) {
            hiddenLoading()
            if (it) {
                showLoading()
                val notification = Notification(
                    contentNoti = getString(
                        R.string.str_track_notification_update_delivering,
                        viewModel.order?.orderCode ?: ""
                    ),
                    notificationType = getString(R.string.str_update_track_order),
                    notiTo = "admin and user",
                    confirmDate = Utils_Date.convertformDate(
                        Date(),
                        Utils_Date.DATE_PATTERN_DD_MM_YYYY_HH_MM_SS
                    ),
                    order = viewModel.order
                )
                viewModel.sendNotiRequestFirebase(notification)
                val content = getString(
                    R.string.str_track_notification_update_delivering,
                    viewModel.order?.orderCode ?: ""
                )
                val listToken = mutableListOf<String>()
                viewModel.allTokenList.forEach {
                    if (it.role?.trim() == "admin".trim() || it.role?.trim() == "user".trim()) {
                        it.token?.let { it1 -> listToken.add(it1) }
                    }
                }
                val request = OttRequest(
                    requestId = viewModel.order?.orderCode,
                    serialNumbers = listToken,
                    content = content,
                    title = getString(R.string.str_update_track_order),
                    notificationType = "142341234"
                )
                viewModel.sendOttServer(request)
            } else {
                confirm.newBuild().setNotice(getString(R.string.str_edit_user_fail))
            }
            viewModel.sendNotiRequestFirebaseLiveData.observe(this) {
                hiddenLoading()
                if (it) {
                    confirm.setNotice(getString(R.string.str_update_track_order_success))
                        .addButtonAgree {
                            showLoading()
                            viewModel.getListOrderByStatus(DataConstant.ORDER_STATUS_DELIVERING)
                            viewModel.tabSelected = DataConstant.ORDER_STATUS_DELIVERING
                            binding.viewTrackingTabConfirmed.isVisible = false
                            binding.viewTrackingTabDelivered.isVisible = false
                            binding.viewTrackingTabDelivering.isVisible = true
                        }
                } else {
                    confirm.newBuild().setNotice(getString(R.string.str_update_track_order_failed))
                }
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