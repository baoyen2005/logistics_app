package com.example.bettinalogistics.ui.activity.gg_map

import android.content.Context
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import com.example.baseapp.BaseActivity
import com.example.baseapp.view.GroupEditTextView
import com.example.bettinalogistics.R
import com.example.bettinalogistics.databinding.ActivityGoogleMapBinding
import com.example.bettinalogistics.model.Result
import com.example.bettinalogistics.network.RetrofitApi
import com.example.bettinalogistics.ui.activity.login.LoginViewModel
import com.example.bettinalogistics.utils.AppConstant.Companion.TAG
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.libraries.places.api.net.PlacesClient
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_google_map.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase
import org.osmdroid.util.GeoPoint
import org.osmdroid.util.MapTileIndex
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException


class GoogleMapActivity : BaseActivity(), OnMapReadyCallback {


    private var mMap: GoogleMap? = null

    // creating a variable
    // for search view.
    private var edtOriginDestinationSearch: GroupEditTextView? = null
    var edtDestinationDestinationSearch: GroupEditTextView? = null
    private var apiInterface : RetrofitApi? = null

    private var placeAdapter: PlaceArrayAdapter? = null
    private lateinit var mPlacesClient: PlacesClient

    override val viewModel: LoginViewModel by viewModel()

    override val binding: ActivityGoogleMapBinding by lazy {
        ActivityGoogleMapBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_google_map);

       val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        binding.edtOriginDestinationSearch.setOnEdittextDone =  {
            val  location = binding.edtOriginDestinationSearch.getValueText()
            var addressList: List<Address>? = null;

            // checking if the entered location is null or not.
            if (location != null || location.equals("")) {
                // on below line we are creating and initializing a geo coder.
                val geocoder = Geocoder(this@GoogleMapActivity);
                try {
                    addressList = geocoder.getFromLocationName(location, 1);
                } catch (e: IOException) {
                    e.printStackTrace();
                }
                val address = addressList?.get(0);
                val latLng = address?.let { LatLng(it.getLatitude(), address.getLongitude()) };

                latLng?.let { MarkerOptions().position(it).title(location) }
                    ?.let { mMap?.addMarker(it) };

                // below line is to animate camera to that position.
                latLng?.let { CameraUpdateFactory.newLatLngZoom(it, 10F) }
                    ?.let { mMap?.animateCamera(it) };
            }
        }
//        val ctx: Context = applicationContext
//        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))
//        val map = findViewById<View>(R.id.map) as MapView
//        map.tileProvider.clearTileCache()
//        Configuration.getInstance().cacheMapTileCount = 12.toShort()
//        Configuration.getInstance().cacheMapTileOvershoot = 12.toShort()
//        // Create a custom tile source
//        // Create a custom tile source
//        map.setTileSource(object : OnlineTileSourceBase("", 1, 20, 512, ".png", arrayOf("https://a.tile.openstreetmap.org/")) {
//            override fun getTileURLString(pMapTileIndex: Long): String {
//                return (baseUrl
//                        + MapTileIndex.getZoom(pMapTileIndex)
//                        + "/" + MapTileIndex.getX(pMapTileIndex)
//                        + "/" + MapTileIndex.getY(pMapTileIndex)
//                        + mImageFilenameEnding)
//            }
//        })

//        map.setMultiTouchControls(true)
//        val mapController: IMapController = map.getController()
//        val startPoint: GeoPoint
//        startPoint = GeoPoint(51.0, 4.0)
//        mapController.setZoom(11.0)
//        mapController.setCenter(startPoint)
//        val context: Context = this
//        map.invalidate()
//        createmarker()
        mapFragment?.getMapAsync(this);
        khoitao()
        /*
        Places.initialize(this, getString(R.string.API_KEY))
        mPlacesClient = Places.createClient(this)

        val mapFragment: SupportMapFragment? = supportFragmentManager.findFragmentById(R.id.mapLocation) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        placeAdapter = PlaceArrayAdapter(this, R.layout.layout_item_places, mPlacesClient)
        autoCompleteEditText.setAdapter(placeAdapter)

        autoCompleteEditText.onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->
            val place = parent.getItemAtPosition(position) as PlaceDataModel
            autoCompleteEditText.apply {
                setText(place.fullText)
                setSelection(autoCompleteEditText.length())
            }
        }

         */
    }

    override fun initView() {
    }

    override fun initListener() {

    }

    override fun observeData() {

    }


//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        when (resultCode) {
//            Activity.RESULT_OK -> {
//                data?.let {
//                    val place = Autocomplete.getPlaceFromIntent(data)
//                    Log.i(TAG, "Place: ${place.name}, ${place.id}")
//                }
//            }
//            AutocompleteActivity.RESULT_ERROR -> {
//                // TODO: Handle the error.
//                data?.let {
//                    val status = Autocomplete.getStatusFromIntent(data)
//                    Log.i(TAG, status.statusMessage ?: "")
//                }
//            }
//            Activity.RESULT_CANCELED -> {
//                // The user canceled the operation.
//            }
//        }
//        return
//
//    }

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
    /*
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
*/
    }

}