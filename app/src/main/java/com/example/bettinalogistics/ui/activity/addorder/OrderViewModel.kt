package com.example.bettinalogistics.ui.activity.addorder

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.baseapp.BaseViewModel
import com.example.bettinalogistics.R
import com.example.bettinalogistics.data.OrderRepository
import com.example.bettinalogistics.model.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OrderViewModel(private val orderRepository: OrderRepository) : BaseViewModel() {
    var getAllOrderLiveData = MutableLiveData<List<Order>?>()

    fun getAllOrder() = viewModelScope.launch(Dispatchers.IO) {
        val response = orderRepository.getAllOrder()
        response.let {
            getAllOrderLiveData.postValue(it.value)
        }
    }
}