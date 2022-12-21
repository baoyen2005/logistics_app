package com.example.bettinalogistics.enums

enum class VoucherDataEnum(val title: String, val discount: Double) {
    NEW_MEMBER("Thành viên mới", 0.01),
    HANG_DONG("Thành viên hạng đồng", 0.05),
    HANG_BAC("Thành viên hạng bạc", 0.06),
    HANG_VANG("Thành viên hạng vàng", 0.1),
}