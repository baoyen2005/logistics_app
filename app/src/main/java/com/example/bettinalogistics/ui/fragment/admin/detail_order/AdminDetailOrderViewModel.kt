package com.example.bettinalogistics.ui.fragment.admin.detail_order

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.baseapp.BaseViewModel
import com.example.baseapp.UtilsBase
import com.example.bettinalogistics.R
import com.example.bettinalogistics.data.CardRepository
import com.example.bettinalogistics.data.OTTFirebaseRepo
import com.example.bettinalogistics.data.OrderRepository
import com.example.bettinalogistics.di.AppData
import com.example.bettinalogistics.model.*
import com.example.bettinalogistics.ui.fragment.user.detail_order.DetailOrderAdapter.Companion.TYPE_HEADER
import com.example.bettinalogistics.ui.fragment.user.detail_order.DetailOrderAdapter.Companion.TYPE_ITEM
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailAdminOrderViewModel(
    val orderRepo: OrderRepository,
    val ottFirebaseRepo: OTTFirebaseRepo,
    val cardRepository: CardRepository
) : BaseViewModel() {
    var order: Order? = null
    var isCancel: Boolean = false
    var allTokenList = mutableListOf<TokenOtt>()

    fun getListDetailOrderCommonEntity(
        order: Order,
        context: Context
    ): List<CommonEntity> {
        val list = ArrayList<CommonEntity>()
        val commonEntityCompanyInfoTitle = CommonEntity(
            header = context.getString(R.string.str_company_info),
            icon = R.drawable.ic_customer_information,
            typeLayout = TYPE_HEADER
        )
        list.add(commonEntityCompanyInfoTitle)
        val commonEntityFullName = CommonEntity(
            title = context.getString(R.string.str_user_name),
            descript = AppData.g().currentUser?.fullName ?: "",
            typeLayout = TYPE_ITEM
        )
        list.add(commonEntityFullName)
        val commonEntityPhone = CommonEntity(
            title = context.getString(R.string.str_phone_contact),
            descript = AppData.g().currentUser?.phone ?: "",
            typeLayout = TYPE_ITEM
        )
        list.add(commonEntityPhone)
        val commonEntityTransactionInfoTitle = CommonEntity(
            header = context.getString(R.string.str_infor_order),
            icon = R.drawable.ic_transaction_code,
            typeLayout = TYPE_HEADER
        )
        list.add(commonEntityTransactionInfoTitle)
        val commonEntityDate = CommonEntity(
            title = context.getString(R.string.str_order_date),
            descript = order.orderDate ?: "",
            typeLayout = TYPE_ITEM
        )
        list.add(commonEntityDate)
        val commonEntityOrderAmount = CommonEntity(
            title = context.getString(R.string.str_order_amount),
            descript = UtilsBase.g()
                .getDotMoney((order.amountBeforeDiscount ?: 0.0).toLong().toString()) + " VND",
            typeLayout = TYPE_ITEM
        )
        list.add(commonEntityOrderAmount)
        val commonEntityOrderAmountAfterDiscount = CommonEntity(
            title = context.getString(R.string.str_order_amount_after_discount),
            descript = UtilsBase.g()
                .getDotMoney((order.amountAfterDiscount ?: 0.0).toLong().toString()) + " VND",
            typeLayout = TYPE_ITEM
        )
        list.add(commonEntityOrderAmountAfterDiscount)
        val commonEntityVoucherCode = CommonEntity(
            title = context.getString(R.string.str_discount),
            descript = order.discount.toString(),
            typeLayout = TYPE_ITEM
        )
        list.add(commonEntityVoucherCode)
        val commonEntityTypeTransport = CommonEntity(
            title = context.getString(R.string.str_type_transaction),
            descript = order.typeTransportation,
            typeLayout = TYPE_ITEM
        )
        list.add(commonEntityTypeTransport)
        val commonEntityMethodTransport = CommonEntity(
            title = context.getString(R.string.str_method_transaction),
            descript = order.methodTransport,
            typeLayout = TYPE_ITEM
        )
        list.add(commonEntityMethodTransport)
        val commonEntityPayment = CommonEntity(
            title = context.getString(R.string.str_payment_status),
            descript = order.statusPayment,
            typeLayout = TYPE_ITEM
        )
        list.add(commonEntityPayment)
        return list
    }

    val updateOrderLiveData = MutableLiveData<Boolean>()
    fun updateOrder(order: Order) = viewModelScope.launch(Dispatchers.IO) {
        orderRepo.updateOrder(order) {
            updateOrderLiveData.postValue(it)
        }
    }
    var sendNotiRequestFirebaseLiveData = MutableLiveData<Boolean>()
    fun sendNotiRequestFirebase(notification: Notification) = viewModelScope.launch(Dispatchers.IO) {
        ottFirebaseRepo.sendNotiRequestFirebase(notification) {
            sendNotiRequestFirebaseLiveData.postValue(it)
        }
    }

    var getOttTokenListLiveData = MutableLiveData<List<TokenOtt>>()
    fun getOttTokenList() = viewModelScope.launch(Dispatchers.IO) {
        ottFirebaseRepo.getAllToken {
            getOttTokenListLiveData.postValue(it)
        }
    }

    var sendOttServerLiveData = MutableLiveData<OttResponse?>()
    fun sendOttServer(ottRequest: OttRequest) = viewModelScope.launch(Dispatchers.IO) {
        ottFirebaseRepo.sendOttServer(ottRequest) {
            sendOttServerLiveData.postValue(it)
        }
    }

    var getPaymentLiveData = MutableLiveData<Payment>()
    fun getPayment(order: Order) = viewModelScope.launch(Dispatchers.IO) {
        cardRepository.getPayment(order) {
            getPaymentLiveData.postValue(it)
        }
    }

    fun getListDetailPaymentOrderCommonEntity(
        payment: Payment,
        context: Context
    ): List<CommonEntity> {
        val list = ArrayList<CommonEntity>()
        val commonEntityCompanyInfoTitle = CommonEntity(
            header = context.getString(R.string.str_info_payment),
            icon = R.drawable.ic_customer_information,
            typeLayout = TYPE_HEADER
        )
        list.add(commonEntityCompanyInfoTitle)
        val commonEntityFullName = CommonEntity(
            title = context.getString(R.string.str_user_name_payment),
            descript = payment.user?.fullName ?: "",
            typeLayout = TYPE_ITEM
        )
        list.add(commonEntityFullName)
        val commonEntityPhone = CommonEntity(
            title = context.getString(R.string.str_phone_contact),
            descript = payment.user?.phone ?: "",
            typeLayout = TYPE_ITEM
        )
        list.add(commonEntityPhone)
        val commonEntityTransactionInfoTitle = CommonEntity(
            header = context.getString(R.string.str_infor_order),
            icon = R.drawable.ic_transaction_code,
            typeLayout = TYPE_HEADER
        )
        list.add(commonEntityTransactionInfoTitle)
        val commonEntityDate = CommonEntity(
            title = context.getString(R.string.str_order_date),
            descript = payment.order?.orderDate ?: "",
            typeLayout = TYPE_ITEM
        )
        list.add(commonEntityDate)
        val commonEntityOrderAmount = CommonEntity(
            title = context.getString(R.string.str_order_amount),
            descript = UtilsBase.g()
                .getDotMoney((payment.order?.amountBeforeDiscount ?: 0.0).toLong().toString()) + " VND",
            typeLayout = TYPE_ITEM
        )
        list.add(commonEntityOrderAmount)
        val commonEntityOrderAmountAfterDiscount = CommonEntity(
            title = context.getString(R.string.str_order_amount_after_discount),
            descript = UtilsBase.g()
                .getDotMoney((payment.order?.amountAfterDiscount ?: 0.0).toLong().toString()) + " VND",
            typeLayout = TYPE_ITEM
        )
        list.add(commonEntityOrderAmountAfterDiscount)
        val commonEntityVoucherCode = CommonEntity(
            title = context.getString(R.string.str_discount),
            descript = payment.order?.discount.toString(),
            typeLayout = TYPE_ITEM
        )
        list.add(commonEntityVoucherCode)
        val commonEntityTypeTransport = CommonEntity(
            title = context.getString(R.string.str_type_transaction),
            descript = payment.order?.typeTransportation,
            typeLayout = TYPE_ITEM
        )
        list.add(commonEntityTypeTransport)
        val commonEntityMethodTransport = CommonEntity(
            title = context.getString(R.string.str_method_transaction),
            descript = payment.order?.methodTransport,
            typeLayout = TYPE_ITEM
        )
        list.add(commonEntityMethodTransport)
        val commonEntityPaymentInfoTitle = CommonEntity(
            header = context.getString(R.string.str_infor_payment),
            icon = R.drawable.ic_transaction_infor,
            typeLayout = TYPE_HEADER
        )
        list.add(commonEntityPaymentInfoTitle)
        val commonEntityPayment = CommonEntity(
            title = context.getString(R.string.str_payment_date),
            descript = payment.datePayment,
            typeLayout = TYPE_ITEM
        )
        list.add(commonEntityPayment)

        val commonEntityContentPayment = CommonEntity(
            title = context.getString(R.string.str_content_payment),
            descript = payment.contentPayment,
            typeLayout = TYPE_ITEM
        )
        list.add(commonEntityContentPayment)
        return list
    }
}