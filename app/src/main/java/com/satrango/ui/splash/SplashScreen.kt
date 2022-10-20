package com.satrango.ui.splash

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.google.android.gms.location.*
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.FirebaseApp
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.satrango.R
import com.satrango.databinding.ActivitySplashScreenBinding
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.auth.UserLoginTypeScreen
import com.satrango.ui.auth.login_screen.LoginScreen
import com.satrango.ui.user.bookings.view_booking_details.models.GetQRCodeSPReqModel
import com.satrango.utils.AuthUtils
import com.satrango.utils.UserUtils
import com.satrango.utils.snackBar
import com.satrango.utils.toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.*

class SplashScreen : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding
    // Fused Location Objects
    private lateinit var locationCallBack: LocationCallback
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Glide.with(binding.splashImage).load(R.drawable.home_background_one)
            .into(binding.splashBackground)
        Glide.with(binding.splashImage).load(R.drawable.squill).into(binding.splashImage)
    }


    private fun setUserNavigation() {
        Handler().postDelayed({
            if (AuthUtils.getFirstTimeLaunch(this)) {
                startActivity(Intent(this, IntroScreen::class.java))
            } else {
                if (UserUtils.getUserLoggedInVia(this).isNotEmpty()) {
                    startActivity(Intent(this, UserLoginTypeScreen::class.java))
                } else {
                    if (UserUtils.getUserId(this).isNotEmpty()) {
                        startActivity(Intent(this, UserLoginTypeScreen::class.java))
                    } else {
                        startActivity(Intent(this, LoginScreen::class.java))
                    }
                }
            }
            finish()
        }, 1000)

    }

    override fun onStart() {
        super.onStart()
        Handler().postDelayed({
            FirebaseApp.initializeApp(this)
            Firebase.dynamicLinks
                .getDynamicLink(intent)
                .addOnSuccessListener(this) { pendingDynamicLinkData ->
                    var deepLink: Uri? = null
                    if (pendingDynamicLinkData != null) {
                        deepLink = pendingDynamicLinkData.link
                        val referralUserId = deepLink.toString().split("=")[1]
                        UserUtils.saveReferralId(this, referralUserId)
                        if (UserUtils.getReferralId(this).isNotEmpty()) {
                            try {
                                val jsonResponse = JSONObject(Gson().toJson(UserUtils.getReferralId(this)))
                                fetchLocation(jsonResponse)
                            } catch (e: Exception) {
                                toast(this, UserUtils.getReferralId(this))
                            }
                        }
                    }
                    setUserNavigation()
                }
                .addOnFailureListener(this) {
                    Toast.makeText(this, "Refer Failed", Toast.LENGTH_SHORT).show()
                    setUserNavigation()
                }
        }, 3000)
    }

    private fun fetchLocation(jsonResponse: JSONObject) {
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
            return
        }

        val locationRequest =
            LocationRequest().setInterval(2000).setFastestInterval(2000).setPriority(
                LocationRequest.PRIORITY_HIGH_ACCURACY
            )
        locationCallBack = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                for (location in locationResult.locations) {
                    val latitude = location.latitude
                    val longitude = location.longitude
                    fetchLocationDetails(latitude, longitude, jsonResponse)
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
    private fun fetchLocationDetails(latitude: Double, longitude: Double, jsonResponse: JSONObject) {
        val geoCoder = Geocoder(this, Locale.getDefault())
        val address: List<Address> = geoCoder.getFromLocation(latitude, longitude, 1)
        val addressName: String = address.get(0).getAddressLine(0)
        val city: String = address.get(0).locality
        val state: String = address.get(0).adminArea
        val country: String = address.get(0).countryName
        val postalCode: String = address.get(0).postalCode
        val knownName: String = address.get(0).featureName
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationProviderClient.removeLocationUpdates(locationCallBack)
        UserUtils.setLatitude(this, latitude.toString())
        UserUtils.setLongitude(this, longitude.toString())
        UserUtils.setCity(this, city)
        UserUtils.setState(this, state)
        UserUtils.setCountry(this, country)
        UserUtils.setPostalCode(this, postalCode)
        UserUtils.setAddress(this, knownName)
        val categories = emptyArray<String>()
        val professions = jsonResponse.getJSONArray("profession")
        for (profession in 0 until professions.length()) {
            val jsonObject = professions.getJSONObject(profession)
            categories[profession] = jsonObject.getString("profession_name")
        }
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("Select Service Provider")
        alertDialog.setItems(categories) { _, index ->
            toast(this, categories[index])
            getSPDataFromServer(jsonResponse, professions.getJSONObject(index))
        }
        alertDialog.show()
    }

    private fun getSPDataFromServer(json: JSONObject, professionJson: JSONObject) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val spDetails = json.getJSONObject("sp_details")
                val requestBody = GetQRCodeSPReqModel(
                    UserUtils.getAddress(this@SplashScreen),
                    professionJson.getString("category_id").toInt(),
                    UserUtils.getCity(this@SplashScreen),
                    UserUtils.getCountry(this@SplashScreen),
                    RetrofitBuilder.USER_KEY,
                    0,
                    UserUtils.getPostalCode(this@SplashScreen),
                    professionJson.getString("profession_id").toInt(),
                    spDetails.getString("id").toInt(),
                    UserUtils.getState(this@SplashScreen),
                    UserUtils.getLatitude(this@SplashScreen),
                    UserUtils.getLongitude(this@SplashScreen),
                    UserUtils.getUserId(this@SplashScreen).toInt()
                )
                val response = RetrofitBuilder.getUserRetrofitInstance().getSpDetails(requestBody)
                toast(this@SplashScreen, Gson().toJson(response.data))
            } catch (e: Exception) {
                snackBar(binding.splashImage, e.message!!)
            }
        }
    }
}