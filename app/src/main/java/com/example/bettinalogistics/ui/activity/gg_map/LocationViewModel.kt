package com.example.bettinalogistics.ui.activity.gg_map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bettinalogistics.data.AppRepo
import com.example.nearmekotlindemo.models.googlePlaceModel.GooglePlaceModel
import com.google.firebase.auth.AuthCredential
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class LocationViewModel : ViewModel() {

    private val repo = AppRepo()

    fun getNearByPlace(url: String) = repo.getPlaces(url)

    fun removePlace(userSavedLocationId: ArrayList<String>) = repo.removePlace(userSavedLocationId)

    fun addUserPlace(googlePlaceModel: GooglePlaceModel, userSavedLocationId: ArrayList<String>) =
        repo.addUserPlace(googlePlaceModel, userSavedLocationId)

    suspend fun getUserLocationId(): ArrayList<String> {

        return withContext(viewModelScope.coroutineContext) {
            val data = async { repo.getUserLocationId() }
            data
        }.await()
    }

    fun getDirection(url: String) = repo.getDirection(url)

    fun getUserLocations() = repo.getUserLocations()

    fun updateName(name: String) = repo.updateName(name)


    fun confirmEmail(authCredential: AuthCredential) = repo.confirmEmail(authCredential)

    fun updateEmail(email: String) = repo.updateEmail(email)

    fun updatePassword(password: String) = repo.updatePassword(password)
}