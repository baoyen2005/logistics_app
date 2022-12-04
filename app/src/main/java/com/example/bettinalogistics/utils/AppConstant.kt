package com.example.bettinalogistics.utils

import com.example.bettinalogistics.model.Voucher

class AppConstant {
    companion object {
        @JvmStatic
        val STORAGE_REQUEST_CODE = 1000

        @JvmStatic
        val PROFILE_PATH = "/Profile/image_profile.jpg"

        const val CHOOSE_IMAGE  = "Vui lòng chọn ảnh của bạn!"
        const val CANCEL_DATE_PICKER_VAL = "bị hủy"
        const val DATE_OF_BIRTH_NULL_VAL = "Date of birth is null"
        const val SIGN_UP_FAIL_VAL = "Đăng ký thất bại!"
        const val SIGN_IN_FAIL_VAL = "Đăng nhập thất bại!"

        const val TAG = "app_logistics"
        const val KEY_SEARCH_HISTORY = "keySearchLogistics"

        // firebase
        const val ORDER_COLLECTION = "orders"
        const val ORDER_IMAGE_STORAGE = "product_images"
        const val USER_COLLECTION = "users"
        const val USER_COMPANY_COLLECTION = "user_company"
        const val TERM_PRODUCT_LIST = "addedProductList"
        const val SEND_NOTIFICATION = "notification"
        const val TOKEN_OTT = "tokenOtt"

        // van chuyen
        const val LAT_HUU_NGHI = 21.27419975
        const val LON_HUU_NGHI = 106.4634293
        const val LAT_CANG_HAI_PHONG = 20.86774
        const val LON_CANG_HAI_PHONG = 106.69179
        const val LAT_BANG_TUONG_TRUNG_QUOC = 21.85633
        const val LON_BANG_TUONG_TRUNG_QUOC = 106.762038
        const val LAT_CANG_THUONG_HAI_TRUNG_QUOC = 31.224361
        const val LON_CANG_THUONG_HAI_TRUNG_QUOC = 121.469170
        const val LAT_CANG_BUSAN = 35.166668
        const val LON_CANG_BUSAN = 129.066666
        const val SERVICE_DUONG_BO_TRUNG = 20000000
        const val SERVICE_DUONG_BIEN_TRUNG = 24000000
        const val SERVICE_DUONG_BIEN_HAN = 30000000

        // list voucher
        fun allListVoucher(): List<Voucher> {
            val list = ArrayList<Voucher>()
            list.add(
                Voucher(
                    id ="NEW_VOUCHER",
                    name = "NEW VOUCHER 2%",
                    amount = 1,
                    predicate ="Chỉ dành cho khách hàng mới đăng ký lần đầu",
                    discount = 0.02
                )
            )
            list.add(
                Voucher(
                    id ="FIVE_ORDER_VOUCHER",
                    name = "FIVE ORDER VOUCHER 1%",
                    amount = 2,
                    predicate ="Dành cho khách hàng đặt hàng trên 5 lần",
                    discount = 0.01
                )
            )
            list.add(
                Voucher(
                    id ="BIRTHDAY_VOUCHER",
                    name = "BIRTHDAY VOUCHER 1.5%",
                    amount = 1,
                    predicate ="Sinh nhật công ty",
                    discount = 0.02
                )
            )
            return list
        }
    }
}