package com.example.bettinalogistics.data

import com.example.bettinalogistics.model.OttRequest
import com.example.bettinalogistics.model.OttResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface OttFirebaseApi {
    @POST("notification/send")
    fun sendOttFirebase(@Body ottRequest: OttRequest) : Call<OttResponse>
}