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
    var supplierCompany: SupplierCompany? = null

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
    fun updateProduct(product: Product) = viewModelScope.launch(Dispatchers.IO) {
        orderRepository.updateProduct(product) {
            updateProductLiveData.postValue(it)
        }
    }

    var getAllOrderLiveData = MutableLiveData<List<Order>>()
    fun getAllOrder() = viewModelScope.launch(Dispatchers.IO) {
        orderRepository.getAllOrderTransactions {
            getAllOrderLiveData.postValue(it)
        }
    }

    fun getListContType(): List<TypeCommonEntity> {
        val list = ArrayList<TypeCommonEntity>()
        list.add(
            TypeCommonEntity(
                title = "Container 10 feet",
                volumeMaxOfCont = 16.0,
                quantity = 8810.0
            )
        )
        list.add(
            TypeCommonEntity(
                title = "Container 20 feet kh?? th?????ng",
                volumeMaxOfCont = 33.2,
                quantity = 28110.0
            )
        )
        list.add(
            TypeCommonEntity(
                title = "Container 20 feet l???nh",
                volumeMaxOfCont = 28.6,
                quantity = 27560.0
            )
        )
        list.add(
            TypeCommonEntity(
                title = "Container 40 feet kh?? th?????ng",
                volumeMaxOfCont = 67.6,
                quantity = 28550.0
            )
        )
        list.add(
            TypeCommonEntity(
                title = "Container 40 feet kh?? - cao",
                volumeMaxOfCont = 76.3,
                quantity = 28350.0
            )
        )
        list.add(
            TypeCommonEntity(
                title = "Container 40 feet l???nh",
                volumeMaxOfCont = 67.7,
                quantity = 28390.0
            )
        )
        list.add(
            TypeCommonEntity(
                title = "Container h??? m??i 20 feet",
                volumeMaxOfCont = 32.5,
                quantity = 28060.0
            )
        )
        list.add(
            TypeCommonEntity(
                title = "Container h??? m??i 40 feet",
                volumeMaxOfCont = 65.9,
                quantity = 26680.0
            )
        )
        return list
    }

    fun getListProductType(): List<TypeCommonEntity> {
        val list = ArrayList<TypeCommonEntity>()
        list.add(TypeCommonEntity(title = "H??ng n???ng th?????ng", descript = "kg", priceKg = 8000.0))
        list.add(
            TypeCommonEntity(
                title = "Ph??? t??ng ?? t?? h??ng n???ng",
                descript = "kg",
                priceKg = 13000.0
            )
        )
        list.add(TypeCommonEntity(title = "Si??u n???ng", descript = "kg", priceKg = 4000.0))
        list.add(TypeCommonEntity(title = "Laptop/Pc", descript = "kg", priceKg = 65000.0))
        list.add(TypeCommonEntity(title = "H??ng ph??? th??ng", descript = "kg", priceKg = 8000.0))
        list.add(
            TypeCommonEntity(
                title = "H??ng ph??? th??ng",
                descript = "Tr??n 200kg t??nh theo kh???i",
                priceKg = 11700.0,
                priceM3 = 3400000.0
            )
        )
        list.add(
            TypeCommonEntity(
                title = "Ph??? t??ng ?? t?? h??ng b??nh th?????ng",
                descript = "Tr??n 200kg t??nh theo kh???i",
                priceKg = 14300.0,
                priceM3 = 3650000.0
            )
        )
        list.add(
            TypeCommonEntity(
                title = "Th???c ph???m",
                descript = "Tr??n 200kg t??nh theo kh???i",
                priceKg = 16000.0,
                priceM3 = 3650000.0
            )
        )
        list.add(
            TypeCommonEntity(
                title = "D???u g???i, s???a t???m, s??n m??ng tay",
                descript = "Tr??n 200kg t??nh theo kh???i",
                priceKg = 14500.0,
                priceM3 = 3550000.0
            )
        )
        list.add(
            TypeCommonEntity(
                title = "Ch???t l???ng, dung d???ch, h??a ch???t",
                descript = "kg",
                priceKg = 14500.0
            )
        )
        list.add(
            TypeCommonEntity(
                title = "Th???i trang, linh ki???n ??i???n t???",
                descript = "Tr??n 200kg t??nh theo kh???i",
                priceKg = 16000.0, priceM3 = 3900000.0
            )
        )
        list.add(
            TypeCommonEntity(
                title = "H??ng ??i???n t??? - gia d???ng",
                descript = "Tr??n 200kg t??nh theo kh???i",
                priceKg = 18000.0, priceM3 = 4550000.0
            )
        )
        list.add(
            TypeCommonEntity(
                title = "H??ng t???p",
                descript = "Tr??n 200kg t??nh theo kh???i",
                priceKg = 23400.0, priceM3 = 3900000.0
            )
        )
        list.add(TypeCommonEntity(title = "T??i b??ng", descript = "kg", priceKg = 8000.0))
        list.add(
            TypeCommonEntity(title = "C??m ch??n nu??i/ Ph??n b??n", descript = "kg", priceKg = 13000.0)
        )
        list.add(TypeCommonEntity(title = "K???o ?????c", descript = "kg", priceKg = 9100.0))
        list.add(TypeCommonEntity(title = "K???o l???ng", descript = "kg", priceKg = 10500.0))
        return list
    }
}