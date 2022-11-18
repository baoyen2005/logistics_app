package com.example.bettinalogistics.ui.activity.gg_map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.directions.route.*
import com.example.baseapp.BaseActivity
import com.example.baseapp.view.setSafeOnClickListener
import com.example.bettinalogistics.R
import com.example.bettinalogistics.databinding.ActivityGoogleMapBinding
import com.example.bettinalogistics.model.AddressData
import com.example.bettinalogistics.model.OrderAddress
import com.example.bettinalogistics.network.RetrofitApi
import com.example.bettinalogistics.ui.activity.add_new_order.AddAddressTransactionActivity
import com.example.bettinalogistics.utils.AppConstant
import com.example.bettinalogistics.utils.Utils
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.IOException


class GoogleMapActivity : BaseActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener,
    GoogleApiClient.OnConnectionFailedListener, RoutingListener {
    private var mMap: GoogleMap? = null
    private var mapFragment: SupportMapFragment? = null
    private var apiInterface: RetrofitApi? = null
    var setOnSearchMapDone: ((Boolean, Boolean) -> Unit)? = null
    override val viewModel: GoogleMapViewmodel by viewModel()
    private lateinit var lastLocation: Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    var myLocation: Location? = null
    var destinationLocation: Location? = null
    protected var start: LatLng? = null
    protected var end: LatLng? = null

    var locationPermission = false

    //polyline object
    private var polylines: ArrayList<Polyline>? = null

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
            drawMap()
            /*
            if (checkValidate()) {
                val addressData = AddressData(
                    originAddress = binding.edtOriginSearch.getValueText(),
                    originAddressLat = viewModel.latLonOriginAddress?.latitude,
                    originAddressLon = viewModel.latLonOriginAddress?.longitude,
                    destinationAddress = binding.edtDestinationSearch.getValueText(),
                    destinationLat = viewModel.latLonDestinationAddress?.latitude,
                    destinationLon = viewModel.latLonDestinationAddress?.longitude
                )
                viewModel.calculateDistance(addressData)
               //  drawMap()
              //  Findroutes(viewModel.latLonOriginAddress,viewModel.latLonDestinationAddress);
            }

             */
        }
        binding.edtOriginSearch.setOnEdittextDone = { origin ->
            if (origin.isEmpty()) {
                binding.edtOriginSearch.requestFocus()
                binding.edtOriginSearch.setVisibleMessageError(getString(R.string.invalid_field))
            } else {
                binding.edtOriginSearch.setGoneMessageError()
                setUpOriginLatLon()
            }
        }
        binding.edtOriginSearch.onFocusChange = {
            if (!it) {
                val origin = binding.edtOriginSearch.getValueText()
                if (origin.isEmpty()) {
                    binding.edtOriginSearch.requestFocus()
                    binding.edtOriginSearch.setVisibleMessageError(getString(R.string.invalid_field))
                } else {
                    binding.edtOriginSearch.setGoneMessageError()
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
                val destination = binding.edtDestinationSearch.getValueText()
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
        val locationOrigin = binding.edtOriginSearch.getValueText()
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
        val locationDestination = binding.edtDestinationSearch.getValueText()
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
        if (binding.edtOriginSearch.getValueText().isEmpty()) {
            binding.edtOriginSearch.requestFocus()
            binding.edtOriginSearch.setVisibleMessageError(getString(R.string.invalid_field))
            return false
        } else if (binding.edtDestinationSearch.getValueText().isEmpty()) {
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
                    originAddress = binding.edtOriginSearch.getValueText(),
                    originAddressLat = viewModel.latLonOriginAddress?.latitude,
                    originAddressLon = viewModel.latLonOriginAddress?.longitude,
                    destinationAddress = binding.edtDestinationSearch.getValueText(),
                    destinationLat = viewModel.latLonDestinationAddress?.latitude,
                    destinationLon = viewModel.latLonDestinationAddress?.longitude
                )
                val orderAddress = OrderAddress(addressData, it.toDouble())
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

    private fun Findroutes(Start: LatLng?, End: LatLng?) {
        if (Start == null || End == null) {
            Toast.makeText(this, "Unable to get location", Toast.LENGTH_LONG).show()
        } else {
            val routing = Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(this)
                .alternativeRoutes(true)
                .waypoints(Start, End)
                .key(getString(R.string.API_KEY)) //also define your api key here.
                .build()
            routing.execute()
        }
    }

    override fun onRoutingFailure(e: RouteException) {
        val parentLayout: View = findViewById(android.R.id.content)
        val snackbar: Snackbar = Snackbar.make(parentLayout, e.toString(), Snackbar.LENGTH_LONG)
        snackbar.show()
//        Findroutes(start,end);
    }

    override fun onRoutingStart() {
        Toast.makeText(this, "Finding Route...", Toast.LENGTH_LONG).show()
    }

    //If Route finding success..
    override fun onRoutingSuccess(route: ArrayList<Route>, shortestRouteIndex: Int) {
        val center = CameraUpdateFactory.newLatLng(start!!)
        val zoom = CameraUpdateFactory.zoomTo(16f)
        polylines?.clear()
        val polyOptions = PolylineOptions()
        var polylineStartLatLng: LatLng? = null
        var polylineEndLatLng: LatLng? = null
        polylines = ArrayList()
        //add route(s) to the map using polyline
        for (i in 0 until route.size) {
            if (i == shortestRouteIndex) {
                polyOptions.color(resources.getColor(R.color.blue_dark))
                polyOptions.width(7f)
                polyOptions.addAll(route[shortestRouteIndex].getPoints())
                val polyline = mMap!!.addPolyline(polyOptions)
                polylineStartLatLng = polyline.points[0]
                val k: Int = polyline.points.size
                polylineEndLatLng = polyline.points[k - 1]
                polylines!!.add(polyline)
            } else {
            }
        }

        //Add Marker on route starting position
        val startMarker = MarkerOptions()
        startMarker.position(polylineStartLatLng!!)
        startMarker.title(binding.edtOriginSearch.getValueText())
        mMap!!.addMarker(startMarker)

        //Add Marker on route ending position
        val endMarker = MarkerOptions()
        endMarker.position(polylineEndLatLng!!)
        endMarker.title(binding.edtDestinationSearch.getValueText())
        mMap!!.addMarker(endMarker)
    }

    override fun onRoutingCancelled() {
        Findroutes(start, end)
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        Findroutes(start, end)
    }

    private fun bitmapFromVector(context: Context, vectorResId: Int): BitmapDescriptor {
        val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)
        vectorDrawable!!.setBounds(
            0,
            0,
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight
        )
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    private fun drawMap() {
        viewModel.latLonOriginAddress?.let {
            MarkerOptions().position(it).title(binding.edtOriginSearch.getValueText())
        }
            ?.let { mMap!!.addMarker(it) }
        viewModel.latLonDestinationAddress?.let {
            MarkerOptions().position(it).title(binding.edtDestinationSearch.getValueText())
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

/*
    override fun onDirectionFinderSuccess(routes: List<Route>) {
        progressDialog?.dismiss();

        for (route in routes) {
            route.startLocation?.let { CameraUpdateFactory.newLatLngZoom(it, 16F) }
                ?.let { mMap?.moveCamera(it) };
            Log.d(TAG, "onDirectionFinderSuccess: duration = ${route.duration?.text}")
            Log.d(TAG, "onDirectionFinderSuccess: distance = ${route.distance?.text}")

            route.startLocation?.let {
                MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location))
                    .title(route.startAddress)
                    .position(it)
            }?.let {
                mMap?.addMarker(
                    it
                )?.let { originMarkers.add(it) }
            };
            route.endLocation?.let {
                MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location))
                    .title(route.endAddress)
                    .position(it)
            }?.let { mMap?.addMarker(it)?.let { destinationMarkers.add(it) } };

            val polylineOptions = PolylineOptions().
            geodesic(true).
            color(Color.BLUE).
            width(10F);

            for (i in route.points?.indices!!)
            polylineOptions.add(route.points!!.get(i));

            mMap?.addPolyline(polylineOptions)?.let { polylinePaths.add(it) };
        }


    }

 */

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
              //  placeMarkerOnMap(currentLatLong)
               // mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLong, 12f))
            }

        }


    }

    private fun placeMarkerOnMap(currentLatLong: LatLng) {
//        val markerOptions = MarkerOptions().position(currentLatLong)
//        markerOptions.title("$currentLatLong")
//        mMap?.addMarker(markerOptions)
    }

    override fun onMarkerClick(p0: Marker) = false
}