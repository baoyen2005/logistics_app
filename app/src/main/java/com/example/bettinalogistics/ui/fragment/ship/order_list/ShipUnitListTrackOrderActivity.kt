package com.example.bettinalogistics.ui.fragment.ship.order_list

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import androidx.appcompat.widget.PopupMenu
import com.example.baseapp.BaseActivity
import com.example.bettinalogistics.R
import com.example.bettinalogistics.databinding.ActivityShipUnitListTrackOrderBinding
import com.example.bettinalogistics.enums.NotiToDataEnum
import com.example.bettinalogistics.model.Notification
import com.example.bettinalogistics.model.Order
import com.example.bettinalogistics.model.OttRequest
import com.example.bettinalogistics.model.Track
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

    private val trackListOrderAdapter: TrackListOrderAdapter by lazy { TrackListOrderAdapter() }

    override val viewModel: ShipOrderViewModel by viewModel()

    override val binding: ActivityShipUnitListTrackOrderBinding by lazy {
        ActivityShipUnitListTrackOrderBinding.inflate(layoutInflater)
    }

    override fun initView() {
        binding.rvListTrackOrder.adapter = trackListOrderAdapter
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
        binding.tvDetailOrderCustomerName.text = order?.company?.name ?: ""
        binding.tvDetailOrderCustomerPhone.text = order?.user?.phone ?: ""
        binding.tvDetailOrderShipperName.text = order?.supplierCompany?.name ?: ""
        binding.tvDetailOrderShipperPhone.text = order?.supplierCompany?.phone ?: ""
        binding.tvDetailOrderTypeTransport.text = order?.typeTransportation ?: ""
        binding.rvListTrackOrder.adapter = trackListOrderAdapter
        when (order?.statusOrder) {
            DataConstant.ORDER_STATUS_DELIVERING -> {
                binding.linearDetailTransaction.setBackgroundResource(R.drawable.shape_merchant_color_ffbe21_corner_12)
                binding.btnAddNewTrack.visibility = View.VISIBLE
                binding.btnUpdateDelivered.visibility = View.VISIBLE
            }
            DataConstant.ORDER_STATUS_DELIVERED -> {
                binding.btnAddNewTrack.visibility = View.GONE
                binding.btnUpdateDelivered.visibility = View.GONE
            }
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
            viewModel.isDelivered = false
            updateTrackOrderBottomSheet.show(supportFragmentManager, "ssssssss")
        }
        binding.btnUpdateDelivered.setOnClickListener {
            val order = viewModel.order
            order?.statusOrder = DataConstant.ORDER_STATUS_DELIVERED
            val confirmOrderBottomSheet = ConfirmBottomSheetFragment().setTitle(getString(R.string.str_confirm_delivered))
            confirmOrderBottomSheet.setConfirmListener {
                if (order != null) {
                    viewModel.isDelivered = true
                    showLoading()
                    viewModel.updateOrderToDelivering(order)
                }
            }
            confirmOrderBottomSheet.show(supportFragmentManager, "ssss")
        }
        trackListOrderAdapter.onShowDetailTrack = { track, view ->
            val popupMenu: PopupMenu = PopupMenu(this, view)
            popupMenu.menuInflater.inflate(R.menu.track_item_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_edit_track -> {
                        viewModel.isEdit = true
                        val updateTrackOrderBottomSheet = UpdateTrackOrderBottomSheet()
                        updateTrackOrderBottomSheet.track = track
                        updateTrackOrderBottomSheet.onUpdateOrAddFinish = {
                            it.orderId = viewModel.order?.id
                            viewModel.currentTrack = it
                            showLoading()
                            viewModel.updateOrderTrack(it)
                        }
                        viewModel.isDelivered = false
                        updateTrackOrderBottomSheet.show(supportFragmentManager, "ssssssss")
                    }

                    R.id.action_delete_product -> {
                        showLoading()
                        val list : ArrayList<Track> = viewModel.getAllOrderTrackLiveData.value as ArrayList<Track>
                        list.remove(track)
                        trackListOrderAdapter.reset(list)
                        viewModel.deleteOrderTrack(track)
                    }
                }
                true
            }
            popupMenu.show()
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
                trackListOrderAdapter.reset(it)
            }
        }
        viewModel.addOrderTrackLiveData.observe(this) {
            hiddenLoading()
            if (it) {
                val notificationAdmin = Notification(
                    uid = com.example.bettinalogistics.di.AppData.g().userId,
                    contentNoti = getString(
                        R.string.str_track_notification,
                        viewModel.order?.orderCode ?: "",
                        viewModel.currentTrack?.address ?: "",
                        viewModel.currentTrack?.status ?: ""
                    ),
                    notificationType = getString(R.string.str_update_track_order),
                    notiTo = NotiToDataEnum.ADMIN.notiTo,
                    confirmDate = Utils_Date.convertformDate(
                        Date(),
                        Utils_Date.DATE_PATTERN_DD_MM_YYYY_HH_MM_SS
                    ),
                    order = viewModel.order
                )
                viewModel.sendNotiRequestFirebase(notificationAdmin)
                val notificationUser = Notification(
                    uid = com.example.bettinalogistics.di.AppData.g().userId,
                    contentNoti = getString(
                        R.string.str_track_notification,
                        viewModel.order?.orderCode ?: "",
                        viewModel.currentTrack?.address ?: "",
                        viewModel.currentTrack?.status ?: ""
                    ),
                    notificationType = getString(R.string.str_update_track_order),
                    notiTo = NotiToDataEnum.USER.notiTo,
                    confirmDate = Utils_Date.convertformDate(
                        Date(),
                        Utils_Date.DATE_PATTERN_DD_MM_YYYY_HH_MM_SS
                    ),
                    order = viewModel.order
                )
                viewModel.sendNotiRequestFirebase(notificationUser)
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
        viewModel.updateOrderToDeliveringLiveData.observe(this){
            if(it) {
                val notificationAdmin = Notification(
                    uid = com.example.bettinalogistics.di.AppData.g().userId,
                    contentNoti = getString(
                        R.string.str_track_delivered_notification,
                        viewModel.order?.orderCode ?: "",
                    ),
                    notificationType = getString(R.string.str_confirm_delivered_noti),
                    notiTo = NotiToDataEnum.ADMIN.notiTo,
                    confirmDate = Utils_Date.convertformDate(
                        Date(),
                        Utils_Date.DATE_PATTERN_DD_MM_YYYY_HH_MM_SS
                    ),
                    order = viewModel.order
                )
                viewModel.sendNotiRequestFirebase(notificationAdmin)
                val notificationUser = Notification(
                    uid = com.example.bettinalogistics.di.AppData.g().userId,
                    contentNoti = getString(
                        R.string.str_track_delivered_notification,
                        viewModel.order?.orderCode ?: "",
                    ),
                    notificationType = getString(R.string.str_confirm_delivered_noti),
                    notiTo = NotiToDataEnum.USER.notiTo,
                    confirmDate = Utils_Date.convertformDate(
                        Date(),
                        Utils_Date.DATE_PATTERN_DD_MM_YYYY_HH_MM_SS
                    ),
                    order = viewModel.order
                )
                viewModel.sendNotiRequestFirebase(notificationUser)
                val content = getString(
                    R.string.str_track_delivered_notification,
                    viewModel.order?.orderCode ?: "",
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
                    title = getString(R.string.str_confirm_delivered_noti),
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
                        if (viewModel.isDelivered) {
                            finish()
                        } else {
                            showLoading()
                            viewModel.getAllOrderTrack()
                        }
                    }
            } else {
                confirm.newBuild().setNotice(getString(R.string.str_update_track_order_failed))
            }
        }
        viewModel.sendOttServerLiveData.observe(this) {
            hiddenLoading()
            Log.d(ContentValues.TAG, "observeData send ott: ${it?.code} - ${it?.data}")
        }
        viewModel.deleteOrderTrackLiveData.observe(this) {
            hiddenLoading()
            if (it) {
                confirm.newBuild().setNotice(getString(R.string.str_delete_track_success))
            } else {
                confirm.newBuild().setNotice(getString(R.string.str_delete_track_fail))
            }
        }
        viewModel.updateOrderTrackLiveData.observe(this) {
            hiddenLoading()
            if (it) {
                confirm.newBuild().setNotice(getString(R.string.str_update_track_order_success))
            } else {
                confirm.newBuild().setNotice(getString(R.string.str_update_track_order_failed))
            }
        }
    }
}