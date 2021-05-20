package com.satrango.ui.auth.user_signup

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.satrango.databinding.ActivitySignUpScreenTwoBinding
import com.satrango.utils.PermissionUtils
import java.util.*

class UserSignUpScreenTwo : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpScreenTwoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpScreenTwoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        PermissionUtils.checkAndRequestPermissions(this)
        fetchLocation()

        binding.apply {
            nextBtn.setOnClickListener { startActivity(Intent(this@UserSignUpScreenTwo, UserSignUpScreenThree::class.java)) }
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
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            1000 * 60 * 1,
            10f
        ) { location ->
            val latitude = location.latitude
            val longitude = location.longitude
            fetchLocationDetails(latitude, longitude)
        }

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
        binding.locationText.text = "$addressName, $city, $state, $country, $postalCode, $knownName"
        Log.e("LOCATION DETAILS", "$addressName, $city, $state, $country, $postalCode, $knownName")
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
}