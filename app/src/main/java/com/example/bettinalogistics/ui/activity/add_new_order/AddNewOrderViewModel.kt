package com.example.bettinalogistics.ui.activity.add_new_order

import android.content.Context
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.baseapp.BaseViewModel
import com.example.bettinalogistics.R
import com.example.bettinalogistics.data.OrderRepository
import com.example.bettinalogistics.model.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddNewOrderViewModel(private val orderRepository: OrderRepository) : BaseViewModel() {
    var uri: Uri? = null
    var isLCL :Boolean = false
    val addOrderLiveData = MutableLiveData<Boolean>()
    val checkValidDataOrderLiveData = MutableLiveData<String>()

    fun addOrder(order: Order) = viewModelScope.launch(Dispatchers.IO) {
        orderRepository.addOrder(order){
            addOrderLiveData.postValue(it.value)
        }

    }

    fun checkInvalidData(order: Order, context: Context) : Boolean{
        if (order.imgUri == null || order.imgUri?.path.isNullOrBlank()
            || order.productName.isNullOrBlank()
            || order.productDes.isNullOrBlank()
            || order.quantity.toString().isBlank()
            || order.volume.toString().isBlank()
            || order.mass.toString().isBlank()
            || order.numberOfCarton.toString().isBlank()) {
            checkValidDataOrderLiveData.postValue(context.getString(R.string.str_error_product_data_blank_null))
            return false
        }
        return true
    }
}