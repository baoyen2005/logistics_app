package com.example.bettinalogistics.ui.fragment.ship.order_list

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.baseapp.BaseViewModel
import com.example.bettinalogistics.data.AdminOrShipperOrderRepo
import com.example.bettinalogistics.data.FollowTrackingRepo
import com.example.bettinalogistics.data.OTTFirebaseRepo
import com.example.bettinalogistics.model.*
import com.example.bettinalogistics.utils.AppConstant.Companion.TAG
import com.example.bettinalogistics.utils.DataConstant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ShipOrderViewModel(
    val ottFirebaseRepo: OTTFirebaseRepo,
    val trackingRepo: FollowTrackingRepo,
    val adminOrShipperOrderRepo: AdminOrShipperOrderRepo,
) : BaseViewModel() {
    var order: Order? = null
    var isEdit: Boolean? = null
    var currentTrack: Track? = null
    var isDelivered = false
    var tabSelected = DataConstant.ORDER_STATUS_CONFIRM
    var allTokenList = mutableListOf<TokenOtt>()
    var getAllOrderTrackLiveData = MutableLiveData<List<Track>>()

    fun getLisTrackingTab(): List<CommonEntity> {
        val list = mutableListOf<CommonEntity>()
        list.add(CommonEntity().setTitle(DataConstant.ORDER_STATUS_PENDING).setHightLight(true))
        list.add(CommonEntity().setTitle(DataConstant.ORDER_STATUS_CONFIRM))
        list.add(CommonEntity().setTitle(DataConstant.ORDER_STATUS_DELIVERED))
        list.add(CommonEntity().setTitle(DataConstant.ORDER_STATUS_DELIVERING))
        list.add(CommonEntity().setTitle(DataConstant.ORDER_STATUS_CANCEL))
        list.add(CommonEntity().setTitle(DataConstant.ORDER_STATUS_PAYMENT_PAID))
        list.add(CommonEntity().setTitle(DataConstant.ORDER_STATUS_PAYMENT_WAITING))
        return list
    }

    var getListOrderByStatusLiveData = MutableLiveData<List<Order>?>()
    fun getListOrderByStatus(status: String?) = viewModelScope.launch(Dispatchers.IO) {
        adminOrShipperOrderRepo.getListOrderByStatus(status) {
            getListOrderByStatusLiveData.postValue(it)
        }
    }

    fun getAllOrderTrack() = viewModelScope.launch(Dispatchers.IO) {
        order?.id?.let {
            trackingRepo.getAllTrackingByOrder(it) {
                getAllOrderTrackLiveData.postValue(it)
            }
        }
    }

    var addOrderTrackLiveData = MutableLiveData<Boolean>()
    fun addOrderTrack(track: Track) = viewModelScope.launch(Dispatchers.IO) {
        trackingRepo.addOrderTrack(track) {
            addOrderTrackLiveData.postValue(it)
        }
    }

    var updateOrderToDeliveringLiveData = MutableLiveData<Boolean>()
    fun updateOrderToDelivering(orderUpdate: Order) = viewModelScope.launch(Dispatchers.IO) {
        orderUpdate.let {
            adminOrShipperOrderRepo.updateOrderToDelivering(it) {
                updateOrderToDeliveringLiveData.postValue(it)
            }
        }
    }

    var deleteOrderTrackLiveData = MutableLiveData<Boolean>()
    fun deleteOrderTrack(track: Track) = viewModelScope.launch(Dispatchers.IO) {
        trackingRepo.deleteOrderTrack(track) {
            addOrderTrackLiveData.postValue(it)
        }
    }

    var updateOrderTrackLiveData = MutableLiveData<Boolean>()
    fun updateOrderTrack(track: Track) = viewModelScope.launch(Dispatchers.IO) {
        trackingRepo.updateOrderTrack(track) {
            addOrderTrackLiveData.postValue(it)
        }
    }

    var sendNotiRequestFirebaseLiveData = MutableLiveData<Boolean>()
    fun sendNotiRequestFirebase(notification: Notification) =
        viewModelScope.launch(Dispatchers.IO) {
            ottFirebaseRepo.sendNotiRequestFirebase(notification) {
                sendNotiRequestFirebaseLiveData.postValue(it)
            }
        }

    var getOttTokenListLiveData = MutableLiveData<List<TokenOtt>>()
    fun getOttTokenList() = viewModelScope.launch(Dispatchers.IO) {
        ottFirebaseRepo.getAllToken {
            Log.d(TAG, "getOttTokenList: shiporderviewmodel $it")
            getOttTokenListLiveData.postValue(it)
            it?.let {
                allTokenList.addAll(it)
            }
        }
    }

    var sendOttServerLiveData = MutableLiveData<OttResponse?>()
    fun sendOttServer(ottRequest: OttRequest) = viewModelScope.launch(Dispatchers.IO) {
        ottFirebaseRepo.sendOttServer(ottRequest) {
            sendOttServerLiveData.postValue(it)
        }
    }
}