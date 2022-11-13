package com.example.bettinalogistics.utils

import com.example.bettinalogistics.R
import com.example.bettinalogistics.model.PlaceModel

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

        val placesName =
            listOf<PlaceModel>(
                PlaceModel(1, R.drawable.xetai, "Restaurant", "restaurant"),
                PlaceModel(2, R.drawable.maybay, "ATM", "atm"),
                PlaceModel(3, R.drawable.tau, "Gas", "gas_station"),
                PlaceModel(4, R.drawable.duongbien, "Groceries", "supermarket"),
                PlaceModel(5, R.drawable.duongbo, "Hotels", "hotel"),
                PlaceModel(6, R.drawable.hangkhong, "Pharmacies", "pharmacy"),
            )
    }
}