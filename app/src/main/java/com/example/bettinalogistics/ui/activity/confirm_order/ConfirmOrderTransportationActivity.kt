package com.example.bettinalogistics.ui.activity.confirm_order

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import com.example.baseapp.BaseActivity
import com.example.baseapp.UtilsBase
import com.example.baseapp.view.getAmount
import com.example.baseapp.view.getAmountServer
import com.example.baseapp.view.setSafeOnClickListener
import com.example.bettinalogistics.R
import com.example.bettinalogistics.databinding.ActivityConfirmOrderTransportationBinding
import com.example.bettinalogistics.di.AppData
import com.example.bettinalogistics.enums.VoucherDataEnum
import com.example.bettinalogistics.model.*
import com.example.bettinalogistics.ui.activity.addorder.OrderActivity
import com.example.bettinalogistics.ui.fragment.bottom_sheet.ConfirmBottomSheetFragment
import com.example.bettinalogistics.ui.fragment.user.person.EditUserAccountActivity
import com.example.bettinalogistics.utils.DataConstant
import com.example.bettinalogistics.utils.Utils
import com.example.bettinalogistics.utils.Utils_Date
import com.google.gson.reflect.TypeToken
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class ConfirmOrderTransportationActivity : BaseActivity() {
    companion object {
        const val PRODUCT_LIST_CONFIRM_ACTIVITY = "orderInConfirmActivity"
        const val ORDER_ADDRESS_CONFIRM_ACTIVITY = "orderAddressInConfirmActivity"
        const val TYPE_TRANSPORT_ACTIVITY = "typeTransportInConfirmActivity"
        const val METHOD_TRANSPORT_ACTIVITY = "methodTransportInConfirmActivity"
        const val USER_COMPANY_TRANSPORT_ACTIVITY = "userCompanyInConfirmActivity"
        const val SHIPPER_COMPANY_TRANSPORT_ACTIVITY = "shipperCompanyInConfirmActivity"

        fun startConfirmOrderActivity(
            context: Context,
            product: List<Product>,
            orderAddress: OrderAddress,
            typeTransport: String,
            methodTransport: String,
            userCompany: UserCompany?,
            supplierCompany: SupplierCompany?
        ): Intent {
            val intent = Intent(context, ConfirmOrderTransportationActivity::class.java)
            intent.putExtra(PRODUCT_LIST_CONFIRM_ACTIVITY, Utils.g().getJsonFromObject(product))
            intent.putExtra(
                ORDER_ADDRESS_CONFIRM_ACTIVITY,
                Utils.g().getJsonFromObject(orderAddress)
            )
            intent.putExtra(
                USER_COMPANY_TRANSPORT_ACTIVITY,
                Utils.g().getJsonFromObject(userCompany)
            )
            intent.putExtra(
                SHIPPER_COMPANY_TRANSPORT_ACTIVITY,
                Utils.g().getJsonFromObject(supplierCompany)
            )
            intent.putExtra(TYPE_TRANSPORT_ACTIVITY, typeTransport)
            intent.putExtra(METHOD_TRANSPORT_ACTIVITY, methodTransport)
            return intent
        }
    }

    private var adapter: ConfirmUserInfoOrderAdapter? = null

    override val viewModel: ConfirmOrderViewModel by viewModel()

    override val binding: ActivityConfirmOrderTransportationBinding by lazy {
        ActivityConfirmOrderTransportationBinding.inflate(layoutInflater)
    }

    @SuppressLint("SetTextI18n")
    override fun initView() {
        showLoading()
        viewModel.getOttTokenList()
        viewModel.products = Utils.g().provideGson()
            .fromJson(intent.getStringExtra(PRODUCT_LIST_CONFIRM_ACTIVITY), object :
                TypeToken<List<Product>>() {}.type) ?: listOf()
        viewModel.orderAddress = intent.getStringExtra(ORDER_ADDRESS_CONFIRM_ACTIVITY)?.let {
            Utils.g().getObjectFromJson(
                it,
                OrderAddress::class.java
            )
        }
        viewModel.userCompany =
            intent.getStringExtra(USER_COMPANY_TRANSPORT_ACTIVITY)?.let {
                Utils.g().getObjectFromJson(
                    it,
                    UserCompany::class.java
                )
            }
        viewModel.supplierCompany =
            intent.getStringExtra(SHIPPER_COMPANY_TRANSPORT_ACTIVITY)?.let {
                Utils.g().getObjectFromJson(
                    it,
                    SupplierCompany::class.java
                )
            }
        viewModel.typeTransport = intent.getStringExtra(TYPE_TRANSPORT_ACTIVITY)
        viewModel.methodTransport = intent.getStringExtra(METHOD_TRANSPORT_ACTIVITY)
        binding.confirmOrderHeader.tvHeaderTitle.text = getString(R.string.str_confirm_infor)

        val levelMember = Utils.g().getDataString(DataConstant.MEMBER_LEVEL)
        if (levelMember == null) {
            showLoading()
            viewModel.getAllOrderSuccess()
        } else {
            binding.tvPaymentVoucher.text = getDiscountByLevelMember(levelMember).toString()
        }
        adapter = ConfirmUserInfoOrderAdapter()
        binding.rvInfoConfirmOrder.adapter = adapter
        adapter?.reset(viewModel.getListInfoConfirm())

        val inlandTruckingFee = viewModel.calculateInlandTruckingFee()
        val internalTruckingFee = viewModel.calculateInternalTruckingFee()
        val serviceFee = viewModel.getServiceFee()
        val sum = inlandTruckingFee + internalTruckingFee + serviceFee
        val finalTotal =
            (sum - sum * (binding.tvPaymentVoucher.text.toString().toDoubleOrNull() ?: 0.0)).toLong()

        binding.tvPaymentInlandTruckingAmount.text = inlandTruckingFee.toString().getAmount()
        binding.tvPaymentServiceAmount.text = serviceFee.toString().getAmount()
        binding.tvPaymentInternalTruckingAmount.text = internalTruckingFee.toString().getAmount()
        binding.tvAmountBeforeDiscount.text = sum.toString().getAmount()
        binding.tvInfoPaymentSumFinal.text = finalTotal.toString().getAmount()
    }

    override fun initListener() {
        binding.tvEditInformation.setSafeOnClickListener {
            val dialog = ConfirmBottomSheetFragment()
                .setTitle(getString(R.string.str_choose_edit_infor))
                .setContent(getString(R.string.str_choose_edit_content))
                .setTitleBtnConfirm(getString(R.string.str_edit_order))
                .setTitleBtnCancel(getString(R.string.str_edit_info))
            dialog.setConfirmListener {
                dialog.dismiss()
            }
            dialog.setConfirmListener {
                // show trang chinh sua thong tin nguoi dung
                val intent = Intent(this, EditUserAccountActivity::class.java)
                intent.putExtra(EditUserAccountActivity.IS_EDIT_ACCOUNT, true)
                resultLauncherAddAddress.launch(intent)
            }
            dialog.setCancelListener {
                //show trang chinh sua hang hoa
               // startActivity(IntentUtil.buildCallIntent(getString(R.string.str_hotline_phone_number)))
                startActivity(Intent(this, OrderActivity::class.java))
            }
            dialog.show(supportFragmentManager, "ConfirmBottomSheetFragment")
        }

        binding.confirmOrderHeader.ivHeaderBack.setOnClickListener {
            finish()
        }

        binding.btnConfirmOrder.setSafeOnClickListener {
            showLoading()
            viewModel.addOrderTransaction(
                amountBeforeDiscount = binding.tvAmountBeforeDiscount.text.toString().getAmountServer().toDouble(),
                discount = 0.0,
                amountAfterDiscount = binding.tvInfoPaymentSumFinal.text.toString().getAmountServer().toDouble()
            )
        }
    }

    override fun observeData() {
        viewModel.getAllOrderLiveData.observe(this) {
            if (it.isNullOrEmpty()) {
                binding.tvPaymentVoucher.text =
                    getDiscountByLevelMember(getString(R.string.str_rank_level0)).toString()
                Utils.g()
                    .saveDataString(DataConstant.MEMBER_LEVEL, getString(R.string.str_rank_level0))
            } else {
                when (it.size) {
                    in 1..5 -> {
                        binding.tvPaymentVoucher.text =
                            getDiscountByLevelMember(getString(R.string.tv_hang_dong)).toString()
                        Utils.g().saveDataString(
                            DataConstant.MEMBER_LEVEL,
                            getString(R.string.tv_hang_dong)
                        )
                    }
                    in 6..10 -> {
                        binding.tvPaymentVoucher.text =
                            getDiscountByLevelMember(getString(R.string.tv_hang_bac)).toString()
                        Utils.g().saveDataString(
                            DataConstant.MEMBER_LEVEL,
                            getString(R.string.tv_hang_bac)
                        )
                    }
                    else -> {
                        binding.tvPaymentVoucher.text =
                            getDiscountByLevelMember(getString(R.string.tv_hang_vang)).toString()
                        Utils.g().saveDataString(
                            DataConstant.MEMBER_LEVEL,
                            getString(R.string.tv_hang_vang)
                        )
                    }
                }
            }
        }
        viewModel.addOrderTransactionLiveData.observe(this) {
            if (it) {
                viewModel.deleteAddedProducts()
                AppData.g().clearOrderInfo()
                val notification = Notification(
                    contentNoti = getString(
                        R.string.str_noti_order,
                        viewModel.customerOrder?.company?.name ?: "",
                        AppData.g().currentUser?.phone ?: "",
                        viewModel.customerOrder?.orderCode,
                        UtilsBase.g().getDotMoneyHasCcy(
                            (viewModel.customerOrder?.amountAfterDiscount ?: 0L).toString(), "VND"
                        ),
                        viewModel.customerOrder?.orderDate ?: ""
                    ),
                    notificationType = getString(R.string.str_request_order),
                    notiTo = "admin",
                    confirmDate = "null",
                    requestDate = Utils_Date.convertformDate(Date(), Utils_Date.DATE_PATTERN_DD_MM_YYYY_HH_MM_SS),
                    order = viewModel.customerOrder
                )
                viewModel.sendNotification(notification)
                val content = getString(
                    R.string.str_request_ott,
                    viewModel.userCompany?.name ?: "",
                    viewModel.customerOrder?.orderCode ?: ""
                )
                val listToken = mutableListOf<String>()
                viewModel.allTokenList.forEach {
                    if (it.role == "admin") {
                        it.token?.let { it1 -> listToken.add(it1) }
                    }
                }
                val request = OttRequest(
                    requestId = viewModel.customerOrder?.orderCode,
                    serialNumbers = listToken, content = content, title = "Thông báo đặt hàng",
                    notificationType = "142341234"
                )
                viewModel.sentOtt(request)
            } else {
                hiddenLoading()
                confirm.newBuild().setNotice(getString(R.string.str_add_order_fail))
            }
        }
        viewModel.sendNotificationLiveData.observe(this) {
            hiddenLoading()
            if (it) {
                confirm.newBuild().setNotice(getString(R.string.str_add_order_success))
                    .addButtonAgree {
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
                Log.e(TAG, "observeData:tokenlist roongx ")
            } else {
                viewModel.allTokenList.addAll(it)
            }
        }

        viewModel.sentOttLiveData.observe(this) {
            hiddenLoading()
            Log.d(TAG, "observeData send ott: ${it?.code} - ${it?.data}")
        }
    }

    private fun getDiscountByLevelMember(levelMember: String): Double {
        return if (VoucherDataEnum.NEW_MEMBER.title == levelMember) {
            VoucherDataEnum.NEW_MEMBER.discount
        } else if (VoucherDataEnum.HANG_DONG.title == levelMember) {
            VoucherDataEnum.HANG_DONG.discount
        } else if (VoucherDataEnum.HANG_BAC.title == levelMember) {
            VoucherDataEnum.HANG_BAC.discount
        } else {
            VoucherDataEnum.HANG_VANG.discount
        }
    }

    private var resultLauncherAddAddress =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when (result.resultCode) {
                RESULT_OK -> {
                    adapter?.reset(viewModel.getListInfoConfirm())
                }
            }
        }
}