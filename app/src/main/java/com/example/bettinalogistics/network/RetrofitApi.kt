package com.example.bettinalogistics.network

import com.example.bettinalogistics.model.Result
import com.example.bettinalogistics.model.googlePlaceModel.directionPlaceModel.DirectionResponseModel
import com.example.nearmekotlindemo.models.googlePlaceModel.GoogleResponseModel
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface RetrofitApi {

    @GET
    suspend fun getNearByPlaces(@Url url: String): Response<GoogleResponseModel>

    @GET
    suspend fun getDirection(@Url url: String): Response<DirectionResponseModel>

    @GET("maps/api/distancematrix/json")
    fun getDistance(@Query("key") key: String,
                    @Query("origins") origion:String,
                    @Query("destinations") destinations:String,
                    @Query("sensor") sensor:String,
                    @Query("units") units:String,
                    @Query("mode") mode:String,): Single<Result>
}