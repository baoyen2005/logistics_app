package com.example.bettinalogistics.data

import android.content.ContentValues.TAG
import android.util.Log
import com.example.baseapp.di.Common
import com.example.bettinalogistics.R
import com.example.bettinalogistics.model.AddressData
import com.example.bettinalogistics.model.Result
import com.example.bettinalogistics.network.RetrofitApi
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface GoogleMapRepo {
    suspend fun calculateDistance(orderAddress: AddressData,onComplete: ((String?) ->Unit)?)
}

class GoogleMapRepoImpl : GoogleMapRepo{
    override suspend fun calculateDistance(orderAddress: AddressData, onComplete: ((String?) ->Unit)?) {
        val BASE_URL = "https://maps.googleapis.com/"
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .baseUrl(BASE_URL)
            .build()

        val apiInterface =  retrofit.create(RetrofitApi::class.java)
        val origin = orderAddress.originAddressLat.toString()+","+ orderAddress.originAddressLon.toString()
        val dest = orderAddress.destinationLat.toString()+","+ orderAddress.destinationLon.toString()
        apiInterface.getDistance(
            Common.currentActivity!!.getString(R.string.API_KEY),origin,dest)
            .subscribeOn(io.reactivex.schedulers.Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object: SingleObserver<Result> {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onSuccess(t: Result) {
                    if(t.rows == null || t.rows!!.isEmpty()){
                        onComplete?.invoke(null)
                        return
                    }
                    else if(t.rows!![0].elements == null || t.rows!![0].elements!!.isEmpty()){
                        onComplete?.invoke(null)
                        return
                    }
                    else if(t.rows!![0].elements!![0].distance == null){
                        onComplete?.invoke(null)
                        return
                    }
                    else {
                        onComplete?.invoke(t.rows!![0].elements!![0].distance.toString())
                     }
                }

                override fun onError(e: Throwable) {
                    Log.d(TAG, "onError: $e")
                }
            })
    }
}