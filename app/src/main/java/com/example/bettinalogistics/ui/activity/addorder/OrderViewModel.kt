package com.example.bettinalogistics.ui.activity.addorder

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.baseapp.BaseViewModel
import com.example.baseapp.di.Common
import com.example.bettinalogistics.data.AddedProductToDbRepo
import com.example.bettinalogistics.data.OrderRepository
import com.example.bettinalogistics.data.database.ProductDatabase
import com.example.bettinalogistics.model.AddedProduct
import com.example.bettinalogistics.model.Product
import com.example.bettinalogistics.utils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.internal.Util

class OrderViewModel(private val orderRepository: OrderRepository) : BaseViewModel() {
    var productList = ArrayList<Product>()
    var addOrderLiveData = MutableLiveData<Boolean>()
    private var addedProductToDbRepo : AddedProductToDbRepo? = null

    fun initDatabase() {
        val dao = ProductDatabase.getDatabase(Common.currentActivity!!.applicationContext).productOrderDao()
        addedProductToDbRepo = AddedProductToDbRepo(dao)
    }

    var getAllProductLiveData = MutableLiveData<List<Product>>()
    fun getAllProduct(){
        val response = addedProductToDbRepo?.getAllProduct()
        response?.let{
            getAllProductLiveData = it
        }
    }

    var getAllAddedProductLiveData = MutableLiveData<List<AddedProduct>>()
    fun getAllAddedProduct() {
        val response = addedProductToDbRepo?.getAllAddedProduct()
        response?.let{
            getAllAddedProductLiveData = it
        }
    }

    fun updateAddedProduct(addedProduct: AddedProduct) =  viewModelScope.launch (Dispatchers.IO){
        Utils.g().getJsonFromObject(addedProduct)
            ?.let { addedProductToDbRepo?.updateAddedProduct(it) }
    }

    fun insertAddedProduct(addedProduct: AddedProduct) =  viewModelScope.launch (Dispatchers.IO){
        Utils.g().getJsonFromObject(addedProduct)
            ?.let { addedProductToDbRepo?.insertAddedProduct(it) }
    }

    fun deleteProduct(product: Product) =  viewModelScope.launch {
        addedProductToDbRepo?.deleteProduct(product)
    }

    fun deleteAllAddedProduct() =  viewModelScope.launch {
        addedProductToDbRepo?.deleteAllAddedProduct()
    }
}