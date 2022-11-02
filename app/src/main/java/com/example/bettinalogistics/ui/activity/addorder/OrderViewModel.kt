package com.example.bettinalogistics.ui.activity.addorder

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.baseapp.BaseViewModel
import com.example.bettinalogistics.data.OrderRepository
import com.example.bettinalogistics.model.Order
import com.example.bettinalogistics.model.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OrderViewModel(private val orderRepository: OrderRepository) : BaseViewModel() {
    var productList = ArrayList<Product>()
    var addOrderLiveData = MutableLiveData<Boolean>()

    fun addOrder(order: Order) = viewModelScope.launch(Dispatchers.IO) {
        orderRepository.addOrder(order) {
            addOrderLiveData.postValue(it)
        }
    }
}