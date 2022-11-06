package com.example.bettinalogistics.interfaces

import com.example.bettinalogistics.model.SavedPlaceModel

interface SaveLocationInterface {

    fun onLocationClick(savedPlaceModel: SavedPlaceModel)
}