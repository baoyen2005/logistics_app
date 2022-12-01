package com.example.bettinalogistics.ui.activity.confirm_order

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.baseapp.BaseViewModel
import com.example.baseapp.di.Common
import com.example.bettinalogistics.R
import com.example.bettinalogistics.data.OrderRepository
import com.example.bettinalogistics.model.*
import com.example.bettinalogistics.ui.activity.confirm_order.ConfirmUserInfoOrderAdapter.Companion.TYPE_ITEM_USER
import com.example.bettinalogistics.utils.AppConstant
import com.example.bettinalogistics.utils.DataConstant
import com.example.bettinalogistics.utils.Utils
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.UnsupportedEncodingException
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*

class ConfirmOrderViewModel(val orderRepository: OrderRepository) : BaseViewModel() {
    @SuppressLint("StaticFieldLeak")
    private val context = Common.currentActivity
    var products: List<Product>? = null
    var orderAddress: OrderAddress? = null
    var typeTransport: String? = null
    var methodTransport: String? = null
    var userCompany: UserCompany? = null
    var addOrderTransactionLiveData = MutableLiveData<Boolean>()

    fun addOrderTransaction(
        amountBeforeDiscount: Double,
        discount: Double,
        amountAfterDiscount: Double
    ) =
        viewModelScope.launch(Dispatchers.IO) {
            val code = "BT${Date().time}"
            val order = Order(
                orderCode = code,
                productList = products,
                address = orderAddress,
                company = userCompany,
                amountBeforeDiscount = amountBeforeDiscount,
                discount = discount,
                amountAfterDiscount = amountAfterDiscount,
                typeTransportation = typeTransport,
                methodTransport = methodTransport
            )
            Log.d(TAG, "addOrderTransaction: $products")
            orderRepository.addOrderTransaction(order) {
                addOrderTransactionLiveData.postValue(it)
            }
        }

    fun deleteAddedProducts() =
        viewModelScope.launch(Dispatchers.IO) {
            orderRepository.deleteAddedProduct(products?.size ?: 0)
        }

    fun calculateInternalTruckingFee(): Long {
        var res = 0.0
        products?.forEach { product ->
            val typeSelected = product.type
            if (product.isOrderLCL) {
                when {
                    typeSelected?.descript?.lowercase() == "kg" -> {
                        res = (typeSelected.priceKg ?: 0.0) * (product.mass
                            ?: 0.0) * (product.numberOfCarton ?: 0)
                    }
                    typeSelected?.descript?.lowercase()?.contains("khối") == true
                            && (product.volume ?: 0.0) >= 200 -> {
                        res = (typeSelected.priceM3 ?: 0.0) * (product.volume ?: 0.0)
                    }
                }
            } else {
                val contSelected = product.contType
                when {
                    typeSelected?.descript?.lowercase() == "kg" -> {
                        res = (typeSelected.priceKg
                            ?: 0.0) * (contSelected?.quantity ?: 0.0) * (product.quantity ?: 0)
                    }
                    typeSelected?.descript?.lowercase()?.contains("khối") == true -> {
                        res = (typeSelected.priceM3 ?: 0.0) * (contSelected?.volumeMaxOfCont
                            ?: 0.0) * (product.quantity
                            ?: 0)
                    }
                }
            }
        }
        return res.toLong()
    }

    fun calculateInlandTruckingFee(): Long {
        var result = 0.0
        when {
            typeTransport == context!!.getString(R.string.str_road_transport) &&
                    orderAddress?.originAddress?.lowercase()
                        ?.contains(context.getString(R.string.str_china).lowercase()) == true -> {
                result = calculateDistance(
                    latFrom = orderAddress?.originAddressLat ?: 0.0,
                    lonFrom = orderAddress?.originAddressLon ?: 0.0,
                    latTo = AppConstant.LAT_BANG_TUONG_TRUNG_QUOC,
                    lonTo = AppConstant.LON_BANG_TUONG_TRUNG_QUOC
                ) * 80000 + calculateDistance(
                    latFrom = AppConstant.LAT_HUU_NGHI, lonFrom = AppConstant.LON_HUU_NGHI,
                    latTo = orderAddress?.destinationLat ?: 0.0,
                    lonTo = orderAddress?.destinationLon ?: 0.0
                ) * 50000
            }
            typeTransport == context.getString(R.string.str_sea_transport) &&
                    orderAddress?.originAddress?.lowercase()
                        ?.contains(context.getString(R.string.str_korea).lowercase()) == true -> {
                result = calculateDistance(
                    latFrom = orderAddress?.originAddressLat ?: 0.0,
                    lonFrom = orderAddress?.originAddressLon ?: 0.0,
                    latTo = AppConstant.LAT_CANG_BUSAN, lonTo = AppConstant.LON_CANG_BUSAN
                ) * 70000 + calculateDistance(
                    latFrom = AppConstant.LAT_CANG_HAI_PHONG,
                    lonFrom = AppConstant.LON_CANG_HAI_PHONG,
                    latTo = orderAddress?.destinationLat ?: 0.0,
                    lonTo = orderAddress?.destinationLon ?: 0.0
                ) * 50000
            }
            typeTransport == context.getString(R.string.str_sea_transport) &&
                    orderAddress?.originAddress?.lowercase()
                        ?.contains(context.getString(R.string.str_china).lowercase()) == true -> {
                result = calculateDistance(
                    latFrom = orderAddress?.originAddressLat ?: 0.0,
                    lonFrom = orderAddress?.originAddressLon ?: 0.0,
                    latTo = AppConstant.LAT_CANG_THUONG_HAI_TRUNG_QUOC,
                    lonTo = AppConstant.LON_CANG_THUONG_HAI_TRUNG_QUOC
                ) * 80000 + calculateDistance(
                    latFrom = AppConstant.LAT_CANG_HAI_PHONG,
                    lonFrom = AppConstant.LON_CANG_HAI_PHONG,
                    latTo = orderAddress?.destinationLat ?: 0.0,
                    lonTo = orderAddress?.destinationLon ?: 0.0
                ) * 50000
            }
        }
        return result.toLong()
    }

