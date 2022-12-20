package com.example.bettinalogistics.ui.fragment.user.followtrask

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.baseapp.BaseViewModel
import com.example.bettinalogistics.data.FollowTrackingRepo
import com.example.bettinalogistics.model.CommonEntity
import com.example.bettinalogistics.model.Order
import com.example.bettinalogistics.utils.DataConstant
import com.example.bettinalogistics.utils.DataConstant.Companion.ORDER_STATUS_PENDING
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserFollowTrackingViewModel(val followTrackingRepo: FollowTrackingRepo) : BaseViewModel() {
    var tabSelected: String? = ORDER_STATUS_PENDING
    fun getLisTrackingTab(): List<CommonEntity> {
        val list = mutableListOf<CommonEntity>()
        list.add(CommonEntity().setTitle(DataConstant.ORDER_STATUS_PENDING).setHightLight(true))
        list.add(CommonEntity().setTitle(DataConstant.ORDER_STATUS_CONFIRM))
        list.add(CommonEntity().setTitle(DataConstant.ORDER_STATUS_DELIVERING))
        list.add(CommonEntity().setTitle(DataConstant.ORDER_STATUS_DELIVERED))
        list.add(CommonEntity().setTitle(DataConstant.ORDER_STATUS_CANCEL))
        list.add(CommonEntity().setTitle(DataConstant.ORDER_STATUS_PAYMENT_PAID))
        list.add(CommonEntity().setTitle(DataConstant.ORDER_STATUS_PAYMENT_WAITING))
        return list
    }

    var getAllOrderByStatusAndUserLiveData = MutableLiveData<List<Order>>()
    fun getAllOrderByStatusAndUser(status: String?) = viewModelScope.launch(Dispatchers.IO) {
        followTrackingRepo.getAllOrderByStatusAndUser(status ?: "") {
            getAllOrderByStatusAndUserLiveData.postValue(it)
        }
    }
}