package com.satrango.ui.auth.user_signup

import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.basusingh.beautifulprogressdialog.BeautifulProgressDialog
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.location.*
import com.satrango.R
import com.satrango.databinding.ActivitySignUpScreenOneBinding
import com.satrango.ui.auth.login_screen.LoginScreen
import com.satrango.utils.PermissionUtils
import com.satrango.utils.UserUtils
import com.satrango.utils.snackBar
import com.satrango.utils.toast
import java.util.*

class UserSignUpScreenOne : AppCompatActivity() {

    // Fused Location Objects
    private lateinit var locationCallBack: LocationCallback
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private lateinit var binding: ActivitySignUpScreenOneBinding

    // Google SignIn Objects
    private lateinit var googleSignInOptions: GoogleSignInOptions
    private lateinit var googleSignInClient: GoogleSignInClient
    private val GOOGLE_SIGN_IN: Int = 100

    // Facebook SignIn Object
    private lateinit var facebookCallBackManager: CallbackManager

    private lateinit var progressDialog: BeautifulProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpScreenOneBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeProgressDialog()
        initializeSocialLogins()
        PermissionUtils.checkAndRequestPermissions(this)

//        toast(this, UserUtils.fetchLocation(this))
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        textWatchers()

        binding.apply {

            loginBtn.setOnClickListener {
                finish()
                startActivity(Intent(this@UserSignUpScreenOne, LoginScreen::class.java))
            }

            googleSigInBtn.setOnClickListener {
                val signInIntent = googleSignInClient.signInIntent
                startActivityForResult(signInIntent, GOOGLE_SIGN_IN)
            }

            facebookSignInBtn.setOnClickListener {
                facebookSignBtn.performClick()
            }

            nextBtn.setOnClickListener {
                val first_Name = firstName.text.toString().trim()
                val last_Name = lastName.text.toString().trim()
                val phoneNo = mobileNo.text.toString().trim()

                when {
                    first_Name.isEmpty() -> {
                        firstName.error = "Please Enter First Name"
                        firstName.requestFocus()
                    }
                    last_Name.isEmpty() -> {
                        lastName.error = "Please Enter Last Name"
                        lastName.requestFocus()
                    }
                    phoneNo.isEmpty() -> {
                        mobileNo.error = "Please Enter 10 digit Mobile Number"
                        mobileNo.requestFocus()
                    }
                    phoneNo.length != 10 -> {
                        mobileNo.error = "Please Enter 10 digit Mobile Number"
                        mobileNo.requestFocus()
                    }
                    phoneNo.length < 10 -> {
                        mobileNo.error = "Please Enter 10 digit Mobile Number"
                        mobileNo.requestFocus()
                    }
                    else -> {
                        UserUtils.setFirstName(this@UserSignUpScreenOne, first_Name)
                        UserUtils.setLastName(this@UserSignUpScreenOne, last_Name)
                        UserUtils.setPhoneNo(this@UserSignUpScreenOne, phoneNo)
                        PermissionUtils.checkAndRequestPermissions(this@UserSignUpScreenOne)
                        fetchLocation()
                    }
                }
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initializeProgressDialog() {
        progressDialog = BeautifulProgressDialog(this, BeautifulProgressDialog.withGIF, resources.getString(R.string.loading))
        progressDialog.setGifLocation(Uri.parse("android.resource://${packageName}/${R.drawable.blue_loading}"))
        progressDialog.setLayoutColor(resources.getColor(R.color.progressDialogColor))
    }

    private fun textWatchers() {
        binding.mobileNo.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                if (s.length == 10) {
                    binding.mobileNo.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_phoneno, 0, R.drawable.ic_greencheck, 0)
                }
            }
        })

        binding.firstName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                binding.firstName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_person_blue_24, 0, R.drawable.ic_greencheck, 0)
            }

            override fun afterTextChanged(s: Editable) {}
        })

        binding.lastName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                binding.lastName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_person_blue_24, 0, R.drawable.ic_greencheck, 0)
            }

            override fun afterTextChanged(s: Editable) {}
        })
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

    fun fetchLocation() {
        progressDialog.show()
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

        val locationRequest = LocationRequest().setInterval(2000).setFastestInterval(2000).setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
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
        startActivity(Intent(this@UserSignUpScreenOne, UserSignUpScreenTwo::class.java))
        progressDialog.dismiss()
    }

    private fun initializeSocialLogins() {

        // Google SignIn Object Initialization
        googleSignInOptions = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)

        // Facebook SignIn Object Initialization
        facebookCallBackManager = CallbackManager.Factory.create()

        binding.facebookSignBtn.registerCallback(
            facebookCallBackManager,
            object : FacebookCallback<LoginResult> {

                override fun onSuccess(result: LoginResult?) {
                    val token = result!!.accessToken
                    val request = GraphRequest.newMeRequest(
                        token
                    ) { jsonObject, _ ->
                        val userId = jsonObject.getString("id")
                        val userName = jsonObject.getString("name")
                        UserUtils.setFacebookId(this@UserSignUpScreenOne, userId)
                        UserUtils.setFirstName(this@UserSignUpScreenOne , userName.split(" ")[0])
                        try {
                            UserUtils.setLastName(this@UserSignUpScreenOne, userName.split(" ")[1])
                        } catch (e: java.lang.Exception) {}
                        startActivity(Intent(this@UserSignUpScreenOne, UserSignUpScreenThree::class.java))
                    }
                    val parameters = Bundle()
                    parameters.putString("fields", "id, name")
                    request.parameters = parameters
                    request.executeAsync()
                }

                override fun onCancel() {
                    snackBar(binding.nextBtn, "Login Cancelled")
                }

                override fun onError(error: FacebookException?) {
                    snackBar(binding.nextBtn, error!!.message!!)
                }

            })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        facebookCallBackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GOOGLE_SIGN_IN) {
            try {
                val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
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
            } catch (e: java.lang.Exception) {}
            UserUtils.setMail(this, email!!)
            startActivity(Intent(this, UserSignUpScreenThree::class.java))
        } else {
            snackBar(binding.nextBtn, "Google SignIn Failed")
        }
        progressDialog.dismiss()
    }



}