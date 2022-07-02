package com.satrango.ui.user.bookings.booking_address

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.basusingh.beautifulprogressdialog.BeautifulProgressDialog
import com.bumptech.glide.Glide
import com.google.android.gms.location.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.card.MaterialCardView
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.gson.Gson
import com.satrango.R
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivityBookingAddressScreenBinding
import com.satrango.remote.NetworkResponse
import com.satrango.remote.RetrofitBuilder
import com.satrango.remote.fcm.FCMService
import com.satrango.ui.service_provider.provider_dashboard.dashboard.ProviderDashboard
import com.satrango.ui.user.bookings.booking_address.models.BlueCollarBookingReqModel
import com.satrango.ui.user.bookings.booking_address.models.SingleMoveBookingReqModel
import com.satrango.ui.user.bookings.booking_attachments.BookingAttachmentsScreen
import com.satrango.ui.user.bookings.booking_attachments.BookingMultiMoveAddressScreen
import com.satrango.ui.user.bookings.booking_date_time.BookingDateAndTimeScreen
import com.satrango.ui.user.bookings.booking_date_time.MonthsInterface
import com.satrango.ui.user.bookings.booking_date_time.MonthsModel
import com.satrango.ui.user.bookings.change_address.AddBookingAddressScreen
import com.satrango.ui.user.bookings.payment_screen.PaymentScreen
import com.satrango.ui.user.bookings.view_booking_details.models.ProviderResponseReqModel
import com.satrango.ui.user.user_dashboard.UserDashboardScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.my_profile.UserProfileRepository
import com.satrango.ui.user.user_dashboard.drawer_menu.my_profile.UserProfileViewModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_profile.models.UserProfileReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.PostJobAddressScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.PostJobTypeScreen
import com.satrango.ui.user.user_dashboard.search_service_providers.models.Data
import com.satrango.ui.user.user_dashboard.search_service_providers.models.SearchServiceProviderResModel
import com.satrango.ui.user.user_dashboard.search_service_providers.search_service_provider.SearchServiceProvidersScreen
import com.satrango.ui.user.user_dashboard.user_home_screen.user_location_change.UserLocationSelectionScreen
import com.satrango.utils.PermissionUtils
import com.satrango.utils.UserUtils
import com.satrango.utils.UserUtils.isProvider
import com.satrango.utils.snackBar
import com.satrango.utils.toast
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.round


class BookingAddressScreen : AppCompatActivity(), MonthsInterface {
//    , PaymentResultListener

    private lateinit var waitingDialog: BottomSheetDialog
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationCallBack: LocationCallback
    private lateinit var viewModel: BookingViewModel
    private lateinit var addressList: ArrayList<MonthsModel>
    private lateinit var progressDialog: BeautifulProgressDialog
    private lateinit var data: Data
    private lateinit var binding: ActivityBookingAddressScreenBinding
    private lateinit var responses: ArrayList<String>

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookingAddressScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeToolBar()
        initializeProgressDialog()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        responses = ArrayList()
        registerReceiver(myReceiver, IntentFilter(FCMService.INTENT_FILTER_ONE))

        val bookingFactory = ViewModelFactory(BookingRepository())
        viewModel = ViewModelProvider(this, bookingFactory)[BookingViewModel::class.java]

        if (UserUtils.getSelectedSPDetails(this).isNotEmpty()) {
            data = Gson().fromJson(UserUtils.getSelectedSPDetails(this), Data::class.java)
            updateUI(data)
        } else {
            binding.spCard.visibility = View.GONE
        }

