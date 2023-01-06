package com.example.bettinalogistics.ui.fragment.user.home

import android.content.Context
import com.denzcoskun.imageslider.models.SlideModel
import com.example.baseapp.BaseViewModel
import com.example.bettinalogistics.R
import com.example.bettinalogistics.model.CommonEntity

class UserHomeViewModel :BaseViewModel() {

    fun getTransportMethodList(context: Context) : List<CommonEntity>{
        val list = ArrayList<CommonEntity>()
        val roadTransport = CommonEntity(R.drawable.duongbo, context.getString(R.string.str_road_transport))
        val seaTransport = CommonEntity(R.drawable.duongbien, context.getString(R.string.str_sea_transport))
        val lclTransport = CommonEntity(R.drawable.hang_le_lcl, context.getString(R.string.str_lcl))
        val fclTransport = CommonEntity(R.drawable.hang_nguyen_cong_fcl, context.getString(R.string.str_fcl_transport))
        list.add(roadTransport)
        list.add(seaTransport)
        list.add(lclTransport)
        list.add(fclTransport)
        return list
    }

    fun getTransportTypeList(context: Context) : List<CommonEntity>{
        val list = ArrayList<CommonEntity>()
        val plane =
            CommonEntity(icon = R.drawable.maybay, title = context.getString(R.string.str_plane))
        val container =
            CommonEntity(R.drawable.container, context.getString(R.string.str_container))
        val truck = CommonEntity(R.drawable.xetai, context.getString(R.string.str_truck))
        val trip = CommonEntity(R.drawable.tau, context.getString(R.string.str_ship))
        list.add(plane)
        list.add(container)
        list.add(truck)
        list.add(trip)
        return list
    }

    fun getSliderModels(context: Context): java.util.ArrayList<SlideModel>{
        var slideModels = java.util.ArrayList<SlideModel>()
        slideModels.add(SlideModel(R.drawable.slide1, context.getString(R.string.str_slider_seaway)))
        slideModels.add(SlideModel(R.drawable.slide3, context.getString(R.string.str_slider_roadway)))
        slideModels.add(SlideModel(R.drawable.slide2, context.getString(R.string.str_slider_flightway)))
        return slideModels
    }
}