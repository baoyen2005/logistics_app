package com.example.bettinalogistics.ui.activity.add_new_order

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.baseapp.BaseViewModel
import com.example.bettinalogistics.data.OrderRepository
import com.example.bettinalogistics.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddNewProductViewModel(private val orderRepository: OrderRepository) : BaseViewModel() {
    var uri: String? = null
    var isLCL: Boolean = true
    var products: List<Product>? = null
    var userCompany: UserCompany? = null
    var orderAddress: OrderAddress? = null
    var isEdit: Boolean = false
    var editProduct: Product? = null
    var distance: String? = null
    var typeTransaction: String? = null
    var methodTransaction: String? = null
    var orderType: TypeCommonEntity? = null
    var contType: TypeCommonEntity? = null

    var getUserCompanyInfoLiveData = MutableLiveData<UserCompany?>()
    fun getUserCompanyInfo() = viewModelScope.launch(Dispatchers.IO) {
        orderRepository.getUserCompany() {
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
    fun insertProduct(product: Product) = viewModelScope.launch(Dispatchers.IO) {
        orderRepository.addProduct(product) {
            insertProductLiveData.postValue(it)
        }
    }

    var updateProductLiveData = MutableLiveData<Boolean>()
    fun updateProduct(product: Product) = viewModelScope.launch (Dispatchers.IO){
        orderRepository.updateProduct(product){
            updateProductLiveData.postValue(it)
        }
    }

    fun getListContType(): List<TypeCommonEntity> {
        val list = ArrayList<TypeCommonEntity>()
        list.add(TypeCommonEntity(title = "Container 10 feet", volumeMaxOfCont = 16.0, quantity = 8810.0))
        list.add(
            TypeCommonEntity(
                title = "Container 20 feet khô thường",
                volumeMaxOfCont = 33.2,
                quantity = 28110.0
            )
        )
        list.add(
            TypeCommonEntity(
                title = "Container 20 feet lạnh",
                volumeMaxOfCont = 28.6,
                quantity = 27560.0
            )
        )
        list.add(
            TypeCommonEntity(
                title = "Container 40 feet khô thường",
                volumeMaxOfCont = 67.6,
                quantity = 28550.0
            )
        )
        list.add(
            TypeCommonEntity(
                title = "Container 40 feet khô - cao",
                volumeMaxOfCont = 76.3,
                quantity = 28350.0
            )
        )
        list.add(
            TypeCommonEntity(
                title = "Container 40 feet lạnh",
                volumeMaxOfCont = 67.7,
                quantity = 28390.0
            )
        )
        list.add(
            TypeCommonEntity(
                title = "Container hở mái 20 feet",
                volumeMaxOfCont = 32.5,
                quantity = 28060.0
            )
        )
        list.add(
            TypeCommonEntity(
                title = "Container hở mái 40 feet",
                volumeMaxOfCont = 65.9,
                quantity = 26680.0
            )
        )
        return list
    }

    fun getListProductType(): List<TypeCommonEntity> {
        val list = ArrayList<TypeCommonEntity>()
        list.add(TypeCommonEntity(title = "Hàng nặng thường", descript = "kg", priceKg = 8000.0))
        list.add(
            TypeCommonEntity(
                title = "Phụ tùng ô tô hàng nặng",
                descript = "kg",
                priceKg = 13000.0
            )
        )
        list.add(TypeCommonEntity(title = "Siêu nặng", descript = "kg", priceKg = 4000.0))
        list.add(TypeCommonEntity(title = "Laptop/Pc", descript = "kg", priceKg = 65000.0))
        list.add(TypeCommonEntity(title = "Hàng phổ thông", descript = "kg", priceKg = 8000.0))
        list.add(
            TypeCommonEntity(
                title = "Hàng phổ thông",
                descript = "Trên 200kg tính theo khối",
                priceKg = 11700.0,
                priceM3 = 3400000.0
            )
        )
        list.add(
            TypeCommonEntity(
                title = "Phụ tùng ô tô hàng bình thường",
                descript = "Trên 200kg tính theo khối",
                priceKg = 14300.0,
                priceM3 = 3650000.0
            )
        )
        list.add(
            TypeCommonEntity(
                title = "Thực phẩm",
                descript = "Trên 200kg tính theo khối",
                priceKg = 16000.0,
                priceM3 = 3650000.0
            )
        )
        list.add(
            TypeCommonEntity(
                title = "Dầu gối, sữa tắm, sơn móng tay",
                descript = "Trên 200kg tính theo khối",
                priceKg = 14500.0,
                priceM3 = 3550000.0
            )
        )
        list.add(
            TypeCommonEntity(
                title = "Chất lỏng, dung dịch, hóa chất",
                descript = "kg",
                priceKg = 14500.0
            )
        )
        list.add(
            TypeCommonEntity(
                title = "Thời trang, linh kiện điện tử",
                descript = "Trên 200kg tính theo khối",
                priceKg = 16000.0, priceM3 = 3900000.0
            )
        )
        list.add(
            TypeCommonEntity(
                title = "Hàng điện tử - gia dụng",
                descript = "Trên 200kg tính theo khối",
                priceKg = 18000.0, priceM3 = 4550000.0
            )
        )
        list.add(
            TypeCommonEntity(
                title = "Hàng tạp",
                descript = "Trên 200kg tính theo khối",
                priceKg = 23400.0, priceM3 = 3900000.0
            )
        )
        list.add(TypeCommonEntity(title = "Túi bóng", descript = "kg", priceKg = 8000.0))
        list.add(
            TypeCommonEntity(title = "Cám chăn nuôi/ Phân bón", descript = "kg", priceKg = 13000.0)
        )
        list.add(TypeCommonEntity(title = "Kẹo đặc", descript = "kg", priceKg = 9100.0))
        list.add(TypeCommonEntity(title = "Kẹo lỏng", descript = "kg", priceKg = 10500.0))
        return list
    }
}