package com.example.bettinalogistics.ui.addorder

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.baseapp.BaseViewModel
import com.example.bettinalogistics.R
import com.example.bettinalogistics.data.OrderRepository
import com.example.bettinalogistics.model.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OrderViewModel(val orderRepository: OrderRepository) : BaseViewModel() {
    val addOrderLiveData = MutableLiveData<Boolean>()
    val checkValidDataOrder = MutableLiveData<String>()

    fun addOrder(order: Order) = viewModelScope.launch(Dispatchers.IO) {
        val response = orderRepository.addOrder(order)
        response.let {
            addOrderLiveData.postValue(it.value)
        }
    }

    /*
     var imgUri: Uri? = null,
    var productName : String ? = null,
    var productDes : String? = null,
    var quantity: Long? = null,
    var volume: Double? = null,
    var mass: Double?= null,
    var numberOfCarton: Long? = null,
    var isOrderLCL: Boolean = true
     */
    fun checkInvalidData(order: Order, context: Context) : Boolean{
        if (order.imgUri == null || order.imgUri?.path.isNullOrBlank()) {
            checkValidDataOrder.postValue(context.getString(R.string.str_error_product_image_null))
        }
    }
}