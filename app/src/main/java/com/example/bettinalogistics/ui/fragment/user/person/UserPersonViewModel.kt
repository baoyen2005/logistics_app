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
        list.add(CommonEntity().setTitle("BIDV - ?????u t?? v?? Ph??t tri???n Vi???t Nam").setIdString("BIDV"))
        list.add(CommonEntity().setTitle("VietinBank - C??ng th????ng Vi???t Nam").setIdString("VietinBank"))
        list.add(CommonEntity().setTitle("Vietcombank - Ngo???i Th????ng Vi???t Nam").setIdString("Vietcombank"))
        list.add(CommonEntity().setTitle("VPBank - Vi???t Nam Th???nh V?????ng").setIdString("VPBank"))
        list.add(CommonEntity().setTitle("MB - Qu??n ?????i").setIdString("MB"))
        list.add(CommonEntity().setTitle("Techcombank - K??? Th????ng").setIdString("Techcombank"))
        list.add(CommonEntity().setTitle("Agribank - NN&PT N??ng th??n Vi???t Nam").setIdString("Agribank"))
        list.add(CommonEntity().setTitle("ACB - ?? Ch??u").setIdString("ACB"))
        list.add(CommonEntity().setTitle("HDBank - Ph??t tri???n Th??nh ph??? H??? Ch?? Minh").setIdString("HDBank"))
        list.add(CommonEntity().setTitle("SHB - S??i G??n ??? H?? N???i").setIdString("SHB"))
        list.add(CommonEntity().setTitle("Sacombank - S??i G??n Th????ng T??n").setIdString("Sacombank"))
        list.add(CommonEntity().setTitle("VBSP - Ch??nh s??ch x?? h???i Vi???t Nam").setIdString("VBSP"))
        list.add(CommonEntity().setTitle("VIB - Qu???c T???").setIdString("VIB"))
        list.add(CommonEntity().setTitle("MSB - H??ng H???i").setIdString("MSB"))
        list.add(CommonEntity().setTitle("SCB - S??i G??n").setIdString("SCB"))
        list.add(CommonEntity().setTitle("VDB - Ph??t tri???n Vi???t Nam").setIdString("VDB"))
        list.add(CommonEntity().setTitle("SeABank - ????ng Nam ??").setIdString("SeABank"))
        list.add(CommonEntity().setTitle("OCB - Ph????ng ????ng").setIdString("OCB"))
        list.add(CommonEntity().setTitle("TPBank - Ti??n Phong").setIdString("TPBank"))
        list.add(CommonEntity().setTitle("LienVietPostBank - B??u ??i???n Li??n Vi???t").setIdString("LienVietPostBank"))
        list.add(CommonEntity().setTitle("Eximbank - Xu???t Nh???p Kh???u").setIdString("Eximbank"))
        list.add(CommonEntity().setTitle("PVcomBank - ?????i Ch??ng Vi???t Nam").setIdString("PVcomBank"))
        list.add(CommonEntity().setTitle("Woori - Woori Vi???t Nam").setIdString("Woori"))
        list.add(CommonEntity().setTitle("Bac A Bank - B???c ??").setIdString("BacABank"))
        list.add(CommonEntity().setTitle("HSBC - HSBC Vi???t Nam").setIdString("HSBC"))
        list.add(CommonEntity().setTitle("SCBVL - Standard Chartered Vi???t Nam").setIdString("SCBVL"))
        list.add(CommonEntity().setTitle("PBVN - Public Bank Vi???t Nam").setIdString("PBVN"))
        list.add(CommonEntity().setTitle("ABBANK - An B??nh").setIdString("ABBANK"))
        list.add(CommonEntity().setTitle("SHBVN - Shinhan Vi???t Nam").setIdString("SHBVN"))
        list.add(CommonEntity().setTitle("VietABank - Vi???t ??").setIdString("VietABank"))
        list.add(CommonEntity().setTitle("DongA Bank - ????ng ??").setIdString("DongABank"))
        list.add(CommonEntity().setTitle("UOB - UOB Vi???t Nam").setIdString("UOB"))
        list.add(CommonEntity().setTitle("Vietbank - Vi???t Nam Th????ng T??n").setIdString("Vietbank"))
        list.add(CommonEntity().setTitle("Nam A Bank - Nam ??").setIdString("NamABank"))
        list.add(CommonEntity().setTitle("NCB - Qu???c d??n").setIdString("NCB"))
        list.add(CommonEntity().setTitle("OceanBank - ?????i D????ng").setIdString("OceanBank"))
        list.add(CommonEntity().setTitle("CIMB - CIMB Vi???t Nam").setIdString("CIMB"))
        list.add(CommonEntity().setTitle("Viet Capital Bank - B???n Vi???t").setIdString("VietCapitalBank"))
        list.add(CommonEntity().setTitle("Kienlongbank - Ki??n Long").setIdString("Kienlongbank"))
        list.add(CommonEntity().setTitle("IVB - Indovina").setIdString("IVB"))
        list.add(CommonEntity().setTitle("BAOVIET Bank - B???o Vi???t").setIdString("BAOVIETBank"))
        list.add(CommonEntity().setTitle("SAIGONBANK - S??i G??n C??ng Th????ng").setIdString("SAIGONBANK"))
        list.add(CommonEntity().setTitle("Co-opBank - H???p t??c x?? Vi???t Nam").setIdString("CoopBank"))
        list.add(CommonEntity().setTitle("GPBank - D???u kh?? to??n c???u").setIdString("GPBank"))
        list.add(CommonEntity().setTitle("VRB - Li??n doanh Vi???t Nga").setIdString("VRB"))
        list.add(CommonEntity().setTitle("CB - X??y d???ng").setIdString("CB"))
        list.add(CommonEntity().setTitle("PG Bank - X??ng d???u Petrolimex").setIdString("PGBank"))
        list.add(CommonEntity().setTitle("ANZVL - ANZ Vi???t Nam").setIdString("ANZVL"))
        list.add(CommonEntity().setTitle("HLBVN - Hong Leong Vi???t Nam").setIdString("HLBVN"))
        return list
    }
}