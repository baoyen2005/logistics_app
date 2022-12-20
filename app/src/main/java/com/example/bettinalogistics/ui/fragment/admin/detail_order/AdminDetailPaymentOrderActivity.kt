package com.example.bettinalogistics.ui.fragment.admin.detail_order

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.baseapp.BaseActivity
import com.example.baseapp.UtilsBase
import com.example.bettinalogistics.databinding.ActivityAdminDetailOrderBinding
import com.example.bettinalogistics.model.Order
import com.example.bettinalogistics.ui.fragment.user.detail_order.DetailOrderAdapter
import com.example.bettinalogistics.utils.Utils
import org.koin.androidx.viewmodel.ext.android.viewModel

class AdminDetailPaymentOrderActivity : BaseActivity() {
    companion object {
        const val DETAIL_PAYMENT_ORDER = "detailPaymentOrder"
        fun startAdminDetailPaymentOrderActivity(context: Context, order: Order): Intent {
            val intent = Intent(context, AdminDetailPaymentOrderActivity::class.java)
            intent.putExtra(
                DETAIL_PAYMENT_ORDER,
                Utils.g().getJsonFromObject(order)
            )
            return intent
        }
    }

    private val detailOrderAdapter: DetailOrderAdapter by lazy { DetailOrderAdapter() }

    override val viewModel: DetailAdminOrderViewModel by viewModel()

    override val binding: ActivityAdminDetailOrderBinding by lazy {
        ActivityAdminDetailOrderBinding.inflate(layoutInflater)
    }

    override fun initView() {
        val order =
            intent.getStringExtra(DETAIL_PAYMENT_ORDER)
                ?.let { Utils.g().getObjectFromJson(it, Order::class.java) }
        viewModel.order = order
        binding.tvDetailOrderAmount.text = UtilsBase.g().getDotMoney((order?.amountBeforeDiscount ?: 0.0).toLong().toString()) + " VND"
        binding.tvDetailOrderCode.text = order?.orderCode ?: ""
        binding.tvDetailCompany.text = order?.company?.name ?: ""
        binding.tvDetailOrderOriginAddress.text = order?.address?.originAddress ?: ""
        binding.tvDetailOrderDestinationAddress.text = order?.address?.destinationAddress ?: ""
        binding.tvDetailStatusOrder.text = order?.statusOrder ?: ""
        binding.rvDetailOrder.adapter = detailOrderAdapter
        showLoading()
        if (order != null) {
            viewModel.getPayment(order)
        }
    }

    override fun initListener() {
        binding.ivDetailTransactionBack.setOnClickListener {
            finish()
        }
    }

    override fun observeData() {
        viewModel.getPaymentLiveData.observe(this) {
            hiddenLoading()
            if (it == null) {
                Log.e(ContentValues.TAG, "observeData:getPaymentLiveData roongx ")
            } else {
                detailOrderAdapter.reset(it.let { viewModel.getListDetailPaymentOrderCommonEntity(it, this) })
            }
        }
    }
}