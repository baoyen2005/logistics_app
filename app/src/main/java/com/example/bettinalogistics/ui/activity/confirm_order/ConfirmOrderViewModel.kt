package com.example.bettinalogistics.ui.activity.confirm_order

import com.example.baseapp.BaseViewModel
import com.example.bettinalogistics.data.OrderRepository
import com.example.bettinalogistics.model.CommonEntity
import com.example.bettinalogistics.model.Order
import com.example.bettinalogistics.model.UserCompany
import com.example.bettinalogistics.model.OrderAddress

class ConfirmOrderViewModel(val orderRepository: OrderRepository) : BaseViewModel() {
    val listUserInfoConfirmOrder = mutableListOf<CommonEntity>()

    fun getListUserInfoConfirm(orderAddress: OrderAddress, order: Order, userCompany: UserCompany){

    }
}