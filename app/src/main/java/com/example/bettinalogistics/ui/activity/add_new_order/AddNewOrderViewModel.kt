package com.example.bettinalogistics.ui.activity.add_new_order

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.example.baseapp.BaseViewModel
import com.example.bettinalogistics.R
import com.example.bettinalogistics.data.OrderRepository
import com.example.bettinalogistics.model.Product

class AddNewOrderViewModel(private val orderRepository: OrderRepository) : BaseViewModel() {
    var uri: String? = null
    var isLCL: Boolean = true
    val checkValidDataOrderLiveData = MutableLiveData<String>()

    fun checkInvalidData(product: Product, context: Context): Boolean {
        if (product.imgUri == null || product.imgUri.isNullOrBlank()
            || product.productName.isNullOrBlank()
            || product.productDes.isNullOrBlank()
            || product.quantity.toString().isBlank()
            || product.volume.toString().isBlank()
            || product.mass.toString().isBlank()
            || product.numberOfCarton.toString().isBlank()
        ) {
            checkValidDataOrderLiveData.postValue(context.getString(R.string.str_error_product_data_blank_null))
            return false
        }
        return true
    }
}