package com.example.bettinalogistics.ui.activity.add_new_order

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.baseapp.BaseViewModel
import com.example.bettinalogistics.data.OrderRepository
import com.example.bettinalogistics.model.CommonEntity
import com.example.bettinalogistics.model.OrderAddress
import com.example.bettinalogistics.model.Product
import com.example.bettinalogistics.model.UserCompany
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddNewProductViewModel(private val orderRepository: OrderRepository) : BaseViewModel() {
    var uri: String? = null
    var isLCL: Boolean = true
    var products: List<Product>?= null
    var userCompany: UserCompany?= null
    var orderAddress: OrderAddress?= null
    var isEdit : Boolean = false
    var editProduct: Product? = null

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
    var insertProductLiveData = MutableLiveData<Boolean>()
    fun insertProduct(product: Product) = viewModelScope.launch (Dispatchers.IO) {
        orderRepository.addProduct(product){
            insertProductLiveData.postValue(it)
        }
    }

//    fun updateProduct(product: Product) = viewModelScope.launch (Dispatchers.IO){
//        addedProductToDbRepo?.updateProduct(product)
//    }

    fun getListContType(): List<CommonEntity>{
        val list = ArrayList<CommonEntity>()
        list.add(CommonEntity(title = "Container 10 feet", data = 16.0, quantity = 8810.0))
        list.add(CommonEntity(title = "Container 20 feet khô thường", data = 33.2, quantity = 28110.0))
        list.add(CommonEntity(title = "Container 20 feet lạnh", data = 28.6, quantity = 27560.0 ))
        list.add(CommonEntity(title = "Container 40 feet khô thường", data = 67.6, quantity = 28550.0))
        list.add(CommonEntity(title = "Container 40 feet khô - cao", data = 76.3, quantity = 28350.0))
        list.add(CommonEntity(title = "Container 40 feet lạnh", data = 67.7, quantity = 28390.0))
        list.add(CommonEntity(title = "Container hở mái 20 feet", data = 32.5, quantity = 28060.0))
        list.add(CommonEntity(title = "Container hở mái 40 feet", data = 65.9, quantity = 26680.0))
        return list
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
        list.add(CommonEntity(title = "Túi bóng", descript = "kg").setPriceKg(8000.0))
        list.add(CommonEntity(title = "Cám chăn nuôi/ Phân bón", descript = "Kg").setPriceKg(13000.0))
        list.add(CommonEntity(title = "Kẹo đặc", descript = "kg").setPriceKg(9100.0))
        list.add(CommonEntity(title = "Kẹo lỏng", descript = "kg").setPriceKg(10500.0))
        return list
    }
}