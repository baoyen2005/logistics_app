package com.example.bettinalogistics.ui.activity.add_new_order

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.baseapp.BaseViewModel
import com.example.bettinalogistics.R
import com.example.bettinalogistics.data.OrderRepository
import com.example.bettinalogistics.model.Order
import com.example.bettinalogistics.model.OrderAddress
import com.example.bettinalogistics.model.Product
import com.example.bettinalogistics.model.UserCompany
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddNewOrderViewModel(private val orderRepository: OrderRepository) : BaseViewModel() {
    var uri: String? = null
    var isLCL: Boolean = true
    var order: Order?= null
    var orderAddress: OrderAddress?= null
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
    var getUserCompanyInfoLiveData = MutableLiveData<UserCompany?>()
    fun getUserCompanyInfo() = viewModelScope.launch(Dispatchers.IO){
        orderRepository.getUserCompany(){
            getUserCompanyInfoLiveData.postValue(it)
        }
    }

    var addCompanyInfoLiveData = MutableLiveData<Boolean>()
    fun addCompanyInfo(userCompany: UserCompany) = viewModelScope.launch(Dispatchers.IO){
        orderRepository.addUserCompany(userCompany){
            addCompanyInfoLiveData.postValue(it)
        }
    }
}