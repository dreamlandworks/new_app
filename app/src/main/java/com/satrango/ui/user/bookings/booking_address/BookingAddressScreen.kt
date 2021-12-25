package com.satrango.ui.user.bookings.booking_address

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.basusingh.beautifulprogressdialog.BeautifulProgressDialog
import com.bumptech.glide.Glide
import com.google.android.gms.location.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.card.MaterialCardView
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.gson.Gson
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import com.satrango.R
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivityBookingAddressScreenBinding
import com.satrango.remote.NetworkResponse
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.user.bookings.PaymentScreen
import com.satrango.ui.user.bookings.booking_address.models.BlueCollarBookingReqModel
import com.satrango.ui.user.bookings.booking_address.models.SingleMoveBookingReqModel
import com.satrango.ui.user.bookings.booking_attachments.BookingAttachmentsScreen
import com.satrango.ui.user.bookings.booking_attachments.models.MultiMoveReqModel
import com.satrango.ui.user.bookings.booking_date_time.BookingDateAndTimeScreen
import com.satrango.ui.user.bookings.booking_date_time.MonthsInterface
import com.satrango.ui.user.bookings.booking_date_time.MonthsModel
import com.satrango.ui.user.bookings.change_address.AddBookingAddressScreen
import com.satrango.ui.user.bookings.provider_response.PaymentConfirmReqModel
import com.satrango.ui.user.user_dashboard.UserDashboardScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.my_profile.UserProfileRepository
import com.satrango.ui.user.user_dashboard.drawer_menu.my_profile.UserProfileViewModel
import com.satrango.ui.user.user_dashboard.search_service_providers.models.Data
import com.satrango.ui.user.user_dashboard.search_service_providers.search_service_provider.SearchServiceProvidersScreen
import com.satrango.utils.PermissionUtils
import com.satrango.utils.UserUtils
import com.satrango.utils.snackBar
import com.satrango.utils.toast
import de.hdodenhof.circleimageview.CircleImageView
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.round

class BookingAddressScreen : AppCompatActivity(), MonthsInterface {
//    , PaymentResultListener

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationCallBack: LocationCallback
    private lateinit var viewModel: BookingViewModel
    private lateinit var addressList: ArrayList<MonthsModel>
    private lateinit var progressDialog: BeautifulProgressDialog
    private lateinit var data: Data
    private lateinit var binding: ActivityBookingAddressScreenBinding

//    companion object {
//        var data: Data? = null
//    }

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookingAddressScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeToolBar()
        initializeProgressDialog()

        val bookingFactory = ViewModelFactory(BookingRepository())
        viewModel = ViewModelProvider(this, bookingFactory)[BookingViewModel::class.java]

        if (UserUtils.data != null) {
            data = UserUtils.data!!
            updateUI(data)
        } else {
            binding.spCard.visibility = View.GONE
        }
//        if (!UserUtils.getFromInstantBooking(this)) {
//            data = intent.getSerializableExtra(getString(R.string.service_provider)) as Data
//        }

//        if (data != null) {
//            updateUI(data)
//        }
//        else {
//            binding.spCard.visibility = View.GONE
//        }

        addressList = arrayListOf()
        binding.recentLocation.text = UserUtils.getAddress(this)
        binding.recentLocationText.text = UserUtils.getCity(this)
        binding.recentSearch.setOnClickListener {
            binding.recentSearch.setBackgroundResource(R.drawable.blue_bg_sm)
            binding.recentLocation.setTextColor(resources.getColor(R.color.white))
            binding.recentLocationText.setTextColor(resources.getColor(R.color.white))
            if (BookingDateAndTimeScreen.FROM_PROVIDER) {
                binding.recentSearch.setBackgroundResource(R.drawable.purple_bg_sm)
            }
            addressList.add(MonthsModel(UserUtils.getAddress(this) + ", " + UserUtils.getCity(this) + ", " + UserUtils.getPostalCode(this), "0", true))
            validateFields()
        }
        binding.rowLayout.setOnClickListener {
            binding.rowLayout.setBackgroundResource(R.drawable.blue_bg_sm)
            binding.currentLocation.setTextColor(resources.getColor(R.color.white))
            binding.currentLocationText.setTextColor(resources.getColor(R.color.white))
            if (BookingDateAndTimeScreen.FROM_PROVIDER) {
                binding.rowLayout.setBackgroundResource(R.drawable.purple_bg_sm)
            }
            fetchLocation(this)
        }

