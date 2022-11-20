package com.example.bettinalogistics.ui.activity.gg_map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.util.Log
import androidx.core.app.ActivityCompat
import com.example.baseapp.BaseActivity
import com.example.baseapp.view.removeAccentNormalize
import com.example.baseapp.view.setSafeOnClickListener
import com.example.bettinalogistics.R
import com.example.bettinalogistics.databinding.ActivityGoogleMapBinding
import com.example.bettinalogistics.model.AddressData
import com.example.bettinalogistics.model.OrderAddress
import com.example.bettinalogistics.ui.activity.add_new_order.AddAddressTransactionActivity
import com.example.bettinalogistics.utils.AppConstant
import com.example.bettinalogistics.utils.Utils
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.IOException


class GoogleMapActivity : BaseActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener{
    private var mMap: GoogleMap? = null
    private var mapFragment: SupportMapFragment? = null
    override val viewModel: GoogleMapViewmodel by viewModel()
    private lateinit var lastLocation: Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    companion object {
        private const val LOCATION_REQUEST_CODE = 1
    }

    override val binding: ActivityGoogleMapBinding by lazy {
        ActivityGoogleMapBinding.inflate(layoutInflater)
    }

    override fun initView() {
        mapFragment =
            supportFragmentManager.findFragmentById(R.id.mapLocation) as SupportMapFragment?
        binding.edtOriginSearch.requestFocus()
        mapFragment?.getMapAsync(this);
    }

    override fun initListener() {
        binding.btnFindPath.setSafeOnClickListener {
            if (checkValidate()) {
                val addressData = AddressData(
                    originAddress = binding.edtOriginSearch.getContentText(),
                    originAddressLat = viewModel.latLonOriginAddress?.latitude,
                    originAddressLon = viewModel.latLonOriginAddress?.longitude,
                    destinationAddress = binding.edtDestinationSearch.getContentText(),
                    destinationLat = viewModel.latLonDestinationAddress?.latitude,
                    destinationLon = viewModel.latLonDestinationAddress?.longitude
                )
                viewModel.calculateDistance(addressData)
            }
        }
        binding.edtOriginSearch.setOnEdittextDone = { origin ->
            if (origin.isEmpty()) {
                binding.edtOriginSearch.setGoneMessageError()
                binding.edtOriginSearch.setVisibleMessageError(getString(R.string.invalid_field))
            } else {
                binding.edtOriginSearch.setGoneMessageError()
                binding.edtDestinationSearch.setRequestFocusEdittext()
                setUpOriginLatLon()
            }
        }
        binding.edtOriginSearch.onFocusChange = {
            if (!it) {
                val origin = binding.edtOriginSearch.getContentText()
                if (origin.isEmpty()) {
                    binding.edtOriginSearch.requestFocus()
                    binding.edtOriginSearch.setVisibleMessageError(getString(R.string.invalid_field))
                } else {
                    binding.edtOriginSearch.setGoneMessageError()
                    binding.edtDestinationSearch.setRequestFocusEdittext()
                    setUpOriginLatLon()
                }
            }
        }
        binding.edtDestinationSearch.setOnEdittextDone = { destination ->
            if (destination.isNotEmpty()) {
                setUpDestinationLatLon()
            } else {
                binding.edtDestinationSearch.requestFocus()
                binding.edtDestinationSearch.setVisibleMessageError(getString(R.string.invalid_field))
            }
        }
        binding.edtDestinationSearch.onFocusChange = {
            if (!it) {
                val destination = binding.edtDestinationSearch.getContentText()
                if (destination.isNotEmpty()) {
                    binding.edtDestinationSearch.setGoneMessageError()
                    setUpDestinationLatLon()
                } else {
                    binding.edtDestinationSearch.requestFocus()
                    binding.edtDestinationSearch.setVisibleMessageError(getString(R.string.invalid_field))
                }
            }
        }
    }

    private fun setUpOriginLatLon() {
        val locationOrigin = binding.edtOriginSearch.getContentText().removeAccentNormalize()
        var addressList: List<Address>? = null;
        if (locationOrigin.isNotEmpty()) {
            val geocoder = Geocoder(this@GoogleMapActivity);
            try {
                addressList = geocoder.getFromLocationName(locationOrigin, 1);
            } catch (e: IOException) {
                e.printStackTrace();
            }
            val address = addressList?.get(0);
            val latLng = address?.let { LatLng(it.latitude, it.longitude) };
            viewModel.latLonOriginAddress = latLng
            latLng?.let { MarkerOptions().position(it).title(locationOrigin) }
                ?.let { mMap?.addMarker(it) };

            latLng?.let { CameraUpdateFactory.newLatLngZoom(it, 10F) }
                ?.let { mMap?.animateCamera(it) };
        }
    }

    private fun setUpDestinationLatLon() {
        val locationDestination = binding.edtDestinationSearch.getContentText().removeAccentNormalize()
        var addressList: List<Address>? = null;
        if (locationDestination.isNotEmpty()) {
            val geocoder = Geocoder(this@GoogleMapActivity);
            try {
                addressList = geocoder.getFromLocationName(locationDestination, 1);
            } catch (e: IOException) {
                e.printStackTrace();
            }
            val address = addressList?.get(0);
            if(address == null){
                confirm.setNotice(getString(R.string.str_choose_address_again))
            }
           else{
                val latLng = address.let { LatLng(it.latitude, it.longitude) };
                latLng.let { MarkerOptions().position(it).title(locationDestination) }
                    .let { mMap?.addMarker(it) };

                latLng.let { CameraUpdateFactory.newLatLngZoom(it, 10F) }
                    .let { mMap?.animateCamera(it) };
                viewModel.latLonDestinationAddress = latLng
            }
        }
    }

