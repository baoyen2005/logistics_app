package com.example.bettinalogistics.ui.activity.addorder

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.baseapp.BaseViewModel
import com.example.bettinalogistics.data.OrderRepository
import com.example.bettinalogistics.model.Product
import com.example.bettinalogistics.model.SupplierCompany
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OrderViewModel(private val orderRepository: OrderRepository) : BaseViewModel() {
    var productList = ArrayList<Product>()
    var isEdit = false
    var beforeEditProductPosition : Int = -1
    var getAllProductLiveData = MutableLiveData<List<Product>>()
    var supplierCompany: SupplierCompany? = null
    fun getAllProduct() = viewModelScope.launch(Dispatchers.IO) {
        orderRepository.getAllProduct {
            getAllProductLiveData.postValue(it)
        }
    }

    var deleteProductLiveData = MutableLiveData<Boolean>()
    fun deleteProduct(product: Product) = viewModelScope.launch(Dispatchers.IO) {
        orderRepository.deleteProduct(product) {
            deleteProductLiveData.postValue(it)
        }
    }
}