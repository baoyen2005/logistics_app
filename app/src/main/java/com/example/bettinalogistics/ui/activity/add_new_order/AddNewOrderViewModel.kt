package com.example.bettinalogistics.ui.activity.add_new_order

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.baseapp.BaseViewModel
import com.example.bettinalogistics.R
import com.example.bettinalogistics.data.OrderRepository
import com.example.bettinalogistics.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddNewOrderViewModel(private val orderRepository: OrderRepository) : BaseViewModel() {
    var uri: String? = null
    var isLCL: Boolean = true
    var order: Order?= null
    var userCompany: UserCompany?= null
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
    fun addCompanyInfo(userCompany: UserCompany) = viewModelScope.launch(Dispatchers.IO) {
        orderRepository.addUserCompany(userCompany) {
            addCompanyInfoLiveData.postValue(it)
        }
    }

    fun getListProductType(): List<CommonEntity> {
        val list = ArrayList<CommonEntity>()
        list.add(CommonEntity(title = "Hàng nặng thường", descript = "kg").setPriceKg(8000.0))
        list.add(CommonEntity(title = "Phụ tùng ô tô hàng nặng", descript = "kg").setPriceKg(13000.0))
        list.add(CommonEntity(title = "Siêu nặng", descript = "kg").setPriceKg(4000.0))
        list.add(CommonEntity(title = "Laptop/Pc", descript = "kg").setPriceKg(65000.0))
        list.add(CommonEntity(title = "Hàng phổ thông", descript = "kg").setPriceKg(8000.0))
        list.add(CommonEntity(title = "Hàng phổ thông", descript = "Trên 200kg tính theo khối").setPriceKg(11700.0).setPriceM3(3400000.0))
        list.add(CommonEntity(title = "Phụ tùng ô tô hàng bình thường", descript = "Trên 200kg tính theo khối").setPriceKg(14300.0).setPriceM3(3650000.0))
        list.add(CommonEntity(title = "Thực phẩm", descript = "Trên 200kg tính theo khối").setPriceKg(16000.0).setPriceM3(3650000.0))
        list.add(CommonEntity(title = "Dầu gối, sữa tắm, sơn móng tay", descript = "Trên 200kg tính theo khối").setPriceKg(14500.0).setPriceM3(3550000.0))
        list.add(CommonEntity(title = "Chất lỏng, dung dịch, hóa chất", descript = "Kg").setPriceKg(14500.0))
        list.add(CommonEntity(title = "Thời trang, linh kiện điện tử", descript = "Trên 200kg tính theo khối").setPriceKg(16000.0).setPriceM3(3900000.0))
        list.add(CommonEntity(title = "Hàng điện tử - gia dụng", descript = "Trên 200kg tính theo khối").setPriceKg(18000.0).setPriceM3(4550000.0))
        list.add(CommonEntity(title = "Hàng tạp", descript = "Trên 200kg tính theo khối").setPriceKg(23400.0).setPriceM3(3900000.0))
        list.add(CommonEntity(title = "Túi bóng", descript = "Kg").setPriceKg(8000.0))
        list.add(CommonEntity(title = "Cám chăn nuôi/ Phân bón", descript = "Kg").setPriceKg(13000.0))
        list.add(CommonEntity(title = "Kẹo đặc", descript = "Kg").setPriceKg(9100.0))
        list.add(CommonEntity(title = "Kẹo lỏng", descript = "Kg").setPriceKg(10500.0))
        return list
    }
}