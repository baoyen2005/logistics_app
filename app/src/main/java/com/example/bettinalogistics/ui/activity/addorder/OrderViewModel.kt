package com.example.bettinalogistics.ui.activity.addorder

import androidx.lifecycle.MutableLiveData
import com.example.baseapp.BaseViewModel
import com.example.bettinalogistics.data.OrderRepository
import com.example.bettinalogistics.model.Product

class OrderViewModel(private val orderRepository: OrderRepository) : BaseViewModel() {
    var productList = ArrayList<Product>()
    var addOrderLiveData = MutableLiveData<Boolean>()

}