package com.satrango.ui.auth.user_signup

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
//import com.facebook.CallbackManager
//import com.facebook.FacebookCallback
//import com.facebook.FacebookException
//import com.facebook.GraphRequest
//import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.location.*
import com.satrango.databinding.ActivityUserSignUpScreenTwoBinding
import com.satrango.ui.auth.login_screen.LoginScreen
import com.satrango.utils.PermissionUtils
import com.satrango.utils.UserUtils
import com.satrango.utils.snackBar
import java.util.*

class UserSignUpScreenTwo : AppCompatActivity() {

    private lateinit var binding: ActivityUserSignUpScreenTwoBinding

    // Fused Location Objects
    private lateinit var locationCallBack: LocationCallback
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    // Google SignIn Objects
    private lateinit var googleSignInOptions: GoogleSignInOptions
    private lateinit var googleSignInClient: GoogleSignInClient
    private val GOOGLE_SIGN_IN: Int = 100

    // Facebook SignIn Object
//    private lateinit var facebookCallBackManager: CallbackManager

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserSignUpScreenTwoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        PermissionUtils.checkAndRequestPermissions(this)
        initializeSocialLogins()
        fetchLocation()

        binding.apply {
            userName.text = UserUtils.getFirstName(this@UserSignUpScreenTwo) + " " + UserUtils.getLastName(this@UserSignUpScreenTwo)

            loginBtn.setOnClickListener {
                finish()
                startActivity(Intent(this@UserSignUpScreenTwo, LoginScreen::class.java))
            }

            googleSigInBtn.setOnClickListener {
                val signInIntent = googleSignInClient.signInIntent
                startActivityForResult(signInIntent, GOOGLE_SIGN_IN)
            }

//            facebookSignInBtn.setOnClickListener {
//                facebookSignBtn.performClick()
//            }

            nextBtn.setOnClickListener {
                startActivity(Intent(this@UserSignUpScreenTwo, UserSignUpScreenThree::class.java))
            }
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
        val knownName: String = address.get(0).featureName
        fusedLocationProviderClient.removeLocationUpdates(locationCallBack)
        UserUtils.setLatitude(this, latitude.toString())
        UserUtils.setLongitude(this, longitude.toString())
        UserUtils.setCity(this, city)
        UserUtils.setState(this, state)
        UserUtils.setCountry(this, country)
        UserUtils.setPostalCode(this, postalCode)
        UserUtils.setAddress(this, knownName)
        binding.userLocation.text = "$city, $state, $country, $postalCode"
    }

    private fun initializeSocialLogins() {

        // Google SignIn Object Initialization
        googleSignInOptions = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)

        // Facebook SignIn Object Initialization
//        facebookCallBackManager = CallbackManager.Factory.create()

//        binding.facebookSignBtn.registerCallback(
//            facebookCallBackManager,
//            object : FacebookCallback<LoginResult> {
//
//                override fun onSuccess(result: LoginResult?) {
//                    val token = result!!.accessToken
//                    val request = GraphRequest.newMeRequest(
//                        token
//                    ) { jsonObject, _ ->
//                        val userId = jsonObject.getString("id")
//                        val userName = jsonObject.getString("name")
//                        UserUtils.setFacebookId(this@UserSignUpScreenTwo, userId)
//                        UserUtils.setFirstName(this@UserSignUpScreenTwo, userName.split(" ")[0])
//                        try {
//                            UserUtils.setLastName(this@UserSignUpScreenTwo, userName.split(" ")[1])
//                        } catch (e: java.lang.Exception) {}
//                        startActivity(
//                            Intent(
//                                this@UserSignUpScreenTwo,
//                                UserSignUpScreenThree::class.java
//                            )
//                        )
//                    }
//                    val parameters = Bundle()
//                    parameters.putString("fields", "id, name")
//                    request.parameters = parameters
//                    request.executeAsync()
//                }
//
//                override fun onCancel() {
//                    snackBar(binding.nextBtn, "Login Cancelled")
//                }
//
//                override fun onError(error: FacebookException?) {
//                    snackBar(binding.nextBtn, error!!.message!!)
//                }
//
//            })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        facebookCallBackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GOOGLE_SIGN_IN) {
            try {
                val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data!!)
                validateGoogleSignInResult(result)
            } catch (e: Exception) {
                snackBar(binding.nextBtn, e.message!!)
            }
        }
    }

    private fun validateGoogleSignInResult(result: GoogleSignInResult?) {
        if (result!!.isSuccess) {
            val account = result.signInAccount
            val token = account!!.idToken
            val userName = account.displayName
            val email = account.email
            val googleId = account.id
            val image = account.photoUrl
            UserUtils.setGoogleId(this, googleId!!)
            UserUtils.setFirstName(this, userName!!.split(" ")[0])
            try {
                UserUtils.setLastName(this, userName.split(" ")[1])
            } catch (e: java.lang.Exception) {
            }
            UserUtils.setMail(this, email!!)
            startActivity(Intent(this, UserSignUpScreenThree::class.java))
        } else {
            snackBar(binding.nextBtn, "Google SignIn Failed")
        }
    }


}