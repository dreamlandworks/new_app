package com.satrango.ui.auth.user_signup

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.satrango.R
import com.satrango.databinding.ActivitySignUpScreenOneBinding
import com.satrango.ui.auth.LoginScreen
import com.satrango.utils.PermissionUtils
import java.util.*

class UserSignUpScreenOne : AppCompatActivity() {

    private lateinit var locationCallBack: LocationCallback
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var binding: ActivitySignUpScreenOneBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpScreenOneBinding.inflate(layoutInflater)
        setContentView(binding.root)
        PermissionUtils.checkAndRequestPermissions(this)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        binding.apply {

            loginBtn.setOnClickListener {
                finish()
                startActivity(Intent(this@UserSignUpScreenOne, LoginScreen::class.java))
            }

            nextBtn.setOnClickListener {
                PermissionUtils.checkAndRequestPermissions(this@UserSignUpScreenOne)
                fetchLocation()
            }

            mobileNo.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    if (count == 10) {
                        mobileNo.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.ic_greencheck,
                            0
                        )
                    }
                }

                override fun afterTextChanged(s: Editable) {}
            })
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (PermissionUtils.PERMISSIONS_CODE == requestCode && grantResults.isNotEmpty()) {
            for (grant in grantResults) {
                if (grant != PackageManager.PERMISSION_GRANTED) {
                    PermissionUtils.checkAndRequestPermissions(this)
                    return
                }
            }
            fetchLocation()
        }
    }

    private fun fetchLocation() {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
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
            PermissionUtils.checkAndRequestPermissions(this)
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
                    Toast.makeText(
                        this@UserSignUpScreenOne,
                        "$latitude | $longitude",
                        Toast.LENGTH_SHORT
                    ).show()
                    fetchLocationDetails(latitude, longitude)
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
    private fun fetchLocationDetails(latitude: Double, longitude: Double) {
        val geoCoder = Geocoder(this, Locale.getDefault())
        val address: List<Address> = geoCoder.getFromLocation(latitude, longitude, 1)
        val addressName: String = address.get(0).getAddressLine(0)
        val city: String = address.get(0).locality
        val state: String = address.get(0).adminArea
        val country: String = address.get(0).countryName
        val postalCode: String = address.get(0).postalCode
        val knownName: String = address.get(0).featureName // Only if available else return NULL

        Log.e("LOCATION DETAILS", "$addressName, $city, $state, $country, $postalCode, $knownName")
        Toast.makeText(
            this,
            "$addressName, $city, $state, $country, $postalCode, $knownName",
            Toast.LENGTH_SHORT
        ).show()
        fusedLocationProviderClient.removeLocationUpdates(locationCallBack)
//        startActivity(
//            Intent(
//                this@UserSignUpScreenOne,
//                UserSignUpScreenTwo::class.java
//            )
//        )
    }

}