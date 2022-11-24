package com.example.bettinalogistics.ui.activity.confirm_order

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import com.example.baseapp.BaseActivity
import com.example.baseapp.view.getAmount
import com.example.baseapp.view.getAmountVND
import com.example.baseapp.view.setSafeOnClickListener
import com.example.bettinalogistics.R
import com.example.bettinalogistics.databinding.ActivityConfirmOrderTransportationBinding
import com.example.bettinalogistics.model.AddedProduct
import com.example.bettinalogistics.model.OrderAddress
import com.example.bettinalogistics.model.Product
import com.example.bettinalogistics.model.UserCompany
import com.example.bettinalogistics.ui.activity.add_new_order.AddAddressTransactionActivity
import com.example.bettinalogistics.ui.activity.addorder.OrderActivity
import com.example.bettinalogistics.ui.fragment.bottom_sheet.ConfirmBottomSheetFragment
import com.example.bettinalogistics.utils.Utils
import com.google.gson.reflect.TypeToken
import org.koin.androidx.viewmodel.ext.android.viewModel

class ConfirmOrderTransportationActivity : BaseActivity() {
    companion object {
        const val ORDER_CONFIRM_ACTIVITY = "orderInConfirmActivity"
        const val ORDER_ADDRESS_CONFIRM_ACTIVITY = "orderAddressInConfirmActivity"
        const val TYPE_TRANSPORT_ACTIVITY = "typeTransportInConfirmActivity"
        const val METHOD_TRANSPORT_ACTIVITY = "methodTransportInConfirmActivity"
        const val USER_COMPANY_TRANSPORT_ACTIVITY = "userCompanyInConfirmActivity"

        fun startConfirmOrderActivity(
            context: Context,
            product: List<Product>,
            orderAddress: OrderAddress,
            typeTransport: String,
            methodTransport: String,
            userCompany: UserCompany?
        ): Intent {
            val intent = Intent(context, ConfirmOrderTransportationActivity::class.java)
            intent.putExtra(ORDER_CONFIRM_ACTIVITY, Utils.g().getJsonFromObject(product))
            intent.putExtra(
                ORDER_ADDRESS_CONFIRM_ACTIVITY,
                Utils.g().getJsonFromObject(orderAddress)
            )
            intent.putExtra(
                USER_COMPANY_TRANSPORT_ACTIVITY,
                Utils.g().getJsonFromObject(userCompany)
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
        viewModel.products  = Utils.g().provideGson()
            .fromJson(intent.getStringExtra(AddAddressTransactionActivity.NEW_ORDER), object :
                TypeToken<List<Product>>() {}.type)?:  listOf()
        viewModel.orderAddress = Utils.g().getObjectFromJson(
            intent.getStringExtra(ORDER_ADDRESS_CONFIRM_ACTIVITY).toString(),
            OrderAddress::class.java
        )
        viewModel.userCompany =
            Utils.g().getObjectFromJson(
                intent.getStringExtra(USER_COMPANY_TRANSPORT_ACTIVITY).toString(),
                UserCompany::class.java
            )
        viewModel.typeTransport = intent.getStringExtra(TYPE_TRANSPORT_ACTIVITY)
        viewModel.methodTransport = intent.getStringExtra(METHOD_TRANSPORT_ACTIVITY)
        adapter = ConfirmUserInfoOrderAdapter()
        binding.rvInfoConfirmOrder.adapter = adapter
        adapter?.reset(viewModel.getListInfoConfirm())
        binding.tvPaymentInlandTruckingAmount.text =
            viewModel.calculateInlandTruckingFee().toString()
        binding.tvPaymentServiceAmount.text = viewModel.getServiceFee().toString().getAmount()
        binding.tvPaymentInternalTruckingAmount.text =
            viewModel.calculateInternalTruckingFee().toString().getAmount()
        binding.tvAmountBeforeDiscount.text =
            (viewModel.calculateInlandTruckingFee() + viewModel.getServiceFee() +
                    viewModel.calculateInternalTruckingFee()).toString().getAmount()
        binding.tvPaymentVoucher.text = "0"
        binding.tvInfoPaymentSumFinal.text = binding.tvAmountBeforeDiscount.text
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

            }
            dialog.setCancelListener {
                //show trang chinh sua hang hoa
               // startActivity(IntentUtil.buildCallIntent(getString(R.string.str_hotline_phone_number)))
                startActivity(Intent(this, OrderActivity::class.java))
            }
            dialog.show(supportFragmentManager, "ConfirmBottomSheetFragment")
        }

        binding.btnConfirmOrder.setSafeOnClickListener {
            viewModel.addOrderTransaction(
                amountBeforeDiscount = binding.tvAmountBeforeDiscount.text.toString().toDouble(),
                discount = 0.0,
                amountAfterDiscount = binding.tvInfoPaymentSumFinal.text.toString().toDouble()
            )
        }
    }

    override fun observeData() {
        viewModel.addOrderTransactionLiveData.observe(this){
            if(it){
                confirm.newBuild().setNotice(getString(R.string.str_add_order_success))
            }
            else{
                confirm.newBuild().setNotice(getString(R.string.str_add_order_fail))
            }
        }
    }
}