        val factory = ViewModelFactory(UserProfileRepository())
        val profileViewModel = ViewModelProvider(this, factory)[UserProfileViewModel::class.java]
        profileViewModel.userProfileInfo(this, UserUtils.getUserId(this)).observe(this, {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    val responseData = it.data!!
                    for (address in responseData.address) {
                        addressList.add(MonthsModel(address.locality + ", " + address.city + ", " + address.zipcode, address.id, false))
                    }
                    binding.addressRv.layoutManager = LinearLayoutManager(this@BookingAddressScreen)
                    binding.addressRv.adapter =
                        UserBookingAddressAdapter(addressList.distinctBy { data -> data.month } as java.util.ArrayList<MonthsModel>, this@BookingAddressScreen, "AA")
                    progressDialog.dismiss()
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.nextBtn, it.message!!)
                }
            }
        })

        binding.apply {

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
                UserUtils.started_at = currentDateAndTime().toString() + ":00:00"
                UserUtils.time_slot_from =
                    (calender.get(Calendar.HOUR_OF_DAY) + 1).toString() + ":00:00"

                when (UserUtils.getSelectedKeywordCategoryId(this@BookingAddressScreen)) {
                    "1" -> {
                        bookSingleMoveServiceProvider()
                    }
                    "2" -> {
                        bookBlueCollarServiceProvider()
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
                            bookMultiMoveServiceProvider()
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
                            val intent = Intent(
                                this@BookingAddressScreen,
                                BookingAttachmentsScreen::class.java
                            )
                            intent.putExtra(getString(R.string.service_provider), data)
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
        if (UserUtils.getFromInstantBooking(this)) {
            val requestBody = SingleMoveBookingReqModel(
                UserUtils.address_id.toInt(),
                "0",
                BookingAttachmentsScreen.encodedImages,
                SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date()),
                1,
                1,
                UserUtils.job_description,
                RetrofitBuilder.USER_KEY,
                UserUtils.scheduled_date,
                0,
                UserUtils.started_at,
                UserUtils.temp_address_id.toInt(),
                UserUtils.time_slot_from,
                UserUtils.time_slot_to,
                UserUtils.getUserId(this).toInt()
            )
            Log.e("SINGLE MOVE INSTANTLY:", Gson().toJson(requestBody))
            viewModel.singleMoveBooking(this, requestBody).observe(this, {
                when (it) {
                    is NetworkResponse.Loading -> {
                        progressDialog.show()
                    }
                    is NetworkResponse.Success -> {
                        progressDialog.dismiss()
                        showWaitingForSPConfirmationDialog()
                        if (UserUtils.getFromInstantBooking(this)) {
                            Log.e("SINGLE MOVE RESPONSE", it.data!!)
                            UserUtils.sendFCMtoAllServiceProviders(
                                this,
                                UserUtils.getBookingId(this),
                                "user"
                            )
                        } else {
                            Log.e("SINGLE MOVE SELECTED", it.data!!)
                            UserUtils.sendFCMtoSelectedServiceProvider(
                                this,
                                UserUtils.getBookingId(this),
                                "user"
                            )
                        }
                    }
                    is NetworkResponse.Failure -> {
                        progressDialog.dismiss()
                        snackBar(binding.nextBtn, "SINGLE MOVE" + it.message!!)
                    }
                }
            })
        } else {
            val requestBody = SingleMoveBookingReqModel(
                UserUtils.address_id.toInt(),
                data.per_hour,
                BookingAttachmentsScreen.encodedImages,
                SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date()),
                1,
                1,
                UserUtils.job_description,
                RetrofitBuilder.USER_KEY,
                UserUtils.scheduled_date,
                data.users_id.toInt(),
                UserUtils.started_at,
                UserUtils.temp_address_id.toInt(),
                UserUtils.time_slot_from,
                UserUtils.time_slot_to,
                UserUtils.getUserId(this).toInt()
            )
            Log.e("SINGLE MOVE SELECTION", Gson().toJson(requestBody))
            viewModel.singleMoveBooking(this, requestBody).observe(this, {
                when (it) {
                    is NetworkResponse.Loading -> {
                        progressDialog.show()
                    }
                    is NetworkResponse.Success -> {
                        progressDialog.dismiss()
                        showWaitingForSPConfirmationDialog()
                        if (UserUtils.getFromInstantBooking(this)) {
                            if (PermissionUtils.isNetworkConnected(this)) {
                                UserUtils.sendFCMtoAllServiceProviders(
                                    this,
                                    UserUtils.getBookingId(this),
                                    "user"
                                )
                            } else {
                                snackBar(binding.nextBtn, "No Internet Connection!")
                            }
                        } else {
                            UserUtils.sendFCMtoSelectedServiceProvider(
                                this,
                                UserUtils.getBookingId(this),
                                "user"
                            )
                        }
                    }
                    is NetworkResponse.Failure -> {
                        progressDialog.dismiss()
                        snackBar(binding.nextBtn, it.message!!)
                    }
                }
            })

        }

    }

    @SuppressLint("SetTextI18n")
    private fun showWaitingForSPConfirmationDialog() {
        val dialog = BottomSheetDialog(this)
        dialog.setCancelable(false)
        val dialogView = layoutInflater.inflate(R.layout.waiting_for_sp_confirmation_dialog, null)
        val progressBar = dialogView.findViewById<CircularProgressIndicator>(R.id.progressBar)
        val time = dialogView.findViewById<TextView>(R.id.time)
        val closeBtn = dialogView.findViewById<MaterialCardView>(R.id.closeBtn)
        closeBtn.setOnClickListener {
            UserUtils.sendFCMtoAllServiceProviders(this, UserUtils.getBookingId(this), "accepted")
            finish()
            startActivity(intent)
            dialog.dismiss()
        }
        var minutes = 2
        var seconds = 59
        val mainHandler = Handler(Looper.getMainLooper())
        var progressTime = 180
        mainHandler.post(object : Runnable {
            override fun run() {
                time.text = "$minutes:$seconds"
                progressTime -= 1
                progressBar.progress = progressTime

                seconds -= 1
                if (minutes == 0 && seconds == 0) {
                    UserUtils.sendFCMtoAllServiceProviders(this@BookingAddressScreen, "accepted", "accepted")
                    dialog.dismiss()
                    try {
                        weAreSorryDialog()
                    } catch (e: java.lang.Exception) {}
                    Checkout.preload(applicationContext)
                    startActivity(Intent(this@BookingAddressScreen, UserDashboardScreen::class.java))
                }
                if (seconds == 0) {
                    seconds = 59
                    minutes -= 1
                }
                if (UserUtils.getProviderAction(this@BookingAddressScreen)
                        .split("|")[0].isNotEmpty()
                ) {
                    UserUtils.sendFCMtoAllServiceProviders(this@BookingAddressScreen, "accepted", "accepted")
                    dialog.dismiss()
                    if (UserUtils.getProviderAction(this@BookingAddressScreen)
                            .split("|")[0].trim() == "accept"
                    ) {
                        serviceProviderAcceptDialog(this@BookingAddressScreen)
                    } else {
                        serviceProviderRejectDialog(this@BookingAddressScreen)
                    }
                }
                mainHandler.postDelayed(this, 1000)
            }
        })
        dialog.setContentView(dialogView)
        dialog.show()
    }

    private fun weAreSorryDialog() {
        val dialog = BottomSheetDialog(this)
        dialog.setCancelable(false)
        val dialogView = layoutInflater.inflate(R.layout.no_service_provider_found, null)
        val yesBtn = dialogView.findViewById<TextView>(R.id.yesBtn)
        val noBtn = dialogView.findViewById<TextView>(R.id.noBtn)
        val closeBtn = dialogView.findViewById<MaterialCardView>(R.id.closeBtn)
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
            showWaitingForSPConfirmationDialog()
        }
        dialog.setContentView(dialogView)
        dialog.show()
    }

    override fun selectedMonth(position: Int, dateTime: String, listType: String) {
        val tempAddress = arrayListOf<MonthsModel>()
        if (UserUtils.getFromInstantBooking(this)) {
            if (UserUtils.getSelectedKeywordCategoryId(this) == "3") {
                addressList.onEachIndexed { index, month ->
                    if (index == position || month.isSelected) {
                        tempAddress.add(MonthsModel(month.month, month.day, true))
                    } else {
                        tempAddress.add(MonthsModel(month.month, month.day, false))
                    }
                }
                addressList = tempAddress
            } else {
                addressList.onEachIndexed { index, month ->
                    if (index == position) {
                        tempAddress.add(MonthsModel(month.month, month.day, true))
                    } else {
                        tempAddress.add(MonthsModel(month.month, month.day, false))
                    }
                }
                addressList = tempAddress
            }
        } else {
            if (data.category_id == "3") {
                addressList.onEachIndexed { index, month ->
                    if (index == position || month.isSelected) {
                        tempAddress.add(MonthsModel(month.month, month.day, true))
                    } else {
                        tempAddress.add(MonthsModel(month.month, month.day, false))
                    }
                }
                addressList = tempAddress
            } else {
                addressList.onEachIndexed { index, month ->
                    if (index == position) {
                        tempAddress.add(MonthsModel(month.month, month.day, true))
                    } else {
                        tempAddress.add(MonthsModel(month.month, month.day, false))
                    }
                }
                addressList = tempAddress
            }
        }
        binding.addressRv.adapter =
            UserBookingAddressAdapter(addressList.distinctBy { data -> data.month } as java.util.ArrayList<MonthsModel>, this@BookingAddressScreen, "AA")
        validateFields()
        toast(this, "Validating")
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
        Glide.with(this).load(RetrofitBuilder.BASE_URL + data.profile_pic).into(binding.profilePic)
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
                    BookingDateAndTimeScreen.FROM_PROVIDER = false
                    val intent = Intent(this, BookingDateAndTimeScreen::class.java)
                    intent.putExtra(getString(R.string.service_provider), data)
                    startActivity(intent)
                }
            }
        }
    }

