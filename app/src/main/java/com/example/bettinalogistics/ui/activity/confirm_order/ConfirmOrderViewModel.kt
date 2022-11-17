package com.example.bettinalogistics.ui.activity.confirm_order

import android.annotation.SuppressLint
import com.example.baseapp.BaseViewModel
import com.example.baseapp.di.Common
import com.example.bettinalogistics.R
import com.example.bettinalogistics.data.OrderRepository
import com.example.bettinalogistics.model.*
import com.example.bettinalogistics.ui.activity.confirm_order.ConfirmUserInfoOrderAdapter.Companion.TYPE_ITEM_USER
import com.example.bettinalogistics.utils.DataConstant
import com.example.bettinalogistics.utils.Utils
import java.util.*
import kotlin.collections.ArrayList

class ConfirmOrderViewModel(val orderRepository: OrderRepository) : BaseViewModel() {
    @SuppressLint("StaticFieldLeak")
    private val context = Common.currentActivity
    var order: Order? = null
    var orderAddress: OrderAddress? = null
    var typeTransport: String? = null
    var methodTransport: String? = null
    var userCompany : UserCompany? = null

    fun confirmOrder(){
        val code  = "BT${Date().time}"
     //   val orderTransaction = OrderTransaction(code = code, order = order, address = orderAddress, company = userCompany)
    }

    private fun getListUserInfoConfirm(
    ): ArrayList<CommonEntity> {
        val list = ArrayList<CommonEntity>()
        val companyName =
            CommonEntity(
                title = context!!.getString(R.string.str_company_name),
                descript = userCompany?.name?:""
            ).setTypeLayout(TYPE_ITEM_USER)
        val userName = CommonEntity(
            title = context.getString(R.string.str_user_name), descript = Utils.g().getDataString(
                DataConstant.USER_FULL_NAME
            )
        ).setTypeLayout(TYPE_ITEM_USER)
        val userPhone = CommonEntity(
            title = context.getString(R.string.str_phone_contact),
            descript = Utils.g().getDataString(
                DataConstant.USER_PHONE
            )
        ).setTypeLayout(TYPE_ITEM_USER)
        val origin = CommonEntity(
            title = context.getString(R.string.str_origin_address),
            descript = orderAddress?.address?.originAddress ?: ""
        ).setTypeLayout(TYPE_ITEM_USER)
        val destination = CommonEntity(
            title = context.getString(R.string.str_destination_address),
            descript = orderAddress?.address?.destinationAddress ?: ""
        ).setTypeLayout(TYPE_ITEM_USER)
        list.add(companyName)
        list.add(userName)
        list.add(userPhone)
        list.add(origin)
        list.add(destination)
        return list
    }

    fun getListInfoConfirm(): ArrayList<Any> {
        val list = ArrayList<Any>()
        list.addAll(getListUserInfoConfirm())
        order?.productList?.forEach {
            val orderConfirm = ConfirmOrder(
                transportType = typeTransport, transportMethod = methodTransport,
                product = it, amount = order?.productList!!.size
            )
            list.add(orderConfirm)
        }
        return list
    }
}