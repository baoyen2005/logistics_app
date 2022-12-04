package com.example.bettinalogistics.model

import com.example.bettinalogistics.utils.dateToString
import java.util.*

data class Notification(
    var uid: String? = null,
    var notificationType : String?= null,
    var notificationId : String? = null,
    var contentNoti: String? = null,
    var notiTo: String? = null,
    var order: Order? = null,
    var confirmDate: String = dateToString(Calendar.getInstance().time),
    var timeTransactionEstimate: String? = null
)