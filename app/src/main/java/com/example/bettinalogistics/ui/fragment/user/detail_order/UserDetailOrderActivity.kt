package com.example.bettinalogistics.ui.fragment.user.detail_order

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.baseapp.BaseActivity
import com.example.baseapp.UtilsBase
import com.example.bettinalogistics.R
import com.example.bettinalogistics.databinding.ActivityUserDetailOrderBinding
import com.example.bettinalogistics.di.AppData
import com.example.bettinalogistics.model.Notification
import com.example.bettinalogistics.model.Order
import com.example.bettinalogistics.model.OttRequest
import com.example.bettinalogistics.utils.DataConstant
import com.example.bettinalogistics.utils.Utils
import com.example.bettinalogistics.utils.Utils_Date
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class UserDetailOrderActivity : BaseActivity() {
    companion object {
        const val DETAIL_ORDER = "detailOrder"

        fun startDetailOrderActivity(context: Context, order: Order): Intent {
            val intent = Intent(context, UserDetailOrderActivity::class.java)
            intent.putExtra(
                DETAIL_ORDER,
                Utils.g().getJsonFromObject(order)
            )
            return intent
        }
    }

    private val detailOrderAdapter: DetailOrderAdapter by lazy { DetailOrderAdapter() }

    override val viewModel: DetailUserOrderViewModel by viewModel()

    override val binding: ActivityUserDetailOrderBinding by lazy {
        ActivityUserDetailOrderBinding.inflate(layoutInflater)
    }

    override fun initView() {
        showLoading()
        viewModel.getOttTokenList()
        val order =
            intent.getStringExtra(DETAIL_ORDER)
                ?.let { Utils.g().getObjectFromJson(it, Order::class.java) }
        viewModel.order = order
        binding.tvDetailOrderAmount.text =
            UtilsBase.g().getDotMoney((order?.amountBeforeDiscount ?: 0.0).toLong().toString()) + " VND"
        binding.tvDetailOrderCode.text = order?.orderCode ?: ""
        binding.tvDetailCompany.text = order?.company?.name ?: ""
        binding.tvDetailOrderOriginAddress.text = order?.address?.originAddress ?: ""
        binding.tvDetailOrderDestinationAddress.text = order?.address?.destinationAddress ?: ""
        binding.tvDetailStatusOrder.text = order?.statusOrder ?: ""
        binding.rvDetailOrder.adapter = detailOrderAdapter
        detailOrderAdapter.reset(order?.let { viewModel.getListDetailOrderCommonEntity(it, this) })
        binding.btnCancel.isVisible = (order?.statusOrder == DataConstant.ORDER_STATUS_PENDING)
        if (order?.statusOrder != DataConstant.ORDER_STATUS_PENDING && order?.statusOrder != DataConstant.ORDER_STATUS_CANCEL) {
            binding.btnUserViewAllTrack.isVisible
        }
        binding.btnUserPayment.isVisible =
            (order?.statusOrder == DataConstant.ORDER_STATUS_DELIVERED || order?.statusPayment == DataConstant.ORDER_STATUS_PAYMENT_WAITING)
        when (order?.statusOrder) {
            DataConstant.ORDER_STATUS_PENDING,
            DataConstant.ORDER_STATUS_PAYMENT_WAITING,
            DataConstant.ORDER_STATUS_DELIVERING -> {
                binding.linearDetailTransaction.setBackgroundResource(R.drawable.shape_merchant_color_ffbe21_corner_12)
                binding.tvDetailOrderAmount.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.merchant_color_ffbe21
                    )
                )
            }
            DataConstant.ORDER_STATUS_DELIVERED,
            DataConstant.ORDER_STATUS_PAYMENT_PAID,
            DataConstant.ORDER_STATUS_CONFIRM -> {
                binding.linearDetailTransaction.setBackgroundResource(R.drawable.shape_merchant_color_004a9c_corner_12)
                binding.tvDetailOrderAmount.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.merchant_color_004a9c
                    )
                )
            }
            else -> {
                binding.linearDetailTransaction.setBackgroundResource(R.drawable.shape_merchant_color_ea5b5b_corner_12)
                binding.tvDetailOrderAmount.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.merchant_color_ea5b5b
                    )
                )
            }
        }
    }

    override fun initListener() {
        binding.ivDetailTransactionBack.setOnClickListener {
            finish()
        }
        binding.btnCancel.setOnClickListener {
            showLoading()
            val order = viewModel.order
            order?.statusOrder = DataConstant.ORDER_STATUS_CANCEL
            order?.let { it1 -> viewModel.cancelOder(it1) }
        }
        binding.btnUserViewAllTrack.setOnClickListener {
            val intent = viewModel.order?.let { it1 -> UserListTrackOrderActivity.startDetailOrderActivity(this, it1) }
            startActivity(intent)
        }
        binding.btnUserPayment.setOnClickListener {

        }
    }

    override fun observeData() {
        viewModel.cancelOrderLiveData.observe(this) {
            hiddenLoading()
            if (it) {
                val notification = Notification(
                    contentNoti = getString(
                        R.string.str_noti_order_cancel,
                        viewModel.order?.company?.name ?: "",
                        AppData.g().currentUser?.phone ?: "",
                        viewModel.order?.orderCode,
                        UtilsBase.g().getDotMoneyHasCcy(
                            (viewModel.order?.amountAfterDiscount ?: 0L).toLong().toString(), "VND"
                        ),
                        viewModel.order?.orderDate ?: ""
                    ),
                    notificationType = getString(R.string.str_cancel_order),
                    notiTo = "admin",
                    confirmDate = "null",
                    requestDate = Utils_Date.convertformDate(Date(), Utils_Date.DATE_PATTERN_DD_MM_YYYY_HH_MM_SS),
                    order = viewModel.order
                )
                viewModel.sendNotiRequestFirebase(notification)
                val content = getString(
                    R.string.str_request_ott_cancel,
                    viewModel.order?.company?.name ?: "",
                    viewModel.order?.orderCode ?: ""
                )
                val listToken = mutableListOf<String>()
                viewModel.allTokenList.forEach {
                    if (it.role == "admin") {
                        it.token?.let { it1 -> listToken.add(it1) }
                    }
                }
                val request = OttRequest(
                    requestId = viewModel.order?.orderCode,
                    serialNumbers = listToken, content = content, title = "Thông báo đặt hàng",
                    notificationType = "142341234"
                )
                viewModel.sendOttServer(request)
            } else {
                confirm.setNotice(getString(R.string.str_cancel_failed))
            }
        }
        viewModel.sendNotiRequestFirebaseLiveData.observe(this) {
            hiddenLoading()
            if (it) {
                confirm.setNotice(getString(R.string.str_cancel_success)).addButtonAgree {
                    val i = Intent()
                    setResult(RESULT_OK, i)
                    finish()
                }
            } else {
                confirm.newBuild().setNotice(getString(R.string.str_add_order_fail))
            }
        }
        viewModel.getOttTokenListLiveData.observe(this) {
            hiddenLoading()
            if (it.isNullOrEmpty()) {
                Log.e(ContentValues.TAG, "observeData:tokenlist roongx ")
            } else {
                viewModel.allTokenList.addAll(it)
            }
        }

        viewModel.sendOttServerLiveData.observe(this) {
            hiddenLoading()
            Log.d(ContentValues.TAG, "observeData send ott: ${it?.code} - ${it?.data}")
        }
    }
}