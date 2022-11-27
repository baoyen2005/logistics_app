package com.example.bettinalogistics.ui.fragment.followtrask

import com.example.baseapp.BaseViewModel
import com.example.bettinalogistics.model.CommonEntity
import com.example.bettinalogistics.utils.DataConstant

class FollowTrackingViewModel : BaseViewModel() {

    fun getLisTrackingTab(): List<CommonEntity>{
        val list = mutableListOf<CommonEntity>()
        list.add(CommonEntity().setTitle(DataConstant.ORDER_STATUS_PENDING).setHightLight(true))
        list.add(CommonEntity().setTitle(DataConstant.ORDER_STATUS_CONFIRM))
        list.add(CommonEntity().setTitle(DataConstant.ORDER_STATUS_DELIVERED))
        list.add(CommonEntity().setTitle(DataConstant.ORDER_STATUS_CANCEL))
        list.add(CommonEntity().setTitle(DataConstant.ORDER_STATUS_PAYMENT_PAID))
        list.add(CommonEntity().setTitle(DataConstant.ORDER_STATUS_PAYMENT_WAITING))
        return list
    }
}