package com.example.bettinalogistics.ui.activity.gg_map

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.example.baseapp.view.EditText
import com.example.bettinalogistics.R
import com.example.bettinalogistics.model.Result
import com.example.bettinalogistics.network.RetrofitApi
import com.example.bettinalogistics.utils.AppConstant.Companion.TAG
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class GoogleMapActivity : FragmentActivity(), OnMapReadyCallback {


    private var mMap: GoogleMap? = null

    // creating a variable
    // for search view.
    var searchView: EditText? = null
    var apiInterface : RetrofitApi? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_google_map);
        searchView = findViewById(R.id.svOriginDestinationSearch)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        /*
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                val  location = searchView?.query.toString();

                // below line is to create a list of address
                // where we will store the list of all address.
                var addressList: List<Address>? = null;

                // checking if the entered location is null or not.
                if (location != null || location.equals("")) {
                    // on below line we are creating and initializing a geo coder.
                    val geocoder = Geocoder(this@GoogleMapActivity);
                    try {
                        // on below line we are getting location from the
                        // location name and adding that location to address list.
                        addressList = geocoder.getFromLocationName(location, 1);
                    } catch (e: IOException) {
                        e.printStackTrace();
                    }
                    // on below line we are getting the location
                    // from our list a first position.
                    val address = addressList?.get(0);

                    // on below line we are creating a variable for our location
                    // where we will add our locations latitude and longitude.
                    val latLng = address?.let { LatLng(it.getLatitude(), address.getLongitude()) };

                    // on below line we are adding marker to that position.
                    latLng?.let { MarkerOptions().position(it).title(location) }
                        ?.let { mMap?.addMarker(it) };

                    // below line is to animate camera to that position.
                    latLng?.let { CameraUpdateFactory.newLatLngZoom(it, 10F) }
                        ?.let { mMap?.animateCamera(it) };
                }
                return false;
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false;
            }

        })

         */
        // at last we calling our map fragment to update.
        Places.initialize(applicationContext,getString(R.string.API_KEY))
        searchView?.isFocusable = false
        searchView?.setOnClickListener {
            val  fieldList: List<Place.Field>  =
                listOf(Place.Field.ADDRESS,
                Place.Field.LAT_LNG, Place.Field.NAME)
            val intent = Autocomplete.IntentBuilder(
                AutocompleteActivityMode.OVERLAY,fieldList).build(this)
            startActivityForResult(intent, 100)
        }

        mapFragment?.getMapAsync(this);
        khoitao()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 100 && resultCode == Activity.RESULT_OK){
            val place = data?.let { Autocomplete.getPlaceFromIntent(it) }
            searchView?.setText(place?.address)
            Log.d(TAG, "onActivityResult: name =  ${place?.name}")
            Log.d(TAG, "onActivityResult: lat , lon =  ${place?.latLng}")
        }
        else if(resultCode == AutocompleteActivity.RESULT_ERROR){
            val status = data?.let { Autocomplete.getStatusFromIntent(it) }
            Log.d(TAG, "onActivityResult: status  = ${status?.statusMessage}")
        }

    }

    private fun khoitao() {
        val BASE_URL = "https://maps.googleapis.com/"
        var retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .baseUrl(BASE_URL)
            .build()

        apiInterface =  retrofit.create(RetrofitApi::class.java)
        tinhtoan("21.030653"+","+"105.847130", "20.351387"+","+"105.221214")
    }

    fun tinhtoan(origin: String, dest: String){
        apiInterface?.getDistance(getString(R.string.API_KEY),origin,dest,"false",
        "metric","bicycling")
            ?.subscribeOn(io.reactivex.schedulers.Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe(object: SingleObserver<Result> {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onSuccess(t: Result) {
                    Log.d(TAG, "onSuccess: tinh toan aaaa success= " +
                            "${t.origin_address?.get(0)} \n" +
                            " ${t.destination_address?.get(0)} \n" )
                        //    "total distance = ${t.rows?.get(0)?.elements?.get(0)?.distance?.text}" )
                }

                override fun onError(e: Throwable) {
                    
                }

            })
    }

    override fun onMapReady(p0: GoogleMap) {
        mMap = p0

        val barcelona = LatLng(41.385064, 2.173403)
        mMap!!.addMarker(MarkerOptions().position(barcelona).title("Marker in Barcelona"))

        val madrid = LatLng(40.416775, -3.70379)
        mMap!!.addMarker(MarkerOptions().position(madrid).title("Marker in Madrid"))

        val zaragoza = LatLng(41.648823, -0.889085)

        //Define list to get all latlng for the route

        //Define list to get all latlng for the route
        val path: MutableList<LatLng> = ArrayList()


        //Execute Directions API request


        //Execute Directions API request
        val context = GeoApiContext.Builder()
            .apiKey(getString(R.string.API_KEY))
            .build()
        val req = DirectionsApi.getDirections(context, "41.385064,2.173403", "40.416775,-3.70379")
        try {
            val res = req.await()

            //Loop through legs and steps to get encoded polylines of each step
            if (res.routes != null && res.routes.size > 0) {
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
                                        //Decode polyline and add points to list of route coordinates
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
        } catch (ex: Exception) {
            ex.localizedMessage?.let { Log.e(TAG, it) }
        }

        //Draw the polyline

        //Draw the polyline
        if (path.size > 0) {
            val opts = PolylineOptions().addAll(path).color(Color.BLUE).width(5f)
            mMap!!.addPolyline(opts)
        }

        mMap!!.uiSettings.isZoomControlsEnabled = true

        mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(zaragoza, 6f))
    }

}