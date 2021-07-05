package com.satrango.ui.auth.user_signup

import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
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
import com.satrango.ui.auth.loginscreen.LoginScreen
import com.satrango.utils.PermissionUtils
import com.satrango.utils.UserUtils
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

    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpScreenOneBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeProgressDialog()
        initializeSocialLogins()
        PermissionUtils.checkAndRequestPermissions(this)
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

                if (first_Name.isEmpty()) {
                    firstName.error = "Enter First Name"
                    firstName.requestFocus()
                } else if (last_Name.isEmpty()) {
                    lastName.error = "Enter Last Name"
                    lastName.requestFocus()
                } else if (phoneNo.isEmpty()) {
                    mobileNo.error = "Enter Mobile No"
                    mobileNo.requestFocus()
                } else if (phoneNo.length < 10) {
                    mobileNo.error = "Enter Valid Mobile No"
                    mobileNo.requestFocus()
                } else {
                    UserUtils.firstName = first_Name
                    UserUtils.lastName = last_Name
                    UserUtils.phoneNo = phoneNo
                    PermissionUtils.checkAndRequestPermissions(this@UserSignUpScreenOne)
                    fetchLocation()
                }
            }
        }
    }

    private fun initializeProgressDialog() {
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Loading...")
        progressDialog.setCancelable(false)
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
                    binding.mobileNo.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.ic_greencheck,
                        0
                    )
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
                binding.firstName.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.ic_greencheck,
                    0
                )
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
                binding.lastName.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.ic_greencheck,
                    0
                )
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

    private fun fetchLocation() {
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
        UserUtils.latitude = latitude.toString()
        UserUtils.longitute = longitude.toString()
        UserUtils.city = city
        UserUtils.state = state
        UserUtils.country = country
        UserUtils.postalCode = postalCode
        UserUtils.address = knownName
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
                        UserUtils.facebookId = userId
                        UserUtils.firstName = userName.split(" ")[0]
                        try {
                            UserUtils.lastName = userName.split(" ")[1]
                        } catch (e: java.lang.Exception) {}
                        startActivity(Intent(this@UserSignUpScreenOne, UserSignUpScreenThree::class.java))
                    }
                    val parameters = Bundle()
                    parameters.putString("fields", "id, name")
                    request.parameters = parameters
                    request.executeAsync()
                }

                override fun onCancel() {
                    Toast.makeText(this@UserSignUpScreenOne, "Login Cancelled", Toast.LENGTH_SHORT).show()
                }

                override fun onError(error: FacebookException?) {
                    Toast.makeText(this@UserSignUpScreenOne, error!!.message, Toast.LENGTH_SHORT).show()
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
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
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
            UserUtils.googleId = googleId!!
            UserUtils.firstName = userName!!.split(" ")[0]
            try {
                UserUtils.lastName = userName.split(" ")[1]
            } catch (e: java.lang.Exception) {}
            UserUtils.mailId = email!!
            startActivity(Intent(this, UserSignUpScreenThree::class.java))
        } else {
            Toast.makeText(this, "Google SignIn Failed", Toast.LENGTH_SHORT).show()
        }
        progressDialog.dismiss()
    }



}