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
import com.satrango.ui.user.user_dashboard.search_service_providers.search_service_provider.SearchServiceProvidersScreen
import com.satrango.utils.*
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*


class UserLocationSelectionScreen : AppCompatActivity(), OnMapReadyCallback {

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
        toolBar.findViewById<ImageView>(R.id.toolBarBackBtn).setOnClickListener {
            finish()
            startActivity(Intent(this, SearchServiceProvidersScreen::class.java))
        }
        toolBar.findViewById<TextView>(R.id.toolBarBackTVBtn).setOnClickListener {
            finish()
            startActivity(Intent(this, SearchServiceProvidersScreen::class.java))
        }
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text = resources.getString(R.string.my_location)
        val profilePic = toolBar.findViewById<CircleImageView>(R.id.toolBarImage)
        loadProfileImage(profilePic)

        Places.initialize(this, getString(R.string.google_maps_and_places_api_key))

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
//                toast(this@UserLocationSelectionScreen, "${place.name}, ${place.latLng!!.latitude}, ${place.latLng!!.longitude}, ${place.address}, ${place.attributions}")
            }

            override fun onError(status: Status) {
                toast(this@UserLocationSelectionScreen, "An error occurred: $status")
            }
        })


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
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

//        viewModel.allLocations(this).observe(this, {
//            when (it) {
//                is NetworkResponse.Loading -> {
//                    progressDialog.show()
//                }
//                is NetworkResponse.Success -> {
//                    val locations = it.data!!
//                    val addresses = ArrayList<String>()
//                    locations.forEachIndexed { index, dataX ->
//                        if (locations[index].locality.isNotEmpty()) {
//                            addresses.add("${dataX.locality}, ${dataX.city}, ${dataX.state}, ${dataX.country}, ${dataX.zipcode}")
//                        } else {
//                            addresses.add("${dataX.city}, ${dataX.state}, ${dataX.country}, ${dataX.zipcode}")
//                        }
//                    }
//                    val adapter = ArrayAdapter(
//                        this,
//                        android.R.layout.simple_spinner_dropdown_item,
//                        addresses
//                    )
//                    binding.myLocation.setAdapter(adapter)
//                    binding.myLocation.threshold = 3
//                    binding.myLocation.setOnItemClickListener { parent, view, position, id ->
//                        val latLog = LatLng(
//                            locations[position].latitude.toDouble(),
//                            locations[position].longitude.toDouble()
//                        )
//                        marker.position = latLog
//                        mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(latLog, 16f))
//                        UserUtils.setCity(this, locations[position].city)
//                        UserUtils.setLatitude(this, locations[position].latitude)
//                        UserUtils.setLongitude(this, locations[position].longitude)
//                        UserUtils.setState(this, locations[position].state)
//                        UserUtils.setCountry(this, locations[position].country)
//                        UserUtils.setPostalCode(this, locations[position].zipcode)
//                        UserUtils.setAddress(this, locations[position].locality)
//                    }
//                }
//                is NetworkResponse.Failure -> {
//                    snackBar(binding.addBtn, it.message!!)
//                }
//            }
//        })

        if (PermissionUtils.checkGPSStatus(this) && networkAvailable(this)) {
            fetchLocation(this)
        }

        binding.resetBtn.setOnClickListener {
            finish()
            startActivity(intent)
        }

        binding.addBtn.setOnClickListener {
            fetchLocationDetails(this, latitude, longitude)
            finish()
            startActivity(Intent(this, SearchServiceProvidersScreen::class.java))
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
//        val latLong = LatLng(UserUtils.getLatitude(this).toDouble(), UserUtils.getLongitude(this).toDouble())
//        mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(latLong, 16f))

//        mMap!!.moveCamera(CameraUpdateFactory.newLatLng(latLong))
//        mMap!!.animateCamera(CameraUpdateFactory.zoomTo(15f), 2000, null)

//        val markerOptions = MarkerOptions()
//            .position(latLong)
////                .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_my_location))
//            .draggable(true)
//        marker = mMap!!.addMarker(markerOptions)!!
        mMap!!.setOnMarkerDragListener(object : OnMarkerDragListener {
            override fun onMarkerDragStart(marker: Marker) {
//                Log.d("System out", "onMarkerDragStart..." + marker.position.latitude + "..." + marker.position.longitude)
            }

            override fun onMarkerDragEnd(marker: Marker) {
                mMap!!.animateCamera(CameraUpdateFactory.newLatLng(marker.position))
                fetchLocationDetails(this@UserLocationSelectionScreen, marker.position.latitude, marker.position.longitude)
            }

            override fun onMarkerDrag(arg0: Marker) {
//                Log.i("System out", "onMarkerDrag...")
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
                    val latitude = location.latitude
                    val longitude = location.longitude
                    fetchLocationDetails(context, latitude, longitude)
                }
            }
        }
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallBack,
            Looper.myLooper()!!
        )
    }

    @SuppressLint("SetTextI18n")
    private fun fetchLocationDetails(context: Context, latitude: Double, longitude: Double) {
        try {
            val geoCoder = Geocoder(context, Locale.getDefault())
            val address: List<Address> = geoCoder.getFromLocation(latitude, longitude, 1)
            val addressName: String = address[0].getAddressLine(0)
            val city: String = address[0].locality
            val state: String = address[0].adminArea
            val country: String = address[0].countryName
            val postalCode: String = address[0].postalCode
            val knownName: String = address[0].featureName
            fusedLocationProviderClient.removeLocationUpdates(locationCallBack)
            UserUtils.setLatitude(context, latitude.toString())
            UserUtils.setLongitude(context, longitude.toString())
            UserUtils.setCity(context, city)
            UserUtils.setState(context, state)
            UserUtils.setCountry(context, country)
            UserUtils.setPostalCode(context, postalCode)
            UserUtils.setAddress(context, knownName)
            if (UserUtils.getAddress(context).isNotEmpty()) {
                binding.myLocation.setText("${UserUtils.getAddress(context)}, ${UserUtils.getCity(context)}, ${UserUtils.getState(context)}, ${UserUtils.getCountry(context)}, ${UserUtils.getPostalCode(context)}")
            } else {
                binding.myLocation.setText(
                    "${UserUtils.getCity(context)}, ${
                        UserUtils.getState(
                            context
                        )
                    }, ${UserUtils.getCountry(context)}, ${UserUtils.getPostalCode(context)}"
                )
            }
            binding.addBtn.isEnabled = true
        } catch (e: Exception) {
            Toast.makeText(context, "Please Check you Internet Connection!", Toast.LENGTH_LONG)
                .show()
        }
    }

//    override fun onBackPressed() {
//        super.onBackPressed()
//        finish()
//        startActivity(Intent(this, SearchServiceProvidersScreen::class.java))
//    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                val place = Autocomplete.getPlaceFromIntent(data!!)
                binding.myLocation.setText(place.name)
//                Log.i(TAG, "Place: " +  + ", " + place.id)
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                val status = Autocomplete.getStatusFromIntent(data!!)
                snackBar(binding.addBtn, status.statusMessage!!)
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

}