        addressList = arrayListOf()
        binding.recentSearch.setOnClickListener {
            binding.recentSearch.setBackgroundResource(R.drawable.blue_bg_sm)
            binding.recentLocation.setTextColor(resources.getColor(R.color.white))
            binding.recentLocationText.setTextColor(resources.getColor(R.color.white))
            if (isProvider(this)) {
                binding.recentSearch.setBackgroundResource(R.drawable.purple_bg_sm)
            }
            addressList.add(
                MonthsModel(
                    UserUtils.getAddress(this) + ", " + UserUtils.getCity(this) + ", " + UserUtils.getPostalCode(
                        this
                    ), "0", true
                )
            )
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
            UserUtils.getUserId(this@BookingAddressScreen).toInt(),
            UserUtils.getCity(this@BookingAddressScreen)
        )
        profileViewModel.userProfileInfo(this, requestBody).observe(this) {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
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
                    binding.addressRv.layoutManager = LinearLayoutManager(this@BookingAddressScreen)
                    binding.addressRv.adapter =
                        UserBookingAddressAdapter(addressList.distinctBy { data -> data.month } as java.util.ArrayList<MonthsModel>,
                            this@BookingAddressScreen,
                            "AA")
                    progressDialog.dismiss()
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.nextBtn, it.message!!)
                }
            }
        }

        binding.apply {

            mapsBtn.setOnClickListener {
                UserLocationSelectionScreen.FROM_USER_BOOKING_ADDRESS = true
                startActivity(
                    Intent(
                        this@BookingAddressScreen,
                        UserLocationSelectionScreen::class.java
                    )
                )
            }

            addNewAddress.setOnClickListener {
                UserUtils.setFromJobPost(this@BookingAddressScreen, false)
                val intent = Intent(this@BookingAddressScreen, AddBookingAddressScreen::class.java)
                if (!UserUtils.getFromInstantBooking(this@BookingAddressScreen)) {
                    intent.putExtra(getString(R.string.service_provider), data)
                }
                startActivity(intent)
            }

            nextBtn.setOnClickListener {
                validateFields()
            }

            backBtn.setOnClickListener {
                onBackPressed()
            }
        }

    }

    private fun validateFields() {
        UserUtils.temp_address_id = "0"
        UserUtils.address_id = "0"
        for (address in addressList) {
            if (address.isSelected) {
                if (UserUtils.getTempAddressId(this@BookingAddressScreen) == address.day) {
                    UserUtils.temp_address_id =
                        UserUtils.getTempAddressId(this@BookingAddressScreen)
                    UserUtils.address_id = "0"
                } else {
                    UserUtils.temp_address_id = "0"
                    UserUtils.address_id = address.day
                }
            }
        }

        if (UserUtils.temp_address_id.isEmpty() && UserUtils.address_id.isEmpty()) {
            snackBar(binding.nextBtn, "Select Address to Provider Service")
        } else {
            if (UserUtils.getFromInstantBooking(this@BookingAddressScreen)) {

                val calender = Calendar.getInstance()
                UserUtils.scheduled_date = currentDateAndTime().split(" ")[0]
                UserUtils.started_at = currentDateAndTime() + ":00:00"
                UserUtils.time_slot_from =
                    (calender.get(Calendar.HOUR_OF_DAY) + 1).toString() + ":00:00"

                when (Gson().fromJson(
                    UserUtils.getSelectedSPDetails(this@BookingAddressScreen),
                    Data::class.java
                ).category_id) {
                    "1" -> {
                        bookSingleMoveServiceProvider()
                    }
                    "2" -> {
                        bookBlueCollarServiceProvider()
                    }
                    "3" -> {
                        UserUtils.addressList = ArrayList()
                        addressList =
                            addressList.distinctBy { monthsModel: MonthsModel -> monthsModel.month } as ArrayList<MonthsModel>
                        addressList.forEach {
                            if (it.isSelected) {
                                UserUtils.addressList.add(it)
                            }
                        }
                        if (UserUtils.addressList.isEmpty()) {
                            snackBar(binding.nextBtn, "Please Select Addresses")
                        } else {
//                            Log.e("ADDRESS:", Gson().toJson(UserUtils.addressList))
                            UserUtils.addressList =
                                UserUtils.addressList.distinctBy { monthsModel: MonthsModel -> monthsModel.month } as ArrayList<MonthsModel>
                            startActivity(Intent(this, BookingMultiMoveAddressScreen::class.java))
                        }
                    }
                }
            } else {
                when (data.category_id) {
                    "1" -> {
                        bookSingleMoveServiceProvider()
                    }
                    "3" -> {
                        UserUtils.addressList = ArrayList()
                        addressList.forEach {
                            if (it.isSelected) {
                                UserUtils.addressList.add(it)
                            }
                        }
                        if (UserUtils.addressList.isEmpty()) {
                            snackBar(binding.nextBtn, "Please Select Addresses")
                        } else {
                            UserUtils.addressList =
                                UserUtils.addressList.distinctBy { monthsModel: MonthsModel -> monthsModel.month } as ArrayList<MonthsModel>
                            val intent = Intent(
                                this@BookingAddressScreen,
                                BookingMultiMoveAddressScreen::class.java
                            )
                            startActivity(intent)
                        }
                    }
                }
            }
        }
    }

    private fun initializeToolBar() {
        val toolBar = binding.root.findViewById<View>(R.id.toolBar)
        toolBar.findViewById<ImageView>(R.id.toolBarBackBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarBackTVBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text =
            resources.getString(R.string.booking)
        val profilePic = toolBar.findViewById<CircleImageView>(R.id.toolBarImage)
        Glide.with(profilePic).load(UserUtils.getUserProfilePic(this)).into(profilePic)
    }

    @SuppressLint("SimpleDateFormat")
    private fun bookSingleMoveServiceProvider() {
        var spId = 0
        var finalAmount = 0
        var cgst = "0"
        var sgst = "0"
        if (UserUtils.getSelectedSPDetails(this).isNotEmpty()) {
            spId = Gson().fromJson(
                UserUtils.getSelectedSPDetails(this),
                Data::class.java
            ).users_id.toInt()
            finalAmount =
                Gson().fromJson(UserUtils.getSelectedSPDetails(this), Data::class.java).final_amount.toInt()
            cgst =
                Gson().fromJson(UserUtils.getSelectedSPDetails(this), Data::class.java).CGST_amount
            sgst =
                Gson().fromJson(UserUtils.getSelectedSPDetails(this), Data::class.java).SGST_amount
        }
        if (UserUtils.getFromInstantBooking(this)) {
            var address = ""
            var city = ""
            var state = ""
            var country = ""
            var postalCode = ""
            var latitude = ""
            var longitude = ""

            if (UserUtils.address_id == "0") {
                address = UserUtils.getAddress(this)
                city = UserUtils.getCity(this)
                state = UserUtils.getState(this)
                country = UserUtils.getCountry(this)
                postalCode = UserUtils.getPostalCode(this)
                latitude = UserUtils.getLatitude(this)
                longitude = UserUtils.getLongitude(this)
            }

            if (UserUtils.time_slot_to.isNotEmpty()) {
                if (UserUtils.time_slot_to.replace("\n", "").split(":")[0].toInt() == 24) {
                    UserUtils.time_slot_to = "00:00:00"
                }
            }

            val requestBody = SingleMoveBookingReqModel(
                UserUtils.address_id.toInt(),
                finalAmount.toString(),
                BookingAttachmentsScreen.encodedImages,
                SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date()),
                1,
                1,
                UserUtils.job_description,
                RetrofitBuilder.USER_KEY,
                UserUtils.scheduled_date,
                spId,
                UserUtils.time_slot_from,
                UserUtils.temp_address_id.toInt(),
                UserUtils.time_slot_from,
                UserUtils.time_slot_to.replace("\n", ""),
                UserUtils.getUserId(this).toInt(),
                address,
                city,
                state,
                country,
                postalCode,
                latitude,
                longitude,
                cgst,
                sgst,
                Gson().fromJson(
                    UserUtils.getSelectedSPDetails(this),
                    Data::class.java
                ).profession_id
            )
//            toast(this, "SINGLEMOVE:" + Gson().toJson(requestBody))
//            Log.e("SINGLE MOVE INSTANTLY:", Gson().toJson(requestBody))
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val bookingResponse = RetrofitBuilder.getUserRetrofitInstance()
                        .bookSingleMoveProvider(requestBody)
                    val jsonResponse = JSONObject(bookingResponse.string())
                    progressDialog.show()
                    if (jsonResponse.getInt("status") == 200) {
                        progressDialog.dismiss()
                        if (UserUtils.getFromInstantBooking(this@BookingAddressScreen)) {
                            Log.e("SINGLE MOVE RESPONSE", Gson().toJson(jsonResponse))
                            UserUtils.saveBookingId(this@BookingAddressScreen, jsonResponse.getInt("booking_id").toString())
                            UserUtils.saveBookingRefId(this@BookingAddressScreen, jsonResponse.getString("booking_ref_id"))
                            val hasToken = UserUtils.sendFCMtoAllServiceProviders(this@BookingAddressScreen, UserUtils.getBookingId(this@BookingAddressScreen), "user", "accepted|${UserUtils.bookingType}")
                            if (hasToken.isNotEmpty()) {
                                toast(this@BookingAddressScreen, hasToken)
                            } else {
                                showWaitingForSPConfirmationDialog()
                            }
                        } else {
                            Log.e("SINGLE MOVE SELECTED", Gson().toJson(jsonResponse))
                            val hasToken = UserUtils.sendFCMtoSelectedServiceProvider(this@BookingAddressScreen, UserUtils.getBookingId(this@BookingAddressScreen), "user")
                            if (hasToken.isNotEmpty()) {
                                toast(this@BookingAddressScreen, hasToken)
                            } else {
                                showWaitingForSPConfirmationDialog()
                            }
                        }
                    } else {
                        progressDialog.dismiss()
                        toast(this@BookingAddressScreen, jsonResponse.getString("status_message"))
                    }
                } catch (e: java.lang.Exception) {
                    toast(this@BookingAddressScreen, e.message!!)
                }
            }
        } else {

            var address = ""
            var city = ""
            var state = ""
            var country = ""
            var postalCode = ""
            var latitude = ""
            var longitude = ""

            if (UserUtils.address_id == "0") {
                address = UserUtils.getAddress(this)
                city = UserUtils.getCity(this)
                state = UserUtils.getState(this)
                country = UserUtils.getCountry(this)
                postalCode = UserUtils.getPostalCode(this)
                latitude = UserUtils.getLatitude(this)
                longitude = UserUtils.getLongitude(this)
            }

            if (UserUtils.time_slot_to.isNotEmpty()) {
                if (UserUtils.time_slot_to.replace("\n", "").split(":")[0].toInt() == 24) {
                    UserUtils.time_slot_to = "00:00:00"
                }
            }

            val requestBody = SingleMoveBookingReqModel(
                UserUtils.address_id.toInt(),
                data.final_amount.toString(),
                BookingAttachmentsScreen.encodedImages,
                SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date()),
                1,
                1,
                UserUtils.job_description,
                RetrofitBuilder.USER_KEY,
                UserUtils.scheduled_date,
                data.users_id.toInt(),
                UserUtils.time_slot_from,
                UserUtils.temp_address_id.toInt(),
                UserUtils.time_slot_from,
                UserUtils.time_slot_to.replace("\n", ""),
                UserUtils.getUserId(this).toInt(),
                address,
                city,
                state,
                country,
                postalCode,
                latitude,
                longitude,
                cgst,
                sgst,
                Gson().fromJson(
                    UserUtils.getSelectedSPDetails(this),
                    Data::class.java
                ).profession_id
            )
