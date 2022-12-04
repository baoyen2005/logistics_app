package com.example.bettinalogistics.model

import com.google.gson.annotations.SerializedName

data class OttResponse(
    @SerializedName("code")
    var code : String?,
    @SerializedName("message")
    var message : String?,
    @SerializedName("data")
    var data : String?,
    @SerializedName("timestamp")
    var timestamp : String?,
    @SerializedName("mac")
    var mac : String?,
)