package com.example.bettinalogistics.ui.fragment.ship.order_list

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.baseapp.BaseActivity
import com.example.bettinalogistics.R
import com.example.bettinalogistics.databinding.ActivityShipUnitListTrackOrderBinding
import com.example.bettinalogistics.model.Notification
import com.example.bettinalogistics.model.Order
import com.example.bettinalogistics.model.OttRequest
import com.example.bettinalogistics.ui.fragment.bottom_sheet.ConfirmBottomSheetFragment
import com.example.bettinalogistics.ui.fragment.bottom_sheet.UpdateTrackOrderBottomSheet
import com.example.bettinalogistics.utils.AppConstant.Companion.TAG
import com.example.bettinalogistics.utils.DataConstant
import com.example.bettinalogistics.utils.Utils
import com.example.bettinalogistics.utils.Utils_Date
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class ShipUnitListTrackOrderActivity : BaseActivity() {
    companion object {
        const val LIST_TRACK_ORDER = "listTrackOrder"

        fun startDetailOrderActivity(context: Context, order: Order): Intent {
            val intent = Intent(context, ShipUnitListTrackOrderActivity::class.java)
            intent.putExtra(
                LIST_TRACK_ORDER,
                Utils.g().getJsonFromObject(order)
            )
            return intent
        }
    }

    private val detailOrderAdapter: TrackListOrderAdapter by lazy { TrackListOrderAdapter() }

    override val viewModel: ShipOrderViewModel by viewModel()

    override val binding: ActivityShipUnitListTrackOrderBinding by lazy {
        ActivityShipUnitListTrackOrderBinding.inflate(layoutInflater)
    }

    override fun initView() {
        binding.rvListTrackOrder.adapter = detailOrderAdapter
        val order =
            intent.getStringExtra(LIST_TRACK_ORDER)
                ?.let { Utils.g().getObjectFromJson(it, Order::class.java) }
        viewModel.order = order
        showLoading()
        viewModel.getOttTokenList()
        viewModel.getAllOrderTrack()
        binding.tvDetailOrderCode.text = order?.orderCode ?: ""
        binding.tvDetailOrderOriginAddress.text = order?.address?.originAddress ?: ""
        binding.tvDetailOrderDestinationAddress.text = order?.address?.destinationAddress ?: ""
        binding.tvDetailStatusOrder.text = order?.statusOrder ?: ""
        binding.tvDetailOrderCustomerName.text = order?.company?.name ?:""
        binding.tvDetailOrderCustomerPhone.text = order?.user?.phone ?:""
        binding.rvListTrackOrder.adapter = detailOrderAdapter
        when (order?.statusOrder) {
            DataConstant.ORDER_STATUS_DELIVERING -> {
                binding.linearDetailTransaction.setBackgroundResource(R.drawable.shape_merchant_color_ffbe21_corner_12)
            }
            DataConstant.ORDER_STATUS_DELIVERED,
            DataConstant.ORDER_STATUS_CONFIRM -> {
            }
            else -> {
                binding.linearDetailTransaction.setBackgroundResource(R.drawable.shape_merchant_color_ea5b5b_corner_12)
            }
        }
    }

    override fun initListener() {
        binding.ivDetailTransactionBack.setOnClickListener {
            finish()
        }
        binding.btnAddNewTrack.setOnClickListener {
            val updateTrackOrderBottomSheet = UpdateTrackOrderBottomSheet()
            updateTrackOrderBottomSheet.onUpdateOrAddFinish = {
                it.orderId = viewModel.order?.id
                viewModel.currentTrack = it
                showLoading()
                viewModel.addOrderTrack(it)
            }
            updateTrackOrderBottomSheet.show(supportFragmentManager, "ssssssss")
        }
        binding.btnUpdateDelivered.setOnClickListener {
            val order = viewModel.order
            order?.statusOrder = DataConstant.ORDER_STATUS_DELIVERED
            val confirmOrderBottomSheet = ConfirmBottomSheetFragment().setTitle(getString(R.string.str_confirm_delivered))
            confirmOrderBottomSheet.setConfirmListener {
                if (order != null) {
                    showLoading()
                    viewModel.updateOrderToDelivering(order)
                }
            }
        }
    }

    override fun observeData() {
        viewModel.getOttTokenListLiveData.observe(this) {
            hiddenLoading()
            if (it.isNullOrEmpty()) {
                Log.e(ContentValues.TAG, "observeData:tokenlist roongx ")
            } else {
                Log.d(TAG, "observeData: in detail get ott token $it")
                viewModel.allTokenList.addAll(it)
            }
        }
        viewModel.getAllOrderTrackLiveData.observe(this) {
            hiddenLoading()
            if (it.isNullOrEmpty()) {
                confirm.newBuild().setNotice(getString(R.string.str_track_empty)).addButtonAgree {
                    val updateTrackOrderBottomSheet = UpdateTrackOrderBottomSheet()
                    updateTrackOrderBottomSheet.onUpdateOrAddFinish = {
                        it.orderId = viewModel.order?.id
                        viewModel.currentTrack = it
                        showLoading()
                        viewModel.addOrderTrack(it)
                    }
                    updateTrackOrderBottomSheet.show(supportFragmentManager, "ssssssss")
                }
            } else {
                detailOrderAdapter.reset(it)
            }
        }
        viewModel.addOrderTrackLiveData.observe(this) {
            hiddenLoading()
            if (it) {
                val notification = Notification(
                    contentNoti = getString(
                        R.string.str_track_notification,
                        viewModel.order?.orderCode ?: "",
                        viewModel.currentTrack?.address ?: "",
                        viewModel.currentTrack?.status ?: ""
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
                    R.string.str_track_notification,
                    viewModel.order?.orderCode ?: "",
                    viewModel.currentTrack?.address ?: "",
                    viewModel.currentTrack?.status ?: ""
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
            }
        }
        viewModel.sendNotiRequestFirebaseLiveData.observe(this) {
            hiddenLoading()
            if (it) {
                confirm.setNotice(getString(R.string.str_update_track_order_success))
                    .addButtonAgree {
                        showLoading()
                        viewModel.getAllOrderTrack()
                    }
            } else {
                confirm.newBuild().setNotice(getString(R.string.str_update_track_order_failed))
            }
        }
        viewModel.sendOttServerLiveData.observe(this) {
            hiddenLoading()
            Log.d(ContentValues.TAG, "observeData send ott: ${it?.code} - ${it?.data}")
        }
    }
}