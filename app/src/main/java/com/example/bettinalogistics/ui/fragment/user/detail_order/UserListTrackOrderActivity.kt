package com.example.bettinalogistics.ui.fragment.user.detail_order

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import com.example.baseapp.BaseActivity
import com.example.bettinalogistics.R
import com.example.bettinalogistics.databinding.ActivityUserListTrackOrderBinding
import com.example.bettinalogistics.enums.NotiToDataEnum
import com.example.bettinalogistics.model.Notification
import com.example.bettinalogistics.model.Order
import com.example.bettinalogistics.model.OttRequest
import com.example.bettinalogistics.model.Track
import com.example.bettinalogistics.ui.fragment.bottom_sheet.ConfirmBottomSheetFragment
import com.example.bettinalogistics.ui.fragment.bottom_sheet.UpdateTrackOrderBottomSheet
import com.example.bettinalogistics.ui.fragment.ship.order_list.TrackListOrderAdapter
import com.example.bettinalogistics.utils.AppConstant.Companion.TAG
import com.example.bettinalogistics.utils.DataConstant
import com.example.bettinalogistics.utils.Utils
import com.example.bettinalogistics.utils.Utils_Date
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class UserListTrackOrderActivity : BaseActivity() {
    companion object {
        const val LIST_TRACK_ORDER = "listTrackOrder"

        fun startDetailOrderActivity(context: Context, order: Order): Intent {
            val intent = Intent(context, UserListTrackOrderActivity::class.java)
            intent.putExtra(
                LIST_TRACK_ORDER,
                Utils.g().getJsonFromObject(order)
            )
            return intent
        }
    }

    private val trackListOrderAdapter: TrackListOrderAdapter by lazy { TrackListOrderAdapter() }

    override val viewModel: DetailUserOrderViewModel by viewModel()

    override val binding: ActivityUserListTrackOrderBinding by lazy {
        ActivityUserListTrackOrderBinding.inflate(layoutInflater)
    }

    override fun initView() {
        binding.rvListTrackOrder.adapter = trackListOrderAdapter
        val order =
            intent.getStringExtra(LIST_TRACK_ORDER)
                ?.let { Utils.g().getObjectFromJson(it, Order::class.java) }
        viewModel.order = order
        showLoading()
        viewModel.getAllOrderTrack()
        binding.tvDetailOrderCode.text = order?.orderCode ?: ""
        binding.tvDetailOrderOriginAddress.text = order?.address?.originAddress ?: ""
        binding.tvDetailOrderDestinationAddress.text = order?.address?.destinationAddress ?: ""
        binding.tvDetailStatusOrder.text = order?.statusOrder ?: ""
        binding.tvDetailOrderCustomerName.text = order?.company?.name ?: ""
        binding.tvDetailOrderCustomerPhone.text = order?.user?.phone ?: ""
        binding.rvListTrackOrder.adapter = trackListOrderAdapter
        binding.emptyOrderTracking.tvEmptyLayoutTitle.text = getString(R.string.str_no_track_user)
    }

    override fun initListener() {
        binding.ivDetailTransactionBack.setOnClickListener {
            finish()
        }
        trackListOrderAdapter.onShowDetailTrack = {track, _ ->
            val detailUserTrackBottomSheetFragment = DetailUserTrackBottomSheetFragment()
            detailUserTrackBottomSheetFragment.track = track
            detailUserTrackBottomSheetFragment.show(supportFragmentManager, "ssss")
        }
    }

    override fun observeData() {
        viewModel.getAllOrderTrackLiveData.observe(this) {
            hiddenLoading()
            if (it.isNullOrEmpty()) {
                binding.emptyOrderTracking.root.isVisible = true
                binding.rvListTrackOrder.isVisible = false
            } else {
                binding.emptyOrderTracking.root.isVisible = false
                binding.rvListTrackOrder.isVisible = true
                trackListOrderAdapter.reset(it)
            }
        }
    }
}