//    private fun makePayment() {
//        Checkout.preload(this)
//        val checkout = Checkout()
//        checkout.setKeyID(getString(com.satrango.R.string.razorpay_api_key))
//        val amount = data.per_hour.toInt() * 100
//        Log.e("AMOUNT:", amount.toString())
//        try {
//            val orderRequest = JSONObject()
//            orderRequest.put("currency", "INR")
//            orderRequest.put(
//                "amount",
//                        amount
//            ) // 500rs * 100 = 50000 paisa passed
//            orderRequest.put("receipt", "order_rcptid_${System.currentTimeMillis()}")
//            orderRequest.put("image", "https://dev.satrango.com/public/assets/img/logo-black.png")
//            orderRequest.put("theme.color", R.color.blue)
//            checkout.open(this, orderRequest)
//        } catch (e: Exception) {
//            toast(this, e.message!!)
//        }
//    }

//    override fun onPaymentSuccess(paymentResponse: String?) {
//        updateStatusInServer(paymentResponse, "Success")
//
//    }
//
//    override fun onPaymentError(p0: Int, paymentError: String?) {
//        updateStatusInServer("", "Failure")
//        snackBar(binding.nextBtn, "Payment Failed. Please Try Again!")
//    }

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
            PaymentScreen.amount = data.per_hour.toDouble()
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