//            toast(this, Gson().toJson(requestBody))
//            Log.e("SINGLE MOVE SELECTION", Gson().toJson(requestBody))
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val bookingResponse = RetrofitBuilder.getUserRetrofitInstance()
                        .bookSingleMoveProvider(requestBody)
                    val jsonResponse = JSONObject(bookingResponse.string())
                    progressDialog.show()
                    if (jsonResponse.getInt("status") == 200) {
                        progressDialog.dismiss()
                        UserUtils.saveBookingId(
                            this@BookingAddressScreen,
                            jsonResponse.getInt("booking_id").toString()
                        )
                        UserUtils.saveBookingRefId(
                            this@BookingAddressScreen,
                            jsonResponse.getString("booking_ref_id")
                        )
//                        UserUtils.saveTxnToken(this@BookingAddressScreen, jsonResponse.getString("txn_id"))
//                        UserUtils.saveOrderId(this@BookingAddressScreen, jsonResponse.getString("order_id"))
                        if (UserUtils.getFromInstantBooking(this@BookingAddressScreen)) {
                            if (PermissionUtils.isNetworkConnected(this@BookingAddressScreen)) {
                                val hasToken = UserUtils.sendFCMtoAllServiceProviders(
                                    this@BookingAddressScreen,
                                    UserUtils.getBookingId(this@BookingAddressScreen),
                                    "user",
                                    UserUtils.bookingType
                                )
                                if (hasToken.isNotEmpty()) {
                                    toast(this@BookingAddressScreen, hasToken)
                                } else {
                                    showWaitingForSPConfirmationDialog()
                                }
                            } else {
                                snackBar(binding.nextBtn, "No Internet Connection!")
                            }
                        } else {
                            val hasToken = UserUtils.sendFCMtoSelectedServiceProvider(
                                this@BookingAddressScreen,
                                UserUtils.getBookingId(this@BookingAddressScreen),
                                "user"
                            )
                            if (hasToken.isNotEmpty()) {
                                toast(this@BookingAddressScreen, hasToken)
                            } else {
                                showWaitingForSPConfirmationDialog()
                            }
                        }
                    } else {
                        progressDialog.dismiss()
                        toast(this@BookingAddressScreen, jsonResponse.getString("status_message"))
                    }
                } catch (e: java.lang.Exception) {
                    toast(this@BookingAddressScreen, e.message!!)
                }
            }
