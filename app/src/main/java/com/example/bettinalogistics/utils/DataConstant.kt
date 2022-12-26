package com.example.bettinalogistics.utils

class DataConstant {
    companion object{
        const val MEMBER_LEVEL = "memberLevel"
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
        const val PRODUCT_SUPPLIER = "supplierCompany"
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
        const val ORDER_STATUS_DELIVERING= "Đang giao"
        const val ORDER_STATUS_PAYMENT_PAID= "Đã thanh toán"
        const val ORDER_STATUS_PAYMENT_WAITING= "Chờ thanh toán"
        const val ORDER_STATUS_CANCEL= "Đã hủy"
        const val ORDER_DATE= "orderDate"
        const val PRODUCT_LIST = "productList"
        const val ORDER_TRANSPORT_TYPE = "typeTransportation"
        const val ORDER_TRANSPORT_METHOD = "methodTransport"
        const val ORDER_USER = "user"
        const val ORDER_CODE= "orderCode"
        const val AMOUNT_BEFORE_DISCOUNT= "amountBeforeDiscount"
        const val AMOUNT_AFTER_DISCOUNT= "amountAfterDiscount"
        const val DISCOUNT= "discount"
        const val ORDER_ID= "id"

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
        const val REQUEST_DATE_NOTIFICATION = "requestDate"
        const val TIME_ESTIMATE_NOTIFICATION = "timeTransactionEstimate"
        const val PERSON_RECEIVER_NOTIFICATION = "notiTo"
        const val NOTIFICATION_TYPE = "notificationType"

        //token
        const val TOKEN_ID = "id"
        const val TOKEN = "token"

        //card
        const val BANK_NAME = "name"
        const val CARD_ID = "id"
        const val ACCOUNT_NUMBER = "accountNumber"
        const val CARD_NUMBER = "cardNumber"
        const val DARE_OF_EXPIRED= "dateOfExpired"

        //track
        const val TRACK_ID = "id"
        const val TRACK_CODE = "trackCode"
        const val TRACK_ADDRESS = "address"
        const val TRACK_STATUS = "status"
        const val TRACK_DATE_UPDATE = "dateUpdate"
        const val TRACK_ORDER_ID = "orderId"

        //payment

        const val PAYMENT_ID = "id"
        const val PAYMENT_IMG_URL = "imgUrlPayment"
        const val PAYMENT_CONTENT = "contentPayment"
        const val PAYMENT_ORDER = "order"
        const val PAYMENT_USER = "user"
        const val PAYMENT_CARD = "card"
        const val PAYMENT_DATE = "datePayment"
        const val PAYMENT_ORDER_ID = "orderId"

        //supplier
        const val COMPANY_EMAIL = "email"
        const val COMPANY_PHONE= "phone"
    }
}