package com.satrango.ui.user.user_dashboard.user_home_screen.user_location_change

import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.satrango.R
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivityUserLocationSelectionScreenBinding
import com.satrango.remote.NetworkResponse
import com.satrango.remote.RetrofitBuilder
import com.satrango.utils.*
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*


class UserLocationSelectionScreen : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityUserLocationSelectionScreenBinding
    private lateinit var viewModel: UserLocationChangeViewModel
    private lateinit var progressDialog: ProgressDialog

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationCallBack: LocationCallback
    private var mMap: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserLocationSelectionScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolBar = binding.root.findViewById<View>(R.id.toolBar)
        toolBar.findViewById<ImageView>(R.id.toolBarBackBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarBackTVBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text =
            resources.getString(R.string.my_location)
        val profilePic = toolBar.findViewById<CircleImageView>(R.id.toolBarImage)
        loadProfileImage(profilePic)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        val mapFragment =
            supportFragmentManager.findFragmentById(com.satrango.R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)

        val factory = ViewModelFactory(UserLocationChangeRepository())
        viewModel = ViewModelProvider(this, factory)[UserLocationChangeViewModel::class.java]

        progressDialog = ProgressDialog(this)
        progressDialog.setCancelable(false)
        progressDialog.setMessage("Loading...")

        if (PermissionUtils.checkGPSStatus(this) && networkAvailable(this)) {
            fetchLocation(this)
        }

        binding.resetBtn.setOnClickListener {
            finish()
            startActivity(intent)
        }

        binding.addBtn.setOnClickListener {
            changeAddressInServer()
        }

    }

    private fun changeAddressInServer() {
        val requestBody = UserLocationChangeReqModel(
            UserUtils.address,
            UserUtils.address,
            UserUtils.city,
            UserUtils.country,
            UserUtils.address,
            RetrofitBuilder.USER_KEY,
            UserUtils.address,
            UserUtils.getUserName(this),
            UserUtils.postalCode,
            UserUtils.state,
            UserUtils.latitude,
            UserUtils.longitute,
            UserUtils.getUserId(this).toInt(),
        )
        viewModel.changeUserLocation(this, requestBody).observe(this, {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    snackBar(binding.addBtn, it.data!!)
                    Handler().postDelayed({
                        onBackPressed()
                    }, 3000)
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.addBtn, it.message!!)
                }
            }
        })
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val latLong = LatLng(UserUtils.latitude.toDouble(), UserUtils.longitute.toDouble())
        mMap!!.moveCamera(CameraUpdateFactory.newLatLng(latLong))
        mMap!!.animateCamera(CameraUpdateFactory.zoomTo(15f), 2000, null);

        mMap!!.addMarker(
            MarkerOptions()
                .position(latLong)
//                .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_my_location))
                .draggable(true)
        )
        mMap!!.setOnMarkerDragListener(object : OnMarkerDragListener {
            override fun onMarkerDragStart(marker: Marker) {
                Log.d(
                    "System out",
                    "onMarkerDragStart..." + marker.position.latitude + "..." + marker.position.longitude
                )
            }

            override fun onMarkerDragEnd(marker: Marker) {
                Log.d(
                    "System out",
                    "onMarkerDragEnd..." + marker.position.latitude + "..." + marker.position.longitude
                )
                mMap!!.animateCamera(CameraUpdateFactory.newLatLng(marker.position))
                fetchLocationDetails(
                    this@UserLocationSelectionScreen,
                    marker.position.latitude,
                    marker.position.longitude
                )
            }

            override fun onMarkerDrag(arg0: Marker) {
                Log.i("System out", "onMarkerDrag...")
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
            UserUtils.latitude = latitude.toString()
            UserUtils.longitute = longitude.toString()
            UserUtils.city = city
            UserUtils.state = state
            UserUtils.country = country
            UserUtils.postalCode = postalCode
            UserUtils.address = knownName
            if (UserUtils.address.isNotEmpty()) {
                binding.myLocation.setText("${UserUtils.address}, ${UserUtils.city}, ${UserUtils.state}, ${UserUtils.country}, ${UserUtils.postalCode}")
            } else {
                binding.myLocation.setText("${UserUtils.city}, ${UserUtils.state}, ${UserUtils.country}, ${UserUtils.postalCode}")
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Please Check you Internet Connection!", Toast.LENGTH_LONG)
                .show()
        }
    }
}