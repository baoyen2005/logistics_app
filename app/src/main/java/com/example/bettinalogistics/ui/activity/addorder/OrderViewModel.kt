package com.example.bettinalogistics.ui.activity.addorder

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.baseapp.BaseViewModel
import com.example.baseapp.di.Common
import com.example.bettinalogistics.data.AddedProductToDbRepo
import com.example.bettinalogistics.data.OrderRepository
import com.example.bettinalogistics.data.database.ProductDatabase
import com.example.bettinalogistics.model.AddedProduct
import com.example.bettinalogistics.model.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OrderViewModel(private val orderRepository: OrderRepository) : BaseViewModel() {
    var productList = ArrayList<Product>()
    var addOrderLiveData = MutableLiveData<Boolean>()
    private var addedProductToDbRepo : AddedProductToDbRepo? = null

    fun initDatabase() {
        val dao = ProductDatabase.getDatabase(Common.currentActivity!!.applicationContext).productOrderDao()
        addedProductToDbRepo = AddedProductToDbRepo(dao)
    }

    var getAllProductLiveData: LiveData<List<Product>> = MutableLiveData()
    fun getAllProduct(){
        val response = addedProductToDbRepo?.getAllProduct()
        response?.let{
            Log.d(TAG, "getAllAddedProduct: ${it.value?.get(0)?.productName?:"cccccccccc"}")
            getAllProductLiveData = it
        }
    }

    var getAllAddedProductLiveData: LiveData<List<AddedProduct>> = MutableLiveData()
    fun getAllAddedProduct() {
        val response = addedProductToDbRepo?.getAllAddedProduct()
        response?.let{
            Log.d(TAG, "getAllAddedProduct: ${it.value?.get(0)?.productList?.length?:0}")
            getAllAddedProductLiveData = it
        }
    }

    fun updateAddedProduct(addedProduct: AddedProduct) =  viewModelScope.launch (Dispatchers.IO){
        addedProductToDbRepo?.updateAddedProduct(addedProduct)
    }

    fun insertAddedProduct(addedProduct: AddedProduct) =  viewModelScope.launch (Dispatchers.IO){
        addedProductToDbRepo?.insertAddedProduct(addedProduct)
    }

    fun deleteProduct(product: Product) =  viewModelScope.launch {
        addedProductToDbRepo?.deleteProduct(product)
    }

    fun deleteAllAddedProduct() =  viewModelScope.launch {
        addedProductToDbRepo?.deleteAllAddedProduct()
    }
}