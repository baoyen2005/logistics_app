package com.example.bettinalogistics.model

data class Notification(
    var uid: String? = null,
    var notificationType : String?= null,
    var notificationId : String? = null,
    var contentNoti: String? = null,
    var notiTo: String? = null,
    var order: Order? = null,
    var confirmDate: String? = null,
    var requestDate: String? = null,
    var timeTransactionEstimate: String? = null
)