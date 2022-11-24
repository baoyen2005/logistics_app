package com.example.bettinalogistics.ui.activity.addorder

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.baseapp.BaseViewModel
import com.example.bettinalogistics.data.OrderRepository
import com.example.bettinalogistics.model.AddedProduct
import com.example.bettinalogistics.model.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OrderViewModel(private val orderRepository: OrderRepository) : BaseViewModel() {
    var productList = ArrayList<Product>()

    var getAllProductLiveData = MutableLiveData<List<Product>>()
    fun getAllProduct() = viewModelScope.launch(Dispatchers.IO) {
        orderRepository.getAllProduct {
            getAllProductLiveData.postValue(it)
        }
    }
}