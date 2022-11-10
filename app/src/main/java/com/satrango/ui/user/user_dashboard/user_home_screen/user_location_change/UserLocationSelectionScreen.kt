package com.satrango.ui.user.user_dashboard.user_home_screen.user_location_change

import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.satrango.R
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivityUserLocationSelectionScreenBinding
import com.satrango.ui.user.user_dashboard.UserDashboardScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.PostJobAddressScreen
import com.satrango.ui.user.user_dashboard.search_service_providers.search_service_provider.SearchServiceProvidersScreen
import com.satrango.utils.*
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*


class UserLocationSelectionScreen : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        var FROM_USER_DASHBOARD = false
        var FROM_USER_POST_JOB_ADDRESS = false
        var FROM_USER_BOOKING_ADDRESS = false
    }

    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private lateinit var marker: Marker
    private lateinit var binding: ActivityUserLocationSelectionScreenBinding
    private lateinit var viewModel: UserLocationChangeViewModel
    private lateinit var progressDialog: ProgressDialog

    private val AUTOCOMPLETE_REQUEST_CODE = 1

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationCallBack: LocationCallback
    private var mMap: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserLocationSelectionScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolBar = binding.root.findViewById<View>(R.id.toolBar)
        val backBtn = toolBar.findViewById<ImageView>(R.id.toolBarBackBtn)
        val backBtnText = toolBar.findViewById<TextView>(R.id.toolBarBackTVBtn)
        backBtnText.text = resources.getString(R.string.back)
        backBtnText.setOnClickListener {
            finish()
            onBackPressed()
        }
        backBtn.setOnClickListener {
            finish()
            onBackPressed()
        }
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text = resources.getString(R.string.my_location)
        val profilePic = toolBar.findViewById<CircleImageView>(R.id.toolBarImage)
        loadProfileImage(profilePic)

        Places.initialize(this, resources.getString(R.string.google_maps_key))

        val autocompleteFragment = supportFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment?
        autocompleteFragment!!.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS))
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                val latLong = LatLng(place.latLng!!.latitude, place.latLng!!.longitude)
                mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(latLong, 16f))
                val markerOptions = MarkerOptions()
                    .position(latLong)
                    .draggable(true)
                marker = mMap!!.addMarker(markerOptions)!!
                latitude = place.latLng!!.latitude
                longitude = place.latLng!!.longitude
