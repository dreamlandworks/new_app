package com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job

import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.basusingh.beautifulprogressdialog.BeautifulProgressDialog
import com.google.android.gms.location.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.card.MaterialCardView
import com.google.gson.Gson
import com.satrango.R
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivityPostJobAddressScreenBinding
import com.satrango.remote.NetworkResponse
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.user.bookings.booking_address.UserBookingAddressAdapter
import com.satrango.ui.user.bookings.booking_date_time.BookingDateAndTimeScreen
import com.satrango.ui.user.bookings.booking_date_time.MonthsInterface
import com.satrango.ui.user.bookings.booking_date_time.MonthsModel
import com.satrango.ui.user.bookings.change_address.AddBookingAddressScreen
import com.satrango.ui.user.user_dashboard.UserDashboardScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.my_accounts.UserMyAccountScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_edit.models.single_move.MyJobPostSingleMoveEditReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.models.MyJobPostViewResModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_profile.UserProfileRepository
import com.satrango.ui.user.user_dashboard.drawer_menu.my_profile.UserProfileViewModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_profile.models.UserProfileReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.attachments.PostJobAttachmentsScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.models.post_job_single_move.PostJobSingleMoveReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.plans.UserPlanScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.post_job_multi_move.PostJobMultiMoveAddressScreen
import com.satrango.ui.user.user_dashboard.user_home_screen.user_location_change.UserLocationSelectionScreen
import com.satrango.utils.UserUtils
import com.satrango.utils.UserUtils.isProvider
import com.satrango.utils.snackBar
import com.satrango.utils.toast
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class PostJobAddressScreen : AppCompatActivity(), MonthsInterface {

    private lateinit var locationCallBack: LocationCallback
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var data: MyJobPostViewResModel
    private lateinit var viewModel: PostJobViewModel
    private lateinit var binding: ActivityPostJobAddressScreenBinding
    private lateinit var addressList: ArrayList<MonthsModel>
    private lateinit var progressDialog: BeautifulProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostJobAddressScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeToolBar()

        val factory = ViewModelFactory(PostJobRepository())
        viewModel = ViewModelProvider(this, factory)[PostJobViewModel::class.java]

        binding.apply {

            mapsBtn.setOnClickListener {
                UserLocationSelectionScreen.FROM_USER_POST_JOB_ADDRESS = true
                startActivity(Intent(this@PostJobAddressScreen, UserLocationSelectionScreen::class.java))
            }

            addNewAddress.setOnClickListener {
                UserUtils.setFromJobPost(this@PostJobAddressScreen, true)
                val intent = Intent(this@PostJobAddressScreen, AddBookingAddressScreen::class.java)
                startActivity(intent)
            }

            backBtn.setOnClickListener { onBackPressed() }

            nextBtn.setOnClickListener {
                validateFields()
            }

            if (UserUtils.getFromJobPostMultiMove(this@PostJobAddressScreen)) {
                btnLayout.visibility = View.VISIBLE
            } else {
                btnLayout.visibility = View.GONE
            }

        }
        initializeProgressDialog()
    }

    private fun validateFields() {
        var addressId = -1
        for (address in addressList) {
            if (address.isSelected) {
                addressId = address.day.toInt()
            }
        }
        if (addressId == -1) {
            snackBar(binding.nextBtn, "Select Address")
        } else {
            if (UserUtils.getFromJobPostMultiMove(this@PostJobAddressScreen)) {
                for (address in addressList) {
                    if (address.isSelected) {
                        UserUtils.addressList.add(MonthsModel(address.month, address.day, address.isSelected))
                    }
                }
                if (UserUtils.addressList.isNotEmpty()) {
                    startActivity(Intent(this@PostJobAddressScreen, PostJobMultiMoveAddressScreen::class.java))
                } else {
                    snackBar(binding.nextBtn, "Select Address")
                }
            } else {
                if (UserUtils.POST_JOB_AGAIN) {
                    postJobSingleMove(addressId)
                } else {
                    if (UserUtils.EDIT_MY_JOB_POST) {
                        updatePostJobSingleMove(addressId)
                    } else {
                        postJobSingleMove(addressId)
                    }
                }
            }
        }
    }

    private fun initializeToolBar() {
        val toolBar = binding.root.findViewById<View>(R.id.toolBar)
        toolBar.findViewById<ImageView>(R.id.toolBarBackBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarBackTVBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text = resources.getString(R.string.post_a_job)
    }

    private fun updatePostJobSingleMove(addressId: Int) {

        val requestBody = MyJobPostSingleMoveEditReqModel(
            addressId,
            PostJobAttachmentsScreen.encodedImages,
            UserUtils.bid_per,
            UserUtils.bid_range_id,
            UserUtils.bids_period,
            data.job_post_details.booking_id,
            data.job_post_details.post_created_on,
            UserUtils.estimate_time,
            UserUtils.estimateTypeId,
            UserUtils.job_description,
            RetrofitBuilder.USER_KEY,
            PostJobAttachmentsScreen.finalKeywords,
            PostJobAttachmentsScreen.finalLanguages,
            data.job_post_details.post_job_id.toInt(),
            UserUtils.scheduled_date,
            UserUtils.time_slot_from,
            UserUtils.title,
            UserUtils.getUserId(this).toInt(),
            UserUtils.getAddress(this),
            UserUtils.getCity(this),
            UserUtils.getCountry(this),
            UserUtils.getPostalCode(this),
            UserUtils.getState(this),
            UserUtils.getLatitude(this),
            UserUtils.getLongitude(this)
        )
//        Log.e("POST JOB:", Gson().toJson(requestBody))
        viewModel.updateSingleMoveMyJobPost(this, requestBody).observe(this) {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    showSuccessDialog("Updated")
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.nextBtn, it.message!!)
                }
            }
        }

    }

    private fun updateUI() {
        data = Gson().fromJson(UserUtils.EDIT_MY_JOB_POST_DETAILS, MyJobPostViewResModel::class.java)
        for (index in addressList.indices) {
            if (data.job_details[index].address_id == addressList[index].day) {
                addressList[index] = MonthsModel(addressList[index].month, addressList[index].day, true)
            }
        }
        binding.addressRv.layoutManager = LinearLayoutManager(this@PostJobAddressScreen)
        binding.addressRv.adapter = UserBookingAddressAdapter(addressList.distinctBy { data -> data.month } as java.util.ArrayList<MonthsModel>, this@PostJobAddressScreen, "AA")
    }

    @SuppressLint("SimpleDateFormat")
    private fun postJobSingleMove(addressId: Int) {
        var address = ""
        var city = ""
        var state = ""
        var country = ""
        var postalCode = ""
        var latitude = ""
        var longitude = ""

        if (addressId == 0) {
            address = UserUtils.getAddress(this)
            city = UserUtils.getCity(this)
            state = UserUtils.getState(this)
            country = UserUtils.getCountry(this)
            postalCode = UserUtils.getPostalCode(this)
            latitude = UserUtils.getLatitude(this)
            longitude = UserUtils.getLongitude(this)
        }

        val requestBody = PostJobSingleMoveReqModel(
            addressId,
            PostJobAttachmentsScreen.encodedImages,
            UserUtils.bid_per,
            UserUtils.bid_range_id,
            UserUtils.bids_period,
            SimpleDateFormat("yyyy/MM/dd hh:mm:ss").format(Date()),
            UserUtils.estimate_time,
            UserUtils.estimateTypeId,
            UserUtils.job_description,
            RetrofitBuilder.USER_KEY,
            PostJobAttachmentsScreen.finalKeywords,
            PostJobAttachmentsScreen.finalLanguages,
            UserUtils.scheduled_date,
            UserUtils.time_slot_from,
            UserUtils.title,
            UserUtils.getUserId(this).toInt(),
            address,
            city,
            state,
            country,
            postalCode,
            latitude,
            longitude
        )
//        toast(this, Gson().toJson(requestBody))
        viewModel.postJobSingleMove(this, requestBody).observe(this) {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    if (it.data!!.user_plan_id == "0") {
                        UserMyAccountScreen.FROM_MY_ACCOUNT = false
                        showSuccessDialog("Created")
//                        startActivity(Intent(this@PostJobAddressScreen, UserPlanScreen::class.java))
                    }
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.nextBtn, it.message!!)
                }
            }
        }

    }

    private fun loadAddress() {

        binding.recentLocation.text = UserUtils.getAddress(this)
        binding.recentLocationText.text = UserUtils.getCity(this)
        binding.recentSearch.setOnClickListener {
            binding.recentSearch.setBackgroundResource(R.drawable.blue_bg_sm)
            binding.recentLocation.setTextColor(resources.getColor(R.color.white))
            binding.recentLocationText.setTextColor(resources.getColor(R.color.white))
            if (isProvider(this)) {
                binding.recentSearch.setBackgroundResource(R.drawable.purple_bg_sm)
            }
            addressList.add(MonthsModel(UserUtils.getAddress(this) + ", " + UserUtils.getCity(this) + ", " + UserUtils.getPostalCode(this), "0", true))
            validateFields()
        }
        binding.rowLayout.setOnClickListener {
            binding.rowLayout.setBackgroundResource(R.drawable.blue_bg_sm)
            binding.currentLocation.setTextColor(resources.getColor(R.color.white))
            binding.currentLocationText.setTextColor(resources.getColor(R.color.white))
            if (isProvider(this)) {
                binding.rowLayout.setBackgroundResource(R.drawable.purple_bg_sm)
            }
            fetchLocation(this)
        }
        val factory = ViewModelFactory(UserProfileRepository())
        val profileViewModel = ViewModelProvider(this, factory)[UserProfileViewModel::class.java]
        val requestBody = UserProfileReqModel(
            RetrofitBuilder.USER_KEY,
            UserUtils.getUserId(this@PostJobAddressScreen).toInt(),
            UserUtils.getCity(this@PostJobAddressScreen)
        )
        profileViewModel.userProfileInfo(this, requestBody).observe(this) {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    addressList = arrayListOf()
                    val responseData = it.data!!
                    for (address in responseData.address) {
                        addressList.add(
                            MonthsModel(
                                address.locality + ", " + address.city + ", " + address.zipcode,
                                address.id,
                                false
                            )
                        )
                    }
                    binding.addressRv.layoutManager = LinearLayoutManager(this@PostJobAddressScreen)
                    binding.addressRv.adapter =
                        UserBookingAddressAdapter(addressList.distinctBy { data -> data.month } as java.util.ArrayList<MonthsModel>,
                            this@PostJobAddressScreen,
                            "AA")
                    progressDialog.dismiss()

                    if (UserUtils.EDIT_MY_JOB_POST) {
                        updateUI()
                    }

                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.nextBtn, it.message!!)
                }
            }
        }

    }

    override fun selectedMonth(position: Int, dateTime: String, listType: String) {
        if (UserUtils.getFromJobPostMultiMove(this)) {
            val tempAddress = arrayListOf<MonthsModel>()
            addressList.onEachIndexed { index, month ->
                if (dateTime == month.month) {
                    tempAddress.add(MonthsModel(month.month, month.day, !month.isSelected))
                } else {
                    tempAddress.add(MonthsModel(month.month, month.day, month.isSelected))
                }
            }
            addressList = tempAddress
        } else {
            val tempAddress = arrayListOf<MonthsModel>()
            addressList.onEachIndexed { index, month ->
                if (dateTime == month.month) {
                    tempAddress.add(MonthsModel(month.month, month.day, true))
                } else {
                    tempAddress.add(MonthsModel(month.month, month.day, false))
                }
            }
            addressList = tempAddress
        }
        binding.addressRv.adapter = UserBookingAddressAdapter(addressList.distinctBy { data -> data.month } as java.util.ArrayList<MonthsModel>, this@PostJobAddressScreen, "AA")
        if (!UserUtils.getFromJobPostMultiMove(this)) {
            validateFields()
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initializeProgressDialog() {
        progressDialog = BeautifulProgressDialog(this, BeautifulProgressDialog.withImage, resources.getString(R.string.loading))
        progressDialog.setGifLocation(Uri.parse("android.resource://${packageName}/${R.drawable.blue_loading}"))
        progressDialog.setLayoutColor(resources.getColor(R.color.white))
    }

    @SuppressLint("SetTextI18n")
    private fun showSuccessDialog(message: String) {
        val dialog = BottomSheetDialog(this)
        val dialogView = layoutInflater.inflate(R.layout.payment_success_dialog, null)
        val closeBtn = dialogView.findViewById<MaterialCardView>(R.id.closeBtn)
        val homeBtn = dialogView.findViewById<TextView>(R.id.closBtn)
        val text = dialogView.findViewById<TextView>(R.id.text)
        text.text = "Your Job is successfully $message"
        closeBtn.setOnClickListener {
            dialog.dismiss()
            startActivity(Intent(this, UserDashboardScreen::class.java))
        }
        homeBtn.setOnClickListener {
            dialog.dismiss()
            startActivity(Intent(this, UserDashboardScreen::class.java))
        }
        dialog.setCancelable(false)
        dialog.setContentView(dialogView)
        dialog.show()
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
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
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
            val city: String = address.get(0).locality
            val state: String = address.get(0).adminArea
            val country: String = address.get(0).countryName
            val postalCode: String = address.get(0).postalCode
            val knownName: String = address.get(0).featureName
            fusedLocationProviderClient.removeLocationUpdates(locationCallBack)
            UserUtils.setLatitude(context, latitude.toString())
            UserUtils.setLongitude(context, longitude.toString())
            UserUtils.setCity(context, city)
            UserUtils.setState(context, state)
            UserUtils.setCountry(context, country)
            UserUtils.setPostalCode(context, postalCode)
            UserUtils.setAddress(context, knownName)
            addressList.add(MonthsModel(UserUtils.getAddress(this) + ", " + UserUtils.getCity(this) + ", " + UserUtils.getPostalCode(this), "0", true))
            validateFields()
        } catch (e: Exception) {
            Toast.makeText(context, "Please Check you Internet Connection: ${e.message!!}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onResume() {
        super.onResume()
        loadAddress()
    }

}