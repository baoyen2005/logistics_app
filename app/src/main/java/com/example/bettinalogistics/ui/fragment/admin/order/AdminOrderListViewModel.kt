package com.example.bettinalogistics.ui.fragment.admin.order

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.baseapp.BaseViewModel
import com.example.bettinalogistics.data.AdminOrShipperOrderRepo
import com.example.bettinalogistics.model.CommonEntity
import com.example.bettinalogistics.model.Order
import com.example.bettinalogistics.utils.DataConstant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AdminOrderListViewModel(val adminOrShipperOrderRepo: AdminOrShipperOrderRepo) : BaseViewModel() {
    var tabSelected : String = DataConstant.ORDER_STATUS_PENDING

    fun getAdminOrderListTab(): List<CommonEntity> {
        val list = mutableListOf<CommonEntity>()
        list.add(CommonEntity().setTitle(DataConstant.ORDER_STATUS_PENDING).setHightLight(true))
        list.add(CommonEntity().setTitle(DataConstant.ORDER_STATUS_CONFIRM))
        list.add(CommonEntity().setTitle(DataConstant.ORDER_STATUS_DELIVERED))
        list.add(CommonEntity().setTitle(DataConstant.ORDER_STATUS_CANCEL))
        list.add(CommonEntity().setTitle(DataConstant.ORDER_STATUS_PAYMENT_PAID))
        list.add(CommonEntity().setTitle(DataConstant.ORDER_STATUS_PAYMENT_WAITING))
        return list
    }
    var getListOrderByStatusLiveData = MutableLiveData<List<Order>?>()
    fun getListOrderByStatus(status: String?) = viewModelScope.launch(Dispatchers.IO){
        adminOrShipperOrderRepo.getListOrderByStatus(status){
            getListOrderByStatusLiveData.postValue(it)
        }
    }
}