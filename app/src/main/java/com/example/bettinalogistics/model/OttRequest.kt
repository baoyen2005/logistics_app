package com.example.bettinalogistics.model

import com.google.gson.annotations.SerializedName

data class OttRequest(
    @SerializedName("requestId")
    var requestId: String?,
    @SerializedName("appId")
    var appId: String? = "ott-firebase",
    @SerializedName("serialNumbers")
    var serialNumbers: List<String>?,
    @SerializedName("title")
    var title: String?,
    @SerializedName("notificationType")
    var notificationType: String?,
    @SerializedName("content")
    var content: String?,
    @SerializedName("versionApp")
    var versionApp: String? = "1.0.0",
    @SerializedName("required")
    var required: String? = "1",
    @SerializedName("mac")
    var mac: String? = "xxxxx-xxxxx",
)