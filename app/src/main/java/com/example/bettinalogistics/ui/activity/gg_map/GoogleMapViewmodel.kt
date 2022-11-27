package com.example.bettinalogistics.ui.activity.gg_map

import android.content.ContentValues
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.baseapp.BaseViewModel
import com.example.bettinalogistics.data.GoogleMapRepo
import com.example.bettinalogistics.model.OrderAddress
import com.google.android.gms.maps.model.LatLng
import com.google.maps.DirectionsApi
import com.google.maps.DirectionsApiRequest
import com.google.maps.GeoApiContext
import com.google.maps.android.SphericalUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.UnsupportedEncodingException

class GoogleMapViewmodel(val googleMapRepo: GoogleMapRepo) : BaseViewModel() {
    var latLonOriginAddress: LatLng? = null
    var latLonDestinationAddress: LatLng? = null
    var distance: String? = null
    var addressData: OrderAddress? = null
    var calculateDistanceLiveData = MutableLiveData<String?>()
    fun calculateDistance(orderAddress: OrderAddress) = viewModelScope.launch(Dispatchers.IO) {
        googleMapRepo.calculateDistance(orderAddress) {
           if(!it.isNullOrEmpty()){
               calculateDistanceLiveData.postValue(it)
           }
            else{
               calculateDistanceLiveData.postValue(sendRequest())
           }
        }
    }

    private fun sendRequest() : String?{
        try {
            val latLonOrigin = latLonOriginAddress?.latitude?.let {
                latLonOriginAddress?.longitude?.let { it1 ->
                    LatLng(
                        it,
                        it1
                    )
                }
            }
            val latLonDestination = latLonDestinationAddress?.let {
                latLonDestinationAddress?.longitude?.let { it1 ->
                    LatLng(
                        it.latitude,
                        it1
                    )
                }
            }
            val distance = SphericalUtil.computeDistanceBetween(latLonOrigin, latLonDestination);
            Log.d(ContentValues.TAG, "sendRequest: ${distance / 1000} km")
            return (distance/1000).toString()
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
        return null
    }
    val requestApiLiveData = MutableLiveData<DirectionsApiRequest>()
    fun requestGgApi(context: GeoApiContext) =  viewModelScope.launch(Dispatchers.IO){
        val req = DirectionsApi.getDirections(
            context,
            "${latLonOriginAddress?.latitude ?: ""},${latLonOriginAddress?.longitude ?: ""}",
            "${latLonDestinationAddress?.latitude ?: ""},${latLonDestinationAddress?.longitude ?: ""}"
        )
        requestApiLiveData.postValue(req)
    }
}