    fun getServiceFee(): Int {
        when {
            typeTransport == context!!.getString(R.string.str_road_transport) -> {
                return AppConstant.SERVICE_DUONG_BO_TRUNG
            }
            typeTransport == context.getString(R.string.str_sea_transport) &&
                    orderAddress?.originAddress?.lowercase()
                        ?.contains(context.getString(R.string.str_china).lowercase()) == true -> {
                return AppConstant.SERVICE_DUONG_BIEN_TRUNG
            }
            typeTransport == context.getString(R.string.str_sea_transport) &&
                    orderAddress?.originAddress?.lowercase()
                        ?.contains(context.getString(R.string.str_korea).lowercase()) == true -> {
                return AppConstant.SERVICE_DUONG_BIEN_TRUNG
            }
        }
        return 0
    }

    private fun calculateDistance(
        latFrom: Double,
        lonFrom: Double,
        latTo: Double,
        lonTo: Double
    ): Double {
        try {
            val latLonOrigin = LatLng(latFrom, lonFrom)
            val latLonDestination = LatLng(latTo, lonTo)
            val distance = SphericalUtil.computeDistanceBetween(latLonOrigin, latLonDestination);
            Log.d(ContentValues.TAG, "calculateDistance: ${distance / 1000} km")
            return roundOffDecimal((distance / 1000))
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
        return 0.0
    }

    private fun roundOffDecimal(number: Double): Double {
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.CEILING
        return df.format(number).toDouble()
    }

    private fun getListUserInfoConfirm(
    ): ArrayList<CommonEntity> {
        val list = ArrayList<CommonEntity>()
        list.add(
            CommonEntity(
                header = context!!.getString(R.string.str_user_info),
                typeLayout = ConfirmUserInfoOrderAdapter.TYPE_HEADER
            )
        )
        val companyName =
            CommonEntity(
                title = context!!.getString(R.string.str_company_name),
                descript = userCompany?.name ?: ""
            ).setTypeLayout(TYPE_ITEM_USER)
        val userName = CommonEntity(
            title = context.getString(R.string.str_user_name), descript = Utils.g().getDataString(
                DataConstant.USER_FULL_NAME
            )
        ).setTypeLayout(TYPE_ITEM_USER)
        val userPhone = CommonEntity(
            title = context.getString(R.string.str_phone_contact),
            descript = Utils.g().getDataString(
                DataConstant.USER_PHONE
            )
        ).setTypeLayout(TYPE_ITEM_USER)
        val origin = CommonEntity(
            title = context.getString(R.string.str_origin_address),
            descript = orderAddress?.originAddress ?: ""
        ).setTypeLayout(TYPE_ITEM_USER)
        val destination = CommonEntity(
            title = context.getString(R.string.str_destination_address),
            descript = orderAddress?.destinationAddress ?: ""
        ).setTypeLayout(TYPE_ITEM_USER)
        list.add(companyName)
        list.add(userName)
        list.add(userPhone)
        list.add(origin)
        list.add(destination)
        return list
    }

    fun getListInfoConfirm(): ArrayList<Any> {
        val list = ArrayList<Any>()
        list.addAll(getListUserInfoConfirm())
        list.add(
            CommonEntity(
                header = context!!.getString(R.string.str_product_info),
                typeLayout = ConfirmUserInfoOrderAdapter.TYPE_HEADER
            )
        )
        list.add(CommonEntity().setTypeLayout(ConfirmUserInfoOrderAdapter.TYPE_LINE))
        products?.forEach {
            val orderConfirm = ConfirmOrder(
                transportType = typeTransport, transportMethod = methodTransport,
                product = it, amount = products?.size ?: 0
            )
            list.add(orderConfirm)
        }
        return list
    }
}