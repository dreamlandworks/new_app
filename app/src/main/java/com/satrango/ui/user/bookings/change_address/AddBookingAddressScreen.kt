package com.satrango.ui.user.bookings.change_address

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.basusingh.beautifulprogressdialog.BeautifulProgressDialog
import com.google.android.gms.location.*
import com.google.gson.Gson
import com.satrango.R
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivityChangeBookingAddressScreenBinding
import com.satrango.remote.NetworkResponse
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.user.bookings.booking_address.BookingAddressScreen
import com.satrango.ui.user.bookings.booking_address.BookingRepository
import com.satrango.ui.user.bookings.booking_address.BookingViewModel
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.description.PostJobDescriptionScreen
import com.satrango.ui.user.user_dashboard.search_service_providers.models.Data
import com.satrango.utils.UserUtils
import com.satrango.utils.snackBar
import java.util.*

class AddBookingAddressScreen : AppCompatActivity() {

    private lateinit var binding: ActivityChangeBookingAddressScreenBinding
    private lateinit var locationCallBack: LocationCallback
    private lateinit var progressDialog: BeautifulProgressDialog
    private lateinit var data: Data

    @SuppressLint("StaticFieldLeak")
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var viewModel: BookingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangeBookingAddressScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val factory = ViewModelFactory(BookingRepository())
        viewModel = ViewModelProvider(this, factory)[BookingViewModel::class.java]
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        if (UserUtils.getSelectedSPDetails(this).isNotEmpty()) {
            data = Gson().fromJson(UserUtils.getSelectedSPDetails(this), Data::class.java)
        }
//        if (!UserUtils.getFromInstantBooking(this)) {
//            data = intent.getSerializableExtra(getString(R.string.service_provider)) as Data
//        }
        initializeProgressDialog()

        binding.apply { 
            
            resetBtn.setOnClickListener {
                flatNo.setText("")
                flatName.setText("")
                streetName.setText("")
                pinCode.setText("")
                city.setText("")
            }
            
            addBtn.setOnClickListener {
                validateFields()
            }
            
        }

    }

    private fun validateFields() {
        when {
            binding.flatNo.text.toString().isEmpty() -> {
                snackBar(binding.addBtn, "Enter Flat No")
            }
            binding.flatName.text.toString().isEmpty() -> {
                snackBar(binding.addBtn, "Enter Flat Name")
            }
            binding.streetName.text.toString().isEmpty() -> {
                snackBar(binding.addBtn, "Enter Street Name")
            }
            binding.pinCode.text.toString().isEmpty() -> {
                snackBar(binding.addBtn, "Enter PinCode")
            }
            binding.city.text.toString().isEmpty() -> {
                snackBar(binding.addBtn, "Enter City Name")
            }
            else -> {
                fetchLocation(this)
            }
        }
    }

    fun fetchLocation(context: Context) {
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
            val addressName: String = address.get(0).getAddressLine(0)
            var city = "unknown"
            var state = "unknown"
            var country = "unknown"
            var postalCode = "unknown"
            var knownName = "unknown"
            try { city = address.get(0).locality } catch (e: Exception) { }
            try { state = address.get(0).adminArea } catch (e: Exception) { }
            try { country = address.get(0).countryName } catch (e: Exception) { }
            try { postalCode = address.get(0).postalCode } catch (e: Exception) { }
            try { knownName = address.get(0).featureName } catch (e: Exception) { }
            fusedLocationProviderClient.removeLocationUpdates(locationCallBack)
            UserUtils.setLatitude(context, latitude.toString())
            UserUtils.setLongitude(context, longitude.toString())
            UserUtils.setCity(context, city)
            UserUtils.setState(context, state)
            UserUtils.setCountry(context, country)
            UserUtils.setPostalCode(context, postalCode)
            UserUtils.setAddress(context, knownName)

            val requestBody = AddBookingAddressReqModel(
                knownName,
                knownName,
                binding.city.text.toString(),
                country,
                "${binding.flatNo.text.toString().trim()} ${binding.flatName.text.toString().trim()}",
                RetrofitBuilder.USER_KEY,
                knownName,
                "${UserUtils.getFirstName(context)} ${UserUtils.getLastName(context)}",
                binding.pinCode.text.toString().trim(),
                state,
                latitude.toString(),
                longitude.toString(),
                UserUtils.getUserId(context).toInt()
            )
            viewModel.addBookingAddress(context, requestBody).observe(this) {
                when (it) {
                    is NetworkResponse.Loading -> {
                        progressDialog.show()
                    }
                    is NetworkResponse.Success -> {
                        progressDialog.dismiss()
//                        toast(context, it.data!!)
                        finish()
                        if (UserUtils.getFromJobPost(this)) {
                            startActivity(Intent(this, PostJobDescriptionScreen::class.java))
                        } else {
                            val intent = Intent(this, BookingAddressScreen::class.java)
//                            intent.putExtra(getString(R.string.service_provider), data)
                            startActivity(intent)
                        }
                    }
                    is NetworkResponse.Failure -> {
                        progressDialog.dismiss()
                        snackBar(binding.addBtn, it.message!!)
                    }
                }
            }

        } catch (e: Exception) {
            Toast.makeText(context, "Please Check you Internet Connection!", Toast.LENGTH_LONG)
                .show()
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initializeProgressDialog() {
        progressDialog = BeautifulProgressDialog(this, BeautifulProgressDialog.withGIF, resources.getString(R.string.loading))
        progressDialog.setGifLocation(Uri.parse("android.resource://${packageName}/${R.drawable.blue_loading}"))
        progressDialog.setLayoutColor(resources.getColor(R.color.progressDialogColor))
    }

    override fun onBackPressed() {
        if (UserUtils.getBookingType(this) != "instant") {
            val intent = Intent(this, BookingAddressScreen::class.java)
            intent.putExtra(getString(R.string.service_provider), data)
            startActivity(intent)
        } else {
            startActivity(Intent(this, PostJobDescriptionScreen::class.java))
        }
    }
}