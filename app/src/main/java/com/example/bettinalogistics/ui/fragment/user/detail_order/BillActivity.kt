package com.example.bettinalogistics.ui.fragment.user.detail_order

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.print.*
import android.provider.Settings
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import com.example.baseapp.BaseActivity
import com.example.baseapp.UtilsBase
import com.example.baseapp.view.getAmount
import com.example.baseapp.view.setSafeOnClickListener
import com.example.bettinalogistics.R
import com.example.bettinalogistics.databinding.ActivityBillBinding
import com.example.bettinalogistics.model.Order
import com.example.bettinalogistics.utils.Utils
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.InputStream
import java.util.*

class BillActivity : BaseActivity() {
    companion object {
        var PERMISSIONS = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
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

    private var printJob: PrintJob? = null

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
        binding.headerBill.ivHeaderBack.setSafeOnClickListener {
            finish()
        }

        binding.btnPrintBill.setSafeOnClickListener {
            val printManager = this
                .getSystemService(PRINT_SERVICE) as PrintManager
            val jobName = "${getString(R.string.app_name)}_bill_${System.currentTimeMillis()}"
            val printAdapter: PrintDocumentAdapter =
                binding.wvBill.createPrintDocumentAdapter(jobName)

            printJob = printManager.print(
                jobName,
                printAdapter,
                PrintAttributes.Builder().build()
            )
        }
        binding.tvBillShare.setSafeOnClickListener {
            if (!UtilsBase.g().hasPermissions(this, PERMISSIONS)) {
                providerPermission()
            } else {
                val dir =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + "/MerchantBill/")
                val fileName = "MerchantBill${System.currentTimeMillis()}.pdf"
                showLoading()
                PdfView.createWebPdfJob(
                    this@BillActivity,
                    binding.wvBill,
                    dir,
                    fileName,
                    object : PdfView.Callback {
                        override fun success(path: String) {
                            hiddenLoading()
                            PdfView.sharePdfFile(this@BillActivity, path)
                        }

                        override fun failure() {
                            hiddenLoading()
                        }
                    })
              }
        }
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

    private fun providerPermission() {
        val permission = resources.getString(R.string.permission_storage)
        confirm.setNotice(
            resources.getString(
                R.string.notification_permission,
                permission
            )
        ).addButtonAgree(R.string.agree) {
            confirm.dismiss()
            startActivity(Intent().apply {
                action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                data = Uri.fromParts("package", packageName, null)
            })
        }.addButtonCancel(R.string.txt_skip) {
            confirm.dismiss()
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
                .replaceFirst("[datePayment]", viewModel.payment?.datePayment ?: "")
                .replaceFirst("[companyName]", viewModel.payment?.order?.company?.name ?: "")
                .replaceFirst("[userPhone]", viewModel.payment?.user?.phone ?: "")
                .replaceFirst("[companyAddress]", viewModel.payment?.order?.company?.address ?: "")
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