//    private fun updateStatusInServer(paymentResponse: String?, status: String) {
//        val requestBody = PaymentConfirmReqModel(
//            data.per_hour,
//            UserUtils.getBookingId(this),
//            UserUtils.scheduled_date,
//            RetrofitBuilder.USER_KEY,
//            status,
//            paymentResponse!!,
//            data.users_id.toInt(),
//            UserUtils.time_slot_from,
//            UserUtils.getUserId(this).toInt()
//        )
//        viewModel.confirmPayment(this, requestBody).observe(this, {
//            when (it) {
//                is NetworkResponse.Loading -> {
//                    progressDialog.show()
//                }
//                is NetworkResponse.Success -> {
//                    progressDialog.dismiss()
//                    finish()
//                    startActivity(Intent(this, UserDashboardScreen::class.java))
//                }
//                is NetworkResponse.Failure -> {
//                    progressDialog.dismiss()
//                    snackBar(binding.nextBtn, it.message!!)
//                }
//            }
//        })
//    }

    @SuppressLint("SimpleDateFormat")
    private fun bookMultiMoveServiceProvider() {
        val requestBody = MultiMoveReqModel(
            UserUtils.finalAddressList,
            "0",
            BookingAttachmentsScreen.encodedImages,
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date()),
            1,
            1,
            RetrofitBuilder.USER_KEY,
            UserUtils.scheduled_date,
            0,
            UserUtils.started_at,
            UserUtils.time_slot_from,
            UserUtils.time_slot_to,
            UserUtils.getUserId(this).toInt()
        )
        Log.e("MULTI MOVE:", Gson().toJson(requestBody))
        viewModel.multiMoveBooking(this, requestBody).observe(this, {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    showWaitingForSPConfirmationDialog()
                    if (UserUtils.getFromInstantBooking(this)) {
                        if (PermissionUtils.isNetworkConnected(this)) {
                            UserUtils.sendFCMtoAllServiceProviders(
                                this,
                                UserUtils.getBookingId(this),
                                "user"
                            )
                        } else {
                            snackBar(binding.nextBtn, "No Internet Connection!")
                        }
                    } else {
                        UserUtils.sendFCMtoSelectedServiceProvider(
                            this,
                            UserUtils.getBookingId(this),
                            "user"
                        )
                    }
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.nextBtn, "MULTI MOVE:" + it.message!!)
                }
            }
        })
    }

    @SuppressLint("SimpleDateFormat")
    private fun bookBlueCollarServiceProvider() {
        val requestBody = BlueCollarBookingReqModel(
            "0",
            BookingAttachmentsScreen.encodedImages,
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date()),
            1,
            1,
            UserUtils.job_description,
            RetrofitBuilder.USER_KEY,
            UserUtils.scheduled_date,
            0,
            UserUtils.started_at,
            UserUtils.time_slot_from,
            UserUtils.time_slot_to,
            UserUtils.getUserId(this).toInt()
        )
        Log.e("BLUE COLLAR MOVE", Gson().toJson(requestBody))
        viewModel.blueCollarBooking(this, requestBody).observe(this, {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    showWaitingForSPConfirmationDialog()
                    if (UserUtils.getFromInstantBooking(this)) {
                        if (PermissionUtils.isNetworkConnected(this)) {
                            UserUtils.sendFCMtoAllServiceProviders(
                                this,
                                UserUtils.getBookingId(this),
                                "user"
                            )
                        } else {
                            snackBar(binding.nextBtn, "No Internet Connection!")
                        }
                    } else {
                        UserUtils.sendFCMtoSelectedServiceProvider(
                            this,
                            UserUtils.getBookingId(this),
                            "user"
                        )
                    }
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.nextBtn, "BLUE COLLAR" + it.message!!)
                }
            }
        })
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
            Toast.makeText(context, "Please Check you Internet Connection!", Toast.LENGTH_LONG)
                .show()
        }
    }

}