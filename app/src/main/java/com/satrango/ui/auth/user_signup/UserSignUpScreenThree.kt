package com.satrango.ui.auth.user_signup

import android.Manifest
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.*
import com.satrango.R
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivitySignUpScreenThreeBinding
import com.satrango.remote.NetworkResponse
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.auth.forgot_password.ForgotPwdRepository
import com.satrango.ui.auth.forgot_password.ForgotPwdVerifyReqModel
import com.satrango.ui.auth.forgot_password.ForgotPwdViewModel
import com.satrango.ui.auth.loginscreen.LoginScreen
import com.satrango.ui.auth.user_signup.otp_verification.OTPVerificationScreen
import com.satrango.utils.PermissionUtils
import com.satrango.utils.UserUtils
import com.satrango.utils.snackBar
import java.util.*

class UserSignUpScreenThree : AppCompatActivity() {

    companion object {
        fun getAge(year: Int, month: Int, day: Int): Int {
            val dob = Calendar.getInstance()
            val today = Calendar.getInstance()
            dob[year, month] = day
            var age = today[Calendar.YEAR] - dob[Calendar.YEAR]
            if (today[Calendar.DAY_OF_YEAR] < dob[Calendar.DAY_OF_YEAR]) {
                age--
            }
            return age
        }
    }


    private lateinit var viewModel: ForgotPwdViewModel
    private var selectedAge = 0
    private lateinit var binding: ActivitySignUpScreenThreeBinding

    private lateinit var locationCallBack: LocationCallback
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var progressDialog: ProgressDialog

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpScreenThreeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        initializeProgressDialog()
        val factory = ViewModelFactory(ForgotPwdRepository())
        viewModel = ViewModelProvider(this, factory)[ForgotPwdViewModel::class.java]

        binding.apply {

            firstName.setText(UserUtils.firstName)
            lastName.setText(UserUtils.lastName)
            mobileNo.setText(UserUtils.phoneNo)
            if (UserUtils.mailId.isNotEmpty()) {
                email.setText(UserUtils.mailId)
            }

            email.addTextChangedListener(object : TextWatcher {

                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    if (s.toString().contains("@") || s.toString().contains(".")) {
                        email.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_email_blue_24, 0, R.drawable.ic_greencheck, 0)
                    }
                }

                override fun afterTextChanged(s: Editable) {}
            })

            dateOfBirth.setOnClickListener {
                openDateOfBirthDialog()
            }

            termsAndConditions.setOnClickListener {
                startActivity(
                    Intent(
                        this@UserSignUpScreenThree,
                        TermsAndConditionScreen::class.java
                    )
                )
            }

            loginBtn.setOnClickListener {
                startActivity(Intent(this@UserSignUpScreenThree, LoginScreen::class.java))
                finish()
            }

            nextBtn.setOnClickListener {

                val first_name = firstName.text.toString().trim()
                val last_name = lastName.text.toString().trim()
                val mail = email.text.toString().trim()
                val phoneNo = mobileNo.text.toString().trim()
                val dob = dateOfBirth.text.toString().trim()
                val selectedGender = genderGroup.checkedRadioButtonId

                when {
                    first_name.isEmpty() -> {
                        firstName.error = "Enter First Name"
                        firstName.requestFocus()
                    }
                    last_name.isEmpty() -> {
                        lastName.error = "Enter Last Name"
                        lastName.requestFocus()
                    }
                    phoneNo.isEmpty() -> {
                        mobileNo.error = "Enter Phone No"
                        mobileNo.requestFocus()
                    }
                    mail.isEmpty() -> {
                        email.error = "Enter Mail Id"
                        email.requestFocus()
                    }
                    dob == resources.getString(R.string.date_of_birth) -> {
                        dateOfBirth.error = "Select Date Of Birth"
                        dateOfBirth.requestFocus()
                    }
                    selectedAge < 13 -> {
                        dateOfBirth.error = "Age should be greater than 13 years"
                        dateOfBirth.requestFocus()
                    }
                    !checkBox.isChecked -> {
                        snackBar(checkBox, "Accept Terms and Conditions")
                    }
                    selectedGender != R.id.male && selectedGender != R.id.female && selectedGender != R.id.others -> {
                        snackBar(genderGroup, "Select Gender")
                    }
                    else -> {
                        UserUtils.firstName = first_name
                        UserUtils.lastName = last_name
                        UserUtils.mailId = mail
                        UserUtils.dateOfBirth = dob
                        UserUtils.phoneNo = phoneNo
                        when (selectedGender) {
                            R.id.male -> UserUtils.gender = "Male"
                            R.id.female -> UserUtils.gender = "Female"
                            R.id.others -> UserUtils.gender = "Others"
                        }
                        fetchLocation()
                    }
                }
            }
        }
    }

    private fun initializeProgressDialog() {
        progressDialog = ProgressDialog(this)
        progressDialog.setCancelable(false)
        progressDialog.setMessage("Loading...")
    }

    @SuppressLint("SetTextI18n")
    private fun openDateOfBirthDialog() {
        val c = Calendar.getInstance()
        val mYear = c[Calendar.YEAR] // current year
        val mMonth = c[Calendar.MONTH] // current month
        val mDay = c[Calendar.DAY_OF_MONTH] // current day

        val datePickerDialog = DatePickerDialog(
            this@UserSignUpScreenThree, { _, year, monthOfYear, dayOfMonth ->
                binding.dateOfBirth.text = year.toString() + "-" + (monthOfYear + 1) + "-" + dayOfMonth
                binding.dateOfBirth.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_greencheck, 0)
                selectedAge = getAge(year, monthOfYear + 1, dayOfMonth)
                if (selectedAge < 13) {
                    snackBar(binding.dateOfBirth, "Age must be greater than 13 years")
                }
            }, mYear, mMonth, mDay
        )
        datePickerDialog.datePicker.maxDate = Date().time
        datePickerDialog.show()
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
        progressDialog.show()
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
        UserUtils.latitude = latitude.toString()
        UserUtils.longitute = longitude.toString()
        UserUtils.city = city
        UserUtils.state = state
        UserUtils.country = country
        UserUtils.postalCode = postalCode
        UserUtils.address = knownName
        verifyUser()
    }

    private fun verifyUser() {
        val forgotPwdVerifyReqModel = ForgotPwdVerifyReqModel(UserUtils.mailId, RetrofitBuilder.KEY, UserUtils.phoneNo)
        viewModel.verifyUser(this, forgotPwdVerifyReqModel).observe(this, {
            when(it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    snackBar(binding.nextBtn, it.data!!)
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    if (it.message!! == "User Not Found") {
                        startActivity(Intent(this@UserSignUpScreenThree, OTPVerificationScreen::class.java))
                    } else {
                        snackBar(binding.nextBtn, it.message)
                    }
                }
            }
        })
    }
}