//            viewModel.singleMoveBooking(this, requestBody).observe(this) {
//                when (it) {
//                    is NetworkResponse.Loading -> {
//                        progressDialog.show()
//                    }
//                    is NetworkResponse.Success -> {
//                        progressDialog.dismiss()
//                        showWaitingForSPConfirmationDialog()
//                        if (UserUtils.getFromInstantBooking(this)) {
//                            if (PermissionUtils.isNetworkConnected(this)) {
//                                UserUtils.sendFCMtoAllServiceProviders(
//                                    this,
//                                    UserUtils.getBookingId(this),
//                                    "user",
//                                    UserUtils.bookingType
//                                )
//                            } else {
//                                snackBar(binding.nextBtn, "No Internet Connection!")
//                            }
//                        } else {
//                            UserUtils.sendFCMtoSelectedServiceProvider(
//                                this,
//                                UserUtils.getBookingId(this),
//                                "user"
//                            )
//                        }
//                    }
//                    is NetworkResponse.Failure -> {
//                        progressDialog.dismiss()
//                        snackBar(binding.nextBtn, it.message!!)
//                    }
//                }
//            }

        }

    }

    @SuppressLint("SetTextI18n")
    private fun showWaitingForSPConfirmationDialog() {
        waitingDialog = BottomSheetDialog(this)
        waitingDialog.setCancelable(false)
        val dialogView = layoutInflater.inflate(R.layout.waiting_for_sp_confirmation_dialog, null)
        val progressBar = dialogView.findViewById<CircularProgressIndicator>(R.id.progressBar)
        val time = dialogView.findViewById<TextView>(R.id.time)
        val closeBtn = dialogView.findViewById<MaterialCardView>(R.id.closeBtn)
        closeBtn.setOnClickListener {
            UserUtils.sendFCMtoAllServiceProviders(
                this,
                UserUtils.getBookingId(this),
                "accepted",
                "accept|${UserUtils.bookingType}|selected"
            )
            finish()
            startActivity(intent)
            waitingDialog.dismiss()
        }
        var minutes = 2
        var seconds = 59
        val mainHandler = Handler(Looper.getMainLooper())
        var progressTime = 180
        mainHandler.post(object : Runnable {
            @SuppressLint("SimpleDateFormat")
            override fun run() {
                if (seconds < 10) {
                    time.text = "0$minutes:0$seconds"
                } else {
                    time.text = "0$minutes:$seconds"
                }

                progressTime -= 1
                progressBar.progress = progressTime

                seconds -= 1
                if (minutes == 0 && seconds == 0) {
                    UserUtils.sendFCMtoAllServiceProviders(
                        this@BookingAddressScreen,
                        UserUtils.getBookingId(this@BookingAddressScreen),
                        "accepted",
                        "accept|${UserUtils.bookingType}|selected"
                    )
                    waitingDialog.dismiss()
                    try {
                        val bookingFactory = ViewModelFactory(BookingRepository())
                        val bookingViewModel = ViewModelProvider(
                            this@BookingAddressScreen,
                            bookingFactory
                        )[BookingViewModel::class.java]
                        val requestBody = ProviderResponseReqModel(
                            data.final_amount.toString(),
                            UserUtils.getBookingId(this@BookingAddressScreen).toInt(),
                            SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date()),
                            "",
                            RetrofitBuilder.USER_KEY,
                            data.users_id.toInt(),
                            6,
                            UserUtils.getUserId(this@BookingAddressScreen).toInt()
                        )
//                        toast(this@BookingAddressScreen, Gson().toJson(requestBody))
                        bookingViewModel.setProviderResponse(this@BookingAddressScreen, requestBody)
                            .observe(this@BookingAddressScreen) {
                                when (it) {
                                    is NetworkResponse.Loading -> {
                                        progressDialog.show()
                                    }
                                    is NetworkResponse.Success -> {
                                        progressDialog.dismiss()
                                        UserUtils.saveFromFCMService(
                                            this@BookingAddressScreen,
                                            false
                                        )
                                        if (FCMService.notificationManager != null) {
                                            FCMService.notificationManager.cancelAll()
                                        }
                                        ProviderDashboard.bookingId = "0"
                                        if (ProviderDashboard.bottomSheetDialog != null) {
                                            if (ProviderDashboard.bottomSheetDialog!!.isShowing) {
                                                toast(this@BookingAddressScreen, "Closed from Booking Address")
                                                ProviderDashboard.bottomSheetDialog!!.dismiss()
                                            }
                                        }
                                        weAreSorryDialog()
                                    }
                                    is NetworkResponse.Failure -> {
                                        progressDialog.dismiss()
                                        toast(this@BookingAddressScreen, it.message!!)
                                    }
                                }
                            }
                    } catch (e: java.lang.Exception) {
                    }
//                    Checkout.preload(applicationContext)
//                    weAreSorryDialog()
                }
                if (seconds == 0) {
                    seconds = 59
                    minutes -= 1
                }
                if (UserUtils.getProviderAction(this@BookingAddressScreen)
                        .split("|")[0].isNotEmpty()
                ) {
                    waitingDialog.dismiss()
                    if (UserUtils.getProviderAction(this@BookingAddressScreen)
                            .split("|")[0].trim() == "accept"
                    ) {
                        serviceProviderAcceptDialog(this@BookingAddressScreen)
                        UserUtils.sendFCMtoAllServiceProviders(
                            this@BookingAddressScreen,
                            "accepted",
                            "accepted",
                            "accepted|${UserUtils.bookingType}"
                        )
                    } else {
                        serviceProviderRejectDialog(this@BookingAddressScreen)
                        UserUtils.sendFCMtoAllServiceProviders(
                            this@BookingAddressScreen,
                            "accepted",
                            "accepted",
                            "accepted|${UserUtils.bookingType}"
                        )
                    }
                }
                mainHandler.postDelayed(this, 1000)
            }
        })
        waitingDialog.setContentView(dialogView)
        if (!(this as Activity).isFinishing) {
            waitingDialog.show()
        }
    }

    private fun weAreSorryDialog() {
        val dialog = BottomSheetDialog(this)
        val dialogView = layoutInflater.inflate(R.layout.no_service_provider_found, null)
        val yesBtn = dialogView.findViewById<TextView>(R.id.yesBtn)
        val noBtn = dialogView.findViewById<TextView>(R.id.noBtn)
        val closeBtn = dialogView.findViewById<MaterialCardView>(R.id.closeBtn)
        closeBtn.setOnClickListener {
            startActivity(Intent(this, SearchServiceProvidersScreen::class.java))
            dialog.dismiss()
        }
        yesBtn.setOnClickListener {
            snackBar(yesBtn, "Post the Job")
            dialog.dismiss()
            finish()
            startActivity(Intent(this, PostJobTypeScreen::class.java))
        }
        noBtn.setOnClickListener {
            dialog.dismiss()
            showWaitingForSPConfirmationDialog()
        }
        dialog.setContentView(dialogView)
        dialog.setCancelable(false)
        if (!(this as Activity).isFinishing) {
            dialog.show()
        }
    }

    override fun selectedMonth(position: Int, dateTime: String, listType: String) {
        val tempAddress = arrayListOf<MonthsModel>()
        if (UserUtils.getFromInstantBooking(this)) {
            if (UserUtils.getSelectedKeywordCategoryId(this) == "3") {
                addressList.onEachIndexed { index, month ->
                    if (month.month == dateTime) {
                        tempAddress.add(MonthsModel(month.month, month.day, !month.isSelected))
                    } else {
                        tempAddress.add(MonthsModel(month.month, month.day, month.isSelected))
                    }
                }
                addressList = tempAddress
            } else {
                addressList.onEachIndexed { index, month ->
                    if (month.month == dateTime) {
                        tempAddress.add(MonthsModel(month.month, month.day, true))
                    } else {
                        tempAddress.add(MonthsModel(month.month, month.day, false))
                    }
                }
                addressList = tempAddress
                validateFields()
            }
        } else {
            if (data.category_id == "3") {
                addressList.onEachIndexed { index, month ->
                    if (month.month == dateTime) {
                        tempAddress.add(MonthsModel(month.month, month.day, !month.isSelected))
                    } else {
                        tempAddress.add(MonthsModel(month.month, month.day, month.isSelected))
                    }
                }
                addressList = tempAddress
            } else {
                addressList.onEachIndexed { index, month ->
                    if (month.month == dateTime) {
                        tempAddress.add(MonthsModel(month.month, month.day, true))
                    } else {
                        tempAddress.add(MonthsModel(month.month, month.day, false))
                    }
                }
                addressList = tempAddress
                validateFields()
            }
        }
        binding.addressRv.adapter =
            UserBookingAddressAdapter(addressList.distinctBy { data -> data.month } as java.util.ArrayList<MonthsModel>,
                this@BookingAddressScreen,
                "AA")
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initializeProgressDialog() {
        progressDialog = BeautifulProgressDialog(
            this,
            BeautifulProgressDialog.withGIF,
            resources.getString(R.string.loading)
        )
        progressDialog.setGifLocation(Uri.parse("android.resource://${packageName}/${R.drawable.blue_loading}"))
        progressDialog.setLayoutColor(resources.getColor(R.color.progressDialogColor))
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI(data: Data) {
        binding.userName.text = "${data.fname} ${data.lname}"
        binding.occupation.text = data.profession
        binding.costPerHour.text = "Rs. ${round(data.final_amount.toDouble()).toInt()}/-"
        Glide.with(this).load(data.profile_pic).into(binding.profilePic)
        if (UserUtils.getSelectedKeywordCategoryId(this) == "3") {
            binding.btnsLayout.visibility = View.VISIBLE
        } else {
            binding.btnsLayout.visibility = View.GONE
        }
    }

    override fun onBackPressed() {
        if (UserUtils.getFromInstantBooking(this)) {
            startActivity(Intent(this, BookingAttachmentsScreen::class.java))
        } else {
            when (data.category_id) {
                "1" -> {
                    finish()
                    val intent = Intent(this, BookingAttachmentsScreen::class.java)
                    intent.putExtra(getString(R.string.service_provider), data)
                    startActivity(intent)
                }
                "3" -> {
                    finish()
                    isProvider(this, false)
                    val intent = Intent(this, BookingDateAndTimeScreen::class.java)
                    intent.putExtra(getString(R.string.service_provider), data)
                    startActivity(intent)
                }
            }
        }
    }

    private fun serviceProviderAcceptDialog(context: Context) {
        val dialog = BottomSheetDialog(context)
        dialog.setCancelable(false)
        val dialogView = layoutInflater.inflate(R.layout.sp_accepted_dialog, null)
        val closeBtn = dialogView.findViewById<MaterialCardView>(R.id.closeBtn)
        closeBtn.setOnClickListener {
            dialog.dismiss()
        }
        Handler().postDelayed({
//            makePayment()
            PaymentScreen.FROM_USER_BOOKING_ADDRESS = true
            PaymentScreen.FROM_USER_PLANS = false
            PaymentScreen.FROM_PROVIDER_PLANS = false
            PaymentScreen.FROM_PROVIDER_BOOKING_RESPONSE = false
            PaymentScreen.FROM_USER_SET_GOALS = false
            PaymentScreen.amount = data.final_amount
            PaymentScreen.userId = data.users_id.toInt()
            startActivity(Intent(this, PaymentScreen::class.java))
        }, 3000)
        dialog.setContentView(dialogView)
        dialog.show()
    }

    private fun serviceProviderRejectDialog(context: Context) {
        val dialog = BottomSheetDialog(context)
        dialog.setCancelable(false)
        val dialogView = layoutInflater.inflate(R.layout.rejected_by_service_provider_dialog, null)
        val closeBtn = dialogView.findViewById<MaterialCardView>(R.id.closeBtn)
        val yesBtn = dialogView.findViewById<TextView>(R.id.yesBtn)
        val noBtn = dialogView.findViewById<TextView>(R.id.noBtn)
        closeBtn.setOnClickListener {
            dialog.dismiss()
        }
        yesBtn.setOnClickListener {
            snackBar(yesBtn, "Post the Job")
            dialog.dismiss()
            finish()
            startActivity(Intent(this, UserDashboardScreen::class.java))
        }
        noBtn.setOnClickListener {
            dialog.dismiss()
        }
        dialog.setContentView(dialogView)
        dialog.show()
    }

    @SuppressLint("SimpleDateFormat")
    private fun bookBlueCollarServiceProvider() {

        var finalAmount = 0
        var spId = "0"
        var cgst = "0"
        var sgst = "0"
        if (UserUtils.getSelectedSPDetails(this).isNotEmpty()) {
            spId = Gson().fromJson(UserUtils.getSelectedSPDetails(this), Data::class.java).users_id
            finalAmount =
                Gson().fromJson(UserUtils.getSelectedSPDetails(this), Data::class.java).final_amount.toInt()
            cgst =
                Gson().fromJson(UserUtils.getSelectedSPDetails(this), Data::class.java).CGST_amount
            sgst =
                Gson().fromJson(UserUtils.getSelectedSPDetails(this), Data::class.java).SGST_amount
        }

        val requestBody = BlueCollarBookingReqModel(
            finalAmount.toString(),
            BookingAttachmentsScreen.encodedImages,
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date()),
            1,
            1,
            UserUtils.job_description,
            RetrofitBuilder.USER_KEY,
            UserUtils.scheduled_date,
            spId.toInt(),
            UserUtils.time_slot_from,
            UserUtils.time_slot_from,
            UserUtils.time_slot_to.replace("\n", ""),
            UserUtils.getUserId(this).toInt(),
            cgst,
            sgst,
            Gson().fromJson(UserUtils.getSelectedSPDetails(this), Data::class.java).profession_id
        )
//        Log.e("BLUE COLLAR MOVE", Gson().toJson(requestBody))
        toast(this, Gson().toJson(requestBody))
        viewModel.blueCollarBooking(this, requestBody).observe(this) {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    val jsonResponse = JSONObject(it.data!!)
//                    UserUtils.saveTxnToken(this@BookingAddressScreen, jsonResponse.getString("txn_id"))
//                    UserUtils.saveOrderId(this@BookingAddressScreen, jsonResponse.getString("order_id"))
                    if (UserUtils.getFromInstantBooking(this)) {
                        if (PermissionUtils.isNetworkConnected(this)) {
                            val hasToken = UserUtils.sendFCMtoAllServiceProviders(
                                this,
                                UserUtils.getBookingId(this),
                                "user",
                                UserUtils.bookingType
                            )
                            if (hasToken.isNotEmpty()) {
                                toast(this, hasToken)
                            } else {
                                showWaitingForSPConfirmationDialog()
                            }
                        } else {
                            snackBar(binding.nextBtn, "No Internet Connection!")
                        }
                    } else {
                        val hasToken = UserUtils.sendFCMtoSelectedServiceProvider(
                            this,
                            UserUtils.getBookingId(this),
                            "user"
                        )
                        if (hasToken.isNotEmpty()) {
                            toast(this, hasToken)
                        } else {
                            showWaitingForSPConfirmationDialog()
                        }
                    }
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.nextBtn, "BLUE COLLAR" + it.message!!)
                }
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun currentDateAndTime(): String {
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
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
    private fun fetchLocationDetails(
        context: Context,
        latitude: Double,
        longitude: Double
    ) {
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
            addressList.add(
                MonthsModel(
                    UserUtils.getAddress(this) + ", " + UserUtils.getCity(this) + ", " + UserUtils.getPostalCode(
                        this
                    ), "0", true
                )
            )
//            if (!waitingDialog.isShowing) {
            validateFields()
//            }
        } catch (e: Exception) {
            Toast.makeText(context, "Please Check you Internet Connection!:${e.message!!}", Toast.LENGTH_LONG)
                .show()
        }
    }

    override fun onResume() {
        super.onResume()
        binding.recentLocation.text = UserUtils.getAddress(this)
        binding.recentLocationText.text = UserUtils.getCity(this)
    }

    private val myReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        @SuppressLint("SimpleDateFormat")
        @RequiresApi(Build.VERSION_CODES.O)
        override fun onReceive(context: Context, intent: Intent) {
            val spIds = intent.getStringExtra(getString(R.string.sp_id))!!
            responses.add(spIds)
            val spDetails = Gson().fromJson(
                UserUtils.getSelectedAllSPDetails(context),
                SearchServiceProviderResModel::class.java
            )
            if (responses.size == spDetails.data.size) {
                waitingDialog.dismiss()
                val bookingFactory = ViewModelFactory(BookingRepository())
                val bookingViewModel = ViewModelProvider(
                    this@BookingAddressScreen,
                    bookingFactory
                )[BookingViewModel::class.java]
                val requestBody = ProviderResponseReqModel(
                    data.final_amount.toString(),
                    UserUtils.getBookingId(this@BookingAddressScreen).toInt(),
                    SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date()),
                    "",
                    RetrofitBuilder.USER_KEY,
                    data.users_id.toInt(),
                    6,
                    UserUtils.getUserId(this@BookingAddressScreen).toInt()
                )
//                toast(this@BookingAddressScreen, Gson().toJson(requestBody))
                bookingViewModel.setProviderResponse(this@BookingAddressScreen, requestBody)
                    .observe(this@BookingAddressScreen) {
                        when (it) {
                            is NetworkResponse.Loading -> {
                                progressDialog.show()
                            }
                            is NetworkResponse.Success -> {
                                progressDialog.dismiss()
                                UserUtils.saveFromFCMService(this@BookingAddressScreen, false)
                                if (FCMService.notificationManager != null) {
                                    FCMService.notificationManager.cancelAll()
                                }
                                ProviderDashboard.bookingId = "0"
                                if (ProviderDashboard.bottomSheetDialog != null) {
                                    if (ProviderDashboard.bottomSheetDialog!!.isShowing) {
                                        ProviderDashboard.bottomSheetDialog!!.dismiss()
                                    }
                                }
                                weAreSorryDialog()
                            }
                            is NetworkResponse.Failure -> {
                                progressDialog.dismiss()
                                toast(this@BookingAddressScreen, it.message!!)
                            }
                        }
                    }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(this)
            .registerReceiver((myReceiver), IntentFilter(FCMService.INTENT_FILTER_ONE))
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(myReceiver)
    }

}