//                toast(this@UserLocationSelectionScreen, "TEST2: $latitude, $longitude")
//                fusedLocationProviderClient.removeLocationUpdates(locationCallBack)
            }

            override fun onError(status: Status) {
                Log.e("MAPS ERROR: ", status.statusMessage!!)
                toast(this@UserLocationSelectionScreen, "An error occurred: $status")
            }
        })

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)

        progressDialog = ProgressDialog(this)
        progressDialog.setCancelable(false)
        progressDialog.setMessage("Loading...")

        val factory = ViewModelFactory(UserLocationChangeRepository())
        viewModel = ViewModelProvider(this, factory)[UserLocationChangeViewModel::class.java]

        binding.myLocation.setOnClickListener {
            val fields = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME)
            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                .build(this)
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
        }

        binding.currentLocationBtn.setOnClickListener {
            fetchLocation(this)
        }

        binding.addBtn.setOnClickListener {
            if (latitude != 0.0 && longitude != 0.0) {
                fetchLocationDetails(this, latitude, longitude)
            } else {
                snackBar(binding.addBtn, "Please select the location to Add")
            }
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        if (UserUtils.getLatitude(this).isNotEmpty() && UserUtils.getLongitude(this).isNotEmpty()) {
//            toast(this@UserLocationSelectionScreen, "Maps Ready Data")
            loadLocation(UserUtils.getLatitude(this).toDouble(), UserUtils.getLongitude(this).toDouble())
        } else {
            fetchLocation(this)
        }
    }

    private fun loadLocation(latitude: Double, longitude: Double) {
//        toast(this, "Test: $latitude$longitude")
        val latLong = LatLng(latitude, longitude)
        mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(latLong, 16f))
        val markerOptions = MarkerOptions()
            .position(latLong)
            .draggable(true)
        mMap!!.clear()
        marker = mMap!!.addMarker(markerOptions)!!
        mMap!!.setOnMarkerDragListener(object : OnMarkerDragListener {
            override fun onMarkerDragStart(marker: Marker) {
            }

            override fun onMarkerDragEnd(marker: Marker) {
//                mMap!!.animateCamera(CameraUpdateFactory.newLatLng(marker.position))
            }

            override fun onMarkerDrag(marker: Marker) {
//                Log.i("System out", "onMarkerDrag...")
//                fetchLocationDetails(this@UserLocationSelectionScreen, marker.position.latitude, marker.position.longitude)
            }
        })
    }

    private fun fetchLocation(context: Context) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }

        val locationRequest = LocationRequest().setInterval(2000).setFastestInterval(2000)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        locationCallBack = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                for (location in locationResult.locations) {
                    latitude = location.latitude
                    longitude = location.longitude
//                    toast(this@UserLocationSelectionScreen, "Results Data")
                    loadLocation(latitude, longitude)
                }
            }
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallBack, Looper.myLooper()!!)
    }

    @SuppressLint("SetTextI18n")
    private fun fetchLocationDetails(context: Context, latitude: Double, longitude: Double) {
        try {
            val geoCoder = Geocoder(context, Locale.getDefault())
            val address: List<Address> = geoCoder.getFromLocation(latitude, longitude, 1)
            if (address.isNotEmpty()) {
                val addressName: String = address[0].getAddressLine(0)
//                if (address[0].postalCode.isNullOrBlank()) {
//                    toast(this, "Please select another location")
//                } else {
                var city = "unknown"
                var state = "unknown"
                var country = "unknown"
                var postalCode = "unknown"
                var knownName = "unknown"
                try { city = address.get(0).locality } catch (e: Exception) { }
                try { state = address.get(0).adminArea } catch (e: Exception) { }
                try { country = address.get(0).countryName } catch (e: Exception) { }
                try { postalCode = address.get(0).postalCode } catch (e: Exception) { }
                try { knownName = address.get(0).featureName } catch (e: Exception) { }
                UserUtils.setLatitude(context, latitude.toString())
                UserUtils.setLongitude(context, longitude.toString())
                UserUtils.setCity(context, city)
                UserUtils.setState(context, state)
                UserUtils.setCountry(context, country)
                UserUtils.setPostalCode(context, postalCode)
                UserUtils.setAddress(context, knownName)
                if (FROM_USER_POST_JOB_ADDRESS) {
                    FROM_USER_POST_JOB_ADDRESS = false
                    onBackPressed()
                } else if(FROM_USER_BOOKING_ADDRESS) {
                    FROM_USER_BOOKING_ADDRESS = false
                    onBackPressed()
                } else {
                    if (FROM_USER_DASHBOARD) {
                        startActivity(Intent(this, UserDashboardScreen::class.java))
                    } else {
                        startActivity(Intent(this, SearchServiceProvidersScreen::class.java))
                    }
                }
//                }
            } else {
                toast(this, "Location Details not found. Please select Another Location")
            }

            try {
                fusedLocationProviderClient.removeLocationUpdates(locationCallBack)
            } catch (e: Exception) {

            }
        } catch (e: Exception) {
            Toast.makeText(context, "Please Try with another location. Selected Location Details are not available!", Toast.LENGTH_LONG)
                .show()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            val place = Autocomplete.getPlaceFromIntent(data!!)
            binding.myLocation.setText(place.name)
            latitude = place.latLng!!.latitude
            longitude = place.latLng!!.longitude
        } catch (e: java.lang.Exception) {

        }

//        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
//            when (resultCode) {
//                AutocompleteActivity.RESULT_OK -> {
//                    val place = Autocomplete.getPlaceFromIntent(data!!)
//                    binding.myLocation.setText(place.name)
//                    toast(this, place.latLng!!.latitude.toString() + " | " + place.latLng!!.longitude)
//                }
//                AutocompleteActivity.RESULT_ERROR -> {
//                    val status = Autocomplete.getStatusFromIntent(data!!)
//                    snackBar(binding.addBtn, status.statusMessage!!)
//                }
//                AutocompleteActivity.RESULT_CANCELED -> {
//                    // The user canceled the operation.
//                }
//            }
//            return
//        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

}