package com.example.bettinalogistics.ui.activity.confirm_order

import android.content.Context
import android.content.Intent
import com.example.baseapp.BaseActivity
import com.example.bettinalogistics.databinding.ActivityConfirmOrderTransportationBinding
import com.example.bettinalogistics.model.Order
import com.example.bettinalogistics.model.OrderAddress
import com.example.bettinalogistics.model.UserCompany
import com.example.bettinalogistics.utils.Utils
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
            order: Order,
            orderAddress: OrderAddress,
            typeTransport: String,
            methodTransport: String,
            userCompany : UserCompany?
        ): Intent{
            val intent = Intent(context, ConfirmOrderTransportationActivity::class.java)
            intent.putExtra(ORDER_CONFIRM_ACTIVITY, Utils.g().getJsonFromObject(order))
            intent.putExtra(ORDER_ADDRESS_CONFIRM_ACTIVITY, Utils.g().getJsonFromObject(orderAddress))
            intent.putExtra(USER_COMPANY_TRANSPORT_ACTIVITY, Utils.g().getJsonFromObject(userCompany))
            intent.putExtra(TYPE_TRANSPORT_ACTIVITY, typeTransport)
            intent.putExtra(METHOD_TRANSPORT_ACTIVITY, methodTransport)
            return  intent
        }
    }

    override val viewModel: ConfirmOrderViewModel  by viewModel()

    override val binding: ActivityConfirmOrderTransportationBinding by lazy {
        ActivityConfirmOrderTransportationBinding.inflate(layoutInflater)
    }
    override fun initView() {
    }

    override fun initListener() {
    }

    override fun observeData() {
    }

}