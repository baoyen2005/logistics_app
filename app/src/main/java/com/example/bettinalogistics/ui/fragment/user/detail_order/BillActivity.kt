package com.example.bettinalogistics.ui.fragment.user.detail_order

import android.content.Context
import android.content.Intent
import android.os.Build
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import com.example.baseapp.BaseActivity
import com.example.baseapp.view.getAmount
import com.example.bettinalogistics.R
import com.example.bettinalogistics.databinding.ActivityBillBinding
import com.example.bettinalogistics.model.Order
import com.example.bettinalogistics.utils.Utils
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.InputStream
import java.util.*

class BillActivity : BaseActivity() {
    companion object {
        const val BILL_ORDER = "billOrder"
        fun startDetailOrderActivity(context: Context, order: Order): Intent {
            val intent = Intent(context, BillActivity::class.java)
            intent.putExtra(
                BILL_ORDER,
                Utils.g().getJsonFromObject(order)
            )
            return intent
        }
    }


    override val binding: ActivityBillBinding by lazy {
        ActivityBillBinding.inflate(layoutInflater)
    }

    override val viewModel: DetailUserOrderViewModel by viewModel()

    override fun initView() {
        val order = intent.getStringExtra(BILL_ORDER)
            ?.let { Utils.g().getObjectFromJson(it, Order::class.java) }
        binding.headerBill.tvHeaderTitle.text = getString(R.string.str_bill)
        if (order != null) {
            showLoading()
            viewModel.getPayment(order)
        }
    }

    override fun initListener() {
    }

    override fun observeData() {
        viewModel.getPaymentLiveData.observe(this) {
            hiddenLoading()
            if (it == null) {
                confirm.newBuild().setNotice(R.string.error_process)
            } else {
                viewModel.payment = it
                setUpWebview()
            }
        }
    }

    private fun setUpWebview() {
        binding.wvBill.settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
        binding.wvBill.scrollBarStyle = WebView.SCROLLBARS_OUTSIDE_OVERLAY
        binding.wvBill.settings.loadWithOverviewMode = true
        binding.wvBill.settings.useWideViewPort = true
        binding.wvBill.settings.loadWithOverviewMode = true
        binding.wvBill.settings.useWideViewPort = true
        binding.wvBill.settings.allowFileAccess = false
        binding.wvBill.settings.cacheMode = WebSettings.LOAD_NO_CACHE
        binding.wvBill.settings.domStorageEnabled = true

        binding.wvBill.requestFocus()
        binding.wvBill.webChromeClient = WebChromeClient()

        binding.wvBill.settings.javaScriptEnabled = true

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            Runnable { binding.wvBill.settings.displayZoomControls = false }.run()
        }
        try {
            val inputStream: InputStream = assets.open("bill/hoa_don_qr.html")
            val size: Int = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            var str = String(buffer)
            str = str.replaceFirst("[terminalName]", "Bettina Logistic")
                .replaceFirst(
                    "[businessAddress]",
                    "40 ngõ 1043 Giải Phóng, Thịnh Liêt, Hoàng Mai, Hà Nội"
                )
                .replaceFirst("[contactPhone]", "0369044581")
                .replaceFirst("[contactEmail]", "lebaoyen2950@gmail.com")
                .replaceFirst(
                    "[transactionId]",
                    viewModel.payment?.id ?: ""
                )
                .replaceFirst("[billNo]", "Bill${Date().time}")
                .replaceFirst("[transactionDate]", viewModel.payment?.datePayment ?: "")
                .replaceFirst("[customerName]", viewModel.payment?.order?.company?.name ?: "")
                .replaceFirst("[customerPhone]", viewModel.payment?.user?.phone ?: "")
                .replaceFirst("[terminalAddress]", viewModel.payment?.order?.company?.address ?: "")
                .replaceFirst("[orderId]", viewModel.payment?.order?.orderCode ?: "")
                .replaceFirst(
                    "[beforeDiscount]",
                    (viewModel.payment?.order?.amountBeforeDiscount ?: 0L).toString().getAmount()
                )
                .replaceFirst(
                    "[amountDiscount]",
                    (viewModel.payment?.order?.amountAfterDiscount ?: 0L).toString().getAmount()
                )
                .replaceFirst(
                    "[discount]",
                    (viewModel.payment?.order?.discount ?: 0L).toString().getAmount()
                )
                .replaceFirst(
                    "[total]",
                    (viewModel.payment?.order?.amountAfterDiscount ?: 0L).toString().getAmount()
                )
            binding.wvBill.loadDataWithBaseURL(
                "file:///android_asset/bill/",
                str,
                "text/html; charset=UTF-8", "utf-8", null
            )
        } catch (e: Exception) {

        }
    }
}