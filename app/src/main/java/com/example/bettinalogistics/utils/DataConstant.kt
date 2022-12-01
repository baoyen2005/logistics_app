package com.example.bettinalogistics.utils

import com.example.bettinalogistics.model.Order
import java.util.*

class DataConstant {
    companion object{
        // user
        const val USER = "user"
        const val USER_ID = "uid"
        const val USER_IMAGE = "image"
        const val USER_FULL_NAME = "fullName"
        const val USER_PHONE = "phone"
        const val USER_DATE_OF_BIRTH = "dateOfBirth"
        const val USER_EMAIL= "email"
        const val USER_PASSWORD= "password"
        const val USER_ADDRESS= "address"
        const val USER_ROLE = "role"
        // order
        const val PRODUCT_NAME = "productName"
        const val PRODUCT_DES = "productDes"
        const val PRODUCT_QUANTITY = "quantity"
        const val PRODUCT_VOLUME = "volume"
        const val PRODUCT_MASS = "mass"
        const val PRODUCT_NUMBER_OF_CARTON = "numberOfCarton"
        const val PRODUCT_IS_ORDER_LCL = "isOrderLCL"
        const val PRODUCT_IMAGE_URL = "imgUri"
        const val PRODUCT_TYPE ="type"
        const val PRODUCT_CONT_TYPE = "contType"
        const val PRODUCT_ID = "productId"
        const val PRODUCT_DOCUMENT_ID= "productDocumentId"
        //
        const val ORDER_ADDRESS = "address"
        const val ORDER_DISTANCE = "distance"
        const val ORIGIN_ADDRESS = "originAdress"
        const val ORIGIN_ADDRESS_LAT = "originAddressLat"
        const val ORIGIN_ADDRESS_LON = "originAddressLon"
        const val DESTINATION_ADDRESS = "destinationAddressLon"
        const val DESTINATION_ADDRESS_LAT = "destinationAddressLon"
        const val DESTINATION_ADDRESS_LON = "destinationAddressLon"
        //
        const val ORDER = "order"
        const val ORDER_COMPANY = "company"
        const val ORDER_STATUS = "statusOrder"
        const val PAYMENT_STATUS = "statusPayment"
        const val ORDER_STATUS_PENDING = "Đang chờ duyệt"
        const val ORDER_STATUS_CONFIRM = "Đã xác nhận"
        const val ORDER_STATUS_DELIVERED= "Đã giao"
        const val ORDER_STATUS_PAYMENT_PAID= "Đã thanh toán"
        const val ORDER_STATUS_PAYMENT_WAITING= "Chờ thanh toán"
        const val ORDER_STATUS_CANCEL= "Đã hủy"
        const val ORDER_DATE= "orderDate"
        const val PRODUCT_LIST = "productList"
        const val ORDER_TRANSPORT_TYPE = "typeTransportation"
        const val ORDER_TRANSPORT_METHOD = "methodTransport"
        const val ORDER_CODE= "orderCode"
        const val AMOUNT_BEFORE_DISCOUNT= "amountBeforeDiscount"
        const val AMOUNT_AFTER_DISCOUNT= "discount"
        const val DISCOUNT= "amountAfterDiscount"

        //user company
        const val COMPANY_ID ="id"
        const val COMPANY_NAME = "name"
        const val COMPANY_ADDRESS = "address"
        const val COMPANY_TEX_CODE= "texCode"
        const val COMPANY_BUSINESS_TYPE = "businessType"

        // notification
        const val CONTENT_NOTIFICATION = "contentNoti"
        const val ID_NOTIFICATION = "notificationId"
        const val ORDER_NOTIFICATION = "order"
        const val CONFIRM_DATE_NOTIFICATION = "confirmDate"
        const val TIME_ESTIMATE_NOTIFICATION = "timeTransactionEstimate"
    }
}