package com.example.bettinalogistics.ui.fragment.user.person

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.baseapp.BaseViewModel
import com.example.bettinalogistics.data.AuthenticationRepository
import com.example.bettinalogistics.data.CardRepository
import com.example.bettinalogistics.data.OrderRepository
import com.example.bettinalogistics.di.AppData
import com.example.bettinalogistics.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserPersonViewModel(
    val authenticationRepository: AuthenticationRepository,
    val cardRepository: CardRepository, val orderRepository: OrderRepository
) : BaseViewModel() {
    var uri: Uri? = null
    var isChangeAvt: Boolean = false
    var company: UserCompany? = null
    var bankNameSelected: CommonEntity? = CommonEntity()
    var isEdit: Boolean = false

    var editUserLiveData = MutableLiveData<Boolean>()
    fun editUser(user: User) = viewModelScope.launch(Dispatchers.IO) {
        authenticationRepository.editUser(user, isChangeAvt) {
            editUserLiveData.postValue(it)
        }
    }

    var getCompanyLiveData = MutableLiveData<UserCompany>()
    fun getCompany() = viewModelScope.launch(Dispatchers.IO) {
        AppData.g().userId?.let {
            authenticationRepository.getCompany(it) {
                getCompanyLiveData.postValue(it)
            }
        }
    }

    var updateCompanyLiveData = MutableLiveData<Boolean>()
    fun updateCompany(companyEdit: UserCompany) = viewModelScope.launch(Dispatchers.IO) {
        authenticationRepository.updateCompany(companyEdit) {
            updateCompanyLiveData.postValue(it)
        }
    }

    var addCardLiveData = MutableLiveData<Boolean>()
    fun addCard(card: Card) = viewModelScope.launch(Dispatchers.IO) {
        cardRepository.addCard(card) {
            addCardLiveData.postValue(it)
        }
    }

    var updateCardLiveData = MutableLiveData<Boolean>()
    fun updateCard(card: Card) = viewModelScope.launch(Dispatchers.IO) {
        cardRepository.updateCard(card) {
            updateCardLiveData.postValue(it)
        }
    }

    var deleteCardLiveData = MutableLiveData<Boolean>()
    fun deleteCard(card: Card) = viewModelScope.launch(Dispatchers.IO) {
        cardRepository.deleteCard(card) {
            deleteCardLiveData.postValue(it)
        }
    }

    var getAllCardLiveData = MutableLiveData<List<Card>>()
    fun getAllCard() = viewModelScope.launch(Dispatchers.IO) {
        AppData.g().userId?.let {
            cardRepository.getAllCards(it) {
                getAllCardLiveData.postValue(it)
            }
        }
    }

    var getNewUserLiveData = MutableLiveData<User>()
    fun getNewUser() = viewModelScope.launch(Dispatchers.IO) {
        AppData.g().currentUser?.email?.let {
            authenticationRepository.getUser(it) {
                getNewUserLiveData.postValue(it)
            }
        }
    }

    var getAllOrderLiveData = MutableLiveData<List<Order>>()
    fun getAllOrderSuccess() = viewModelScope.launch(Dispatchers.IO) {
        orderRepository.getAllOrderTransactionsSuccess {
            getAllOrderLiveData.postValue(it)
        }
    }

    fun getListBank(): List<CommonEntity> {
        val list = ArrayList<CommonEntity>()
        list.add(CommonEntity().setTitle("BIDV - Đầu tư và Phát triển Việt Nam").setIdString("BIDV"))
        list.add(CommonEntity().setTitle("VietinBank - Công thương Việt Nam").setIdString("VietinBank"))
        list.add(CommonEntity().setTitle("Vietcombank - Ngoại Thương Việt Nam").setIdString("Vietcombank"))
        list.add(CommonEntity().setTitle("VPBank - Việt Nam Thịnh Vượng").setIdString("VPBank"))
        list.add(CommonEntity().setTitle("MB - Quân Đội").setIdString("MB"))
        list.add(CommonEntity().setTitle("Techcombank - Kỹ Thương").setIdString("Techcombank"))
        list.add(CommonEntity().setTitle("Agribank - NN&PT Nông thôn Việt Nam").setIdString("Agribank"))
        list.add(CommonEntity().setTitle("ACB - Á Châu").setIdString("ACB"))
        list.add(CommonEntity().setTitle("HDBank - Phát triển Thành phố Hồ Chí Minh").setIdString("HDBank"))
        list.add(CommonEntity().setTitle("SHB - Sài Gòn – Hà Nội").setIdString("SHB"))
        list.add(CommonEntity().setTitle("Sacombank - Sài Gòn Thương Tín").setIdString("Sacombank"))
        list.add(CommonEntity().setTitle("VBSP - Chính sách xã hội Việt Nam").setIdString("VBSP"))
        list.add(CommonEntity().setTitle("VIB - Quốc Tế").setIdString("VIB"))
        list.add(CommonEntity().setTitle("MSB - Hàng Hải").setIdString("MSB"))
        list.add(CommonEntity().setTitle("SCB - Sài Gòn").setIdString("SCB"))
        list.add(CommonEntity().setTitle("VDB - Phát triển Việt Nam").setIdString("VDB"))
        list.add(CommonEntity().setTitle("SeABank - Đông Nam Á").setIdString("SeABank"))
        list.add(CommonEntity().setTitle("OCB - Phương Đông").setIdString("OCB"))
        list.add(CommonEntity().setTitle("TPBank - Tiên Phong").setIdString("TPBank"))
        list.add(CommonEntity().setTitle("LienVietPostBank - Bưu điện Liên Việt").setIdString("LienVietPostBank"))
        list.add(CommonEntity().setTitle("Eximbank - Xuất Nhập Khẩu").setIdString("Eximbank"))
        list.add(CommonEntity().setTitle("PVcomBank - Đại Chúng Việt Nam").setIdString("PVcomBank"))
        list.add(CommonEntity().setTitle("Woori - Woori Việt Nam").setIdString("Woori"))
        list.add(CommonEntity().setTitle("Bac A Bank - Bắc Á").setIdString("BacABank"))
        list.add(CommonEntity().setTitle("HSBC - HSBC Việt Nam").setIdString("HSBC"))
        list.add(CommonEntity().setTitle("SCBVL - Standard Chartered Việt Nam").setIdString("SCBVL"))
        list.add(CommonEntity().setTitle("PBVN - Public Bank Việt Nam").setIdString("PBVN"))
        list.add(CommonEntity().setTitle("ABBANK - An Bình").setIdString("ABBANK"))
        list.add(CommonEntity().setTitle("SHBVN - Shinhan Việt Nam").setIdString("SHBVN"))
        list.add(CommonEntity().setTitle("VietABank - Việt Á").setIdString("VietABank"))
        list.add(CommonEntity().setTitle("DongA Bank - Đông Á").setIdString("DongABank"))
        list.add(CommonEntity().setTitle("UOB - UOB Việt Nam").setIdString("UOB"))
        list.add(CommonEntity().setTitle("Vietbank - Việt Nam Thương Tín").setIdString("Vietbank"))
        list.add(CommonEntity().setTitle("Nam A Bank - Nam Á").setIdString("NamABank"))
        list.add(CommonEntity().setTitle("NCB - Quốc dân").setIdString("NCB"))
        list.add(CommonEntity().setTitle("OceanBank - Đại Dương").setIdString("OceanBank"))
        list.add(CommonEntity().setTitle("CIMB - CIMB Việt Nam").setIdString("CIMB"))
        list.add(CommonEntity().setTitle("Viet Capital Bank - Bản Việt").setIdString("VietCapitalBank"))
        list.add(CommonEntity().setTitle("Kienlongbank - Kiên Long").setIdString("Kienlongbank"))
        list.add(CommonEntity().setTitle("IVB - Indovina").setIdString("IVB"))
        list.add(CommonEntity().setTitle("BAOVIET Bank - Bảo Việt").setIdString("BAOVIETBank"))
        list.add(CommonEntity().setTitle("SAIGONBANK - Sài Gòn Công Thương").setIdString("SAIGONBANK"))
        list.add(CommonEntity().setTitle("Co-opBank - Hợp tác xã Việt Nam").setIdString("CoopBank"))
        list.add(CommonEntity().setTitle("GPBank - Dầu khí toàn cầu").setIdString("GPBank"))
        list.add(CommonEntity().setTitle("VRB - Liên doanh Việt Nga").setIdString("VRB"))
        list.add(CommonEntity().setTitle("CB - Xây dựng").setIdString("CB"))
        list.add(CommonEntity().setTitle("PG Bank - Xăng dầu Petrolimex").setIdString("PGBank"))
        list.add(CommonEntity().setTitle("ANZVL - ANZ Việt Nam").setIdString("ANZVL"))
        list.add(CommonEntity().setTitle("HLBVN - Hong Leong Việt Nam").setIdString("HLBVN"))
        return list
    }
}