    private fun checkValidate(): Boolean {
        if (binding.edtOriginSearch.getContentText().isEmpty()) {
            binding.edtOriginSearch.requestFocus()
            binding.edtOriginSearch.setVisibleMessageError(getString(R.string.invalid_field))
            return false
        } else if (binding.edtDestinationSearch.getContentText().isEmpty()) {
            binding.edtDestinationSearch.requestFocus()
            binding.edtDestinationSearch.setVisibleMessageError(getString(R.string.invalid_field))
            return false
        }
        return true
    }

    override fun observeData() {
        viewModel.calculateDistanceLiveData.observe(this) {
            if(it != null){
                viewModel.distance = it
                val addressData = AddressData(
                    originAddress = binding.edtOriginSearch.getContentText(),
                    originAddressLat = viewModel.latLonOriginAddress?.latitude,
                    originAddressLon = viewModel.latLonOriginAddress?.longitude,
                    destinationAddress = binding.edtDestinationSearch.getContentText(),
                    destinationLat = viewModel.latLonDestinationAddress?.latitude,
                    destinationLon = viewModel.latLonDestinationAddress?.longitude
                )
                val orderAddress = OrderAddress(addressData, it)
                val i = Intent()
                i.putExtra(AddAddressTransactionActivity.NEW_ADDRESS_ORDER, Utils.g().getJsonFromObject(orderAddress))
                setResult(RESULT_OK, i)
                finish()
            }
        }
    }

    @SuppressLint("PotentialBehaviorOverride")
    override fun onMapReady(p0: GoogleMap) {
        mMap = p0
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mMap?.uiSettings?.isZoomControlsEnabled = true
        mMap?.setOnMarkerClickListener(this)
        setUpMap()
    }

    private fun drawMap() {
        viewModel.latLonOriginAddress?.let {
            MarkerOptions().position(it).title(binding.edtOriginSearch.getContentText())
        }
            ?.let { mMap!!.addMarker(it) }
        viewModel.latLonDestinationAddress?.let {
            MarkerOptions().position(it).title(binding.edtDestinationSearch.getContentText())
        }
            ?.let { mMap!!.addMarker(it) }

        val path: MutableList<LatLng> = ArrayList()

        val context = GeoApiContext.Builder()
            .apiKey(getString(R.string.API_KEY))
            .build()
        val req = DirectionsApi.getDirections(
            context,
            "${viewModel.latLonOriginAddress?.latitude ?: ""},${viewModel.latLonOriginAddress?.longitude ?: ""}",
            "${viewModel.latLonDestinationAddress?.latitude ?: ""},${viewModel.latLonDestinationAddress?.longitude ?: ""}"
        )
        try {
            runBlocking {
                async {
                    val res =   req.await()

                if (res.routes != null && res.routes.isNotEmpty()) {
                    val route = res.routes[0]
                    if (route.legs != null) {
                        for (i in route.legs.indices) {
                            val leg = route.legs[i]
                            if (leg.steps != null) {
                                for (j in leg.steps.indices) {
                                    val step = leg.steps[j]
                                    if (step.steps != null && step.steps.size > 0) {
                                    for (k in step.steps.indices) {
                                        val step1 = step.steps[k]
                                        val points1 = step1.polyline
                                        if (points1 != null) {
                                            //Decode polyline and add points to list of route coordinates
                                            val coords1 = points1.decodePath()
                                            for (coord1 in coords1) {
                                                path.add(LatLng(coord1.lat, coord1.lng))
                                            }
                                        }
                                    }
                                } else {
                                    val points = step.polyline
                                    if (points != null) {
                                        val coords = points.decodePath()
                                        for (coord in coords) {
                                            path.add(LatLng(coord.lat, coord.lng))
                                        }
                                    }
                                    }
                                }
                            }
                        }
                    }
                }
                if (path.size > 0) {
                    val opts = PolylineOptions().addAll(path).color(Color.BLUE).width(5f)
                    mMap!!.addPolyline(opts)
                }

                mMap!!.uiSettings.isZoomControlsEnabled = true
            }
            }
        } catch (ex: Exception) {
            ex.localizedMessage?.let { Log.e(AppConstant.TAG, it) }
        }

    }

    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_REQUEST_CODE
            )
            return
        }
        mMap?.isMyLocationEnabled = true
        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            if (location != null) {
                lastLocation = location
                val currentLatLong = LatLng(location.latitude, location.longitude)
                placeMarkerOnMap(currentLatLong)
                mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLong, 12f))
            }
        }
    }

    private fun placeMarkerOnMap(currentLatLong: LatLng) {
        val markerOptions = MarkerOptions().position(currentLatLong)
        markerOptions.title("$currentLatLong")
        mMap?.addMarker(markerOptions)
    }

    override fun onMarkerClick(p0: Marker) = false
}