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
import com.basusingh.beautifulprogressdialog.BeautifulProgressDialog
import com.google.android.gms.location.*
import com.satrango.R
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivitySignUpScreenThreeBinding
import com.satrango.remote.NetworkResponse
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.auth.forgot_password.ForgotPwdRepository
import com.satrango.ui.auth.forgot_password.ForgotPwdVerifyReqModel
import com.satrango.ui.auth.forgot_password.ForgotPwdViewModel
import com.satrango.ui.auth.login_screen.LoginScreen
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
    private lateinit var progressDialog: BeautifulProgressDialog

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

            firstName.setText(UserUtils.getFirstName(this@UserSignUpScreenThree))
            lastName.setText(UserUtils.getLastName(this@UserSignUpScreenThree))
            mobileNo.setText(UserUtils.getPhoneNo(this@UserSignUpScreenThree))
            if (UserUtils.getMail(this@UserSignUpScreenThree).isNotEmpty()) {
                email.setText(UserUtils.getMail(this@UserSignUpScreenThree))
            }

            if (email.text.toString().contains("@") || email.text.toString().contains(".")) {
                email.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_email_blue_24,
                    0,
                    R.drawable.ic_greencheck,
                    0
                )
            }

            if (mobileNo.text.toString().length == 10) {
                mobileNo.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_phoneno,
                    0,
                    R.drawable.ic_greencheck,
                    0
                )
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
                        email.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.ic_email_blue_24,
                            0,
                            R.drawable.ic_greencheck,
                            0
                        )
                    }
                }

                override fun afterTextChanged(s: Editable) {}
            })

            mobileNo.addTextChangedListener(object : TextWatcher {

                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    if (s.toString().length == 10) {
                        mobileNo.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.ic_phoneno,
                            0,
                            R.drawable.ic_greencheck,
                            0
                        )
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
                        firstName.error = "Please Enter First Name"
                        firstName.requestFocus()
                    }
                    last_name.isEmpty() -> {
                        lastName.error = "Please Enter Last Name"
                        lastName.requestFocus()
                    }
                    phoneNo.isEmpty() -> {
                        mobileNo.error = "Please Enter Mobile Number"
                        mobileNo.requestFocus()
                    }
                    phoneNo.length != 10 -> {
                        mobileNo.error = "Please Enter 10 digit Mobile Number"
                        mobileNo.requestFocus()
                    }
                    mail.isEmpty() -> {
                        email.error = "Please Enter Mail Id"
                        email.requestFocus()
                    }
                    !mail.contains("@") || !mail.contains(".") -> {
                        email.error = "Please Enter valid Mail Id"
                        email.requestFocus()
                    }
                    dob == resources.getString(R.string.date_of_birth) -> {
                        dateOfBirth.error = "Please Select Date Of Birth"
                        dateOfBirth.requestFocus()
                    }
                    selectedAge < 13 -> {
                        dateOfBirth.error = "Age should be greater than 13 years"
                        dateOfBirth.requestFocus()
                    }
                    !checkBox.isChecked -> {
                        snackBar(checkBox, "Please Accept Terms and Conditions")
                    }
                    selectedGender != R.id.male && selectedGender != R.id.female && selectedGender != R.id.others -> {
                        snackBar(genderGroup, "Please Select Gender")
                    }
                    else -> {
                        UserUtils.setFirstName(this@UserSignUpScreenThree, first_name)
                        UserUtils.setLastName(this@UserSignUpScreenThree, last_name)
                        UserUtils.setMail(this@UserSignUpScreenThree, mail)
                        UserUtils.setDateOfBirth(this@UserSignUpScreenThree, dob)
                        UserUtils.setPhoneNo(this@UserSignUpScreenThree, phoneNo)
                        when (selectedGender) {
                            R.id.male -> UserUtils.setGender(this@UserSignUpScreenThree, "Male")
                            R.id.female -> UserUtils.setGender(this@UserSignUpScreenThree, "Female")
                            R.id.others -> UserUtils.setGender(this@UserSignUpScreenThree, "Others")
                        }
                        fetchLocation()
                    }
                }
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initializeProgressDialog() {
        progressDialog = BeautifulProgressDialog(this, BeautifulProgressDialog.withImage, resources.getString(R.string.loading))
        progressDialog.setImageLocation(resources.getDrawable(R.drawable.circlelogo))
        progressDialog.setLayoutColor(resources.getColor(R.color.white))
    }

    @SuppressLint("SetTextI18n")
    private fun openDateOfBirthDialog() {
        val c = Calendar.getInstance()
        val mYear = c[Calendar.YEAR] // current year
        val mMonth = c[Calendar.MONTH] // current month
        val mDay = c[Calendar.DAY_OF_MONTH] // current day

        val datePickerDialog = DatePickerDialog(
            this@UserSignUpScreenThree, { _, year, monthOfYear, dayOfMonth ->
                binding.dateOfBirth.text =
                    year.toString() + "-" + (monthOfYear + 1) + "-" + dayOfMonth
                binding.dateOfBirth.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.ic_greencheck,
                    0
                )
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
        UserUtils.setLatitude(this, latitude.toString())
        UserUtils.setLongitude(this, longitude.toString())
        UserUtils.setCity(this, city)
        UserUtils.setState(this, state)
        UserUtils.setCountry(this, country)
        UserUtils.setPostalCode(this, postalCode)
        UserUtils.setAddress(this, knownName)
        verifyUser()
    }

    private fun verifyUser() {
        val forgotPwdVerifyReqModel = ForgotPwdVerifyReqModel(
            UserUtils.getMail(this),
            RetrofitBuilder.USER_KEY,
            UserUtils.getPhoneNo(this)
        )
        viewModel.verifyUser(this, forgotPwdVerifyReqModel).observe(this, {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    snackBar(binding.nextBtn, it.data!!)
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    startActivity(Intent(this@UserSignUpScreenThree, OTPVerificationScreen::class.java))
                }
            }
        })
    }
}