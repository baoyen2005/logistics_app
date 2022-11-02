package com.example.bettinalogistics.utils

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

        // firebase
        const val ORDER_COLLECTION = "orders"
        const val ORDER_IMAGE_STORAGE = "product_images"
        const val USER_COLLECTION = "users"
    }
}