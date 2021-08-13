package com.satrango.ui.user.bookings.booking_address

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
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
import com.satrango.ui.user.bookings.provider_response.PaymentConfirmReqModel
import com.satrango.ui.user.bookings.booking_address.models.BlueCollarBookingReqModel
import com.satrango.ui.user.bookings.booking_address.models.SingleMoveBookingReqModel
import com.satrango.ui.user.bookings.booking_attachments.BookingAttachmentsScreen
import com.satrango.ui.user.bookings.booking_attachments.models.MultiMoveReqModel
import com.satrango.ui.user.bookings.booking_date_time.BookingDateAndTimeScreen
import com.satrango.ui.user.bookings.booking_date_time.MonthsAdapter
import com.satrango.ui.user.bookings.booking_date_time.MonthsInterface
import com.satrango.ui.user.bookings.booking_date_time.MonthsModel
import com.satrango.ui.user.bookings.change_address.AddBookingAddressScreen
import com.satrango.ui.user.user_dashboard.UserDashboardScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.my_profile.UserProfileRepository
import com.satrango.ui.user.user_dashboard.drawer_menu.my_profile.UserProfileViewModel
import com.satrango.ui.user.user_dashboard.search_service_providers.models.Data
import com.satrango.utils.PermissionUtils
import com.satrango.utils.UserUtils
import com.satrango.utils.snackBar
import com.satrango.utils.toast
import de.hdodenhof.circleimageview.CircleImageView
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class BookingAddressScreen : AppCompatActivity(), MonthsInterface, PaymentResultListener {

    private lateinit var viewModel: BookingViewModel
    private lateinit var addressList: ArrayList<MonthsModel>
    private lateinit var binding: ActivityBookingAddressScreenBinding
    private lateinit var progressDialog: ProgressDialog
    private lateinit var data: Data

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookingAddressScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolBar = binding.root.findViewById<View>(R.id.toolBar)
        toolBar.findViewById<ImageView>(R.id.toolBarBackBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarBackTVBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text =
            resources.getString(R.string.booking)
        val profilePic = toolBar.findViewById<CircleImageView>(R.id.toolBarImage)
        Glide.with(profilePic).load(UserUtils.getUserProfilePic(this)).into(profilePic)

        val bookingFactory = ViewModelFactory(BookingRepository())
        viewModel = ViewModelProvider(this, bookingFactory)[BookingViewModel::class.java]

        initializeProgressDialog()
        if (!BookingAttachmentsScreen.FROM_BOOK_INSTANTLY) {
            data = intent.getSerializableExtra(getString(R.string.service_provider)) as Data
            updateUI(data)
        } else {
            binding.spCard.visibility = View.GONE
        }


        addressList = arrayListOf()
        addressList.add(
            MonthsModel(
                UserUtils.getAddress(this@BookingAddressScreen) + ", " + UserUtils.getCity(
                    this@BookingAddressScreen
                ) + ", " + UserUtils.getPostalCode(this@BookingAddressScreen),
                UserUtils.getTempAddressId(this@BookingAddressScreen),
                false
            )
        )

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
                        addressList.add(
                            MonthsModel(
                                address.locality + ", " + address.city + ", " + address.zipcode,
                                address.id,
                                false
                            )
                        )
                    }
                    binding.addressRv.layoutManager = LinearLayoutManager(
                        this@BookingAddressScreen,
                        LinearLayoutManager.HORIZONTAL,
                        false
                    )
                    binding.addressRv.adapter =
                        MonthsAdapter(addressList, this@BookingAddressScreen, "AA")
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
                val intent = Intent(this@BookingAddressScreen, AddBookingAddressScreen::class.java)
                intent.putExtra(getString(R.string.service_provider), data)
                startActivity(intent)
            }

            nextBtn.setOnClickListener {
                UserUtils.temp_address_id = "0"
                UserUtils.address_id = "0"
                for (address in addressList) {
                    if (address.isSelected) {
                        if (UserUtils.getTempAddressId(this@BookingAddressScreen) == address.day) {
                            UserUtils.temp_address_id = UserUtils.getTempAddressId(this@BookingAddressScreen)
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
                    if (BookingAttachmentsScreen.FROM_BOOK_INSTANTLY) {
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
        }

    }

    @SuppressLint("SimpleDateFormat")
    private fun bookSingleMoveServiceProvider() {
        if (BookingAttachmentsScreen.FROM_BOOK_INSTANTLY) {
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
            val factory = ViewModelFactory(BookingRepository())
            val viewModel = ViewModelProvider(this, factory)[BookingViewModel::class.java]
            viewModel.singleMoveBooking(this, requestBody).observe(this, {
                when (it) {
                    is NetworkResponse.Loading -> {
                        progressDialog.show()
                    }
                    is NetworkResponse.Success -> {
                        progressDialog.dismiss()
                        val jsonResponse = JSONObject(it.data!!)
                        Log.e("SINGLE MOVE", jsonResponse.toString())
                        if (jsonResponse.getInt("status") == 200) {
                            if (BookingAttachmentsScreen.FROM_BOOK_INSTANTLY) {
                                if (PermissionUtils.isNetworkConnected(this)) {
                                    UserUtils.sendFCMtoAllServiceProviders(this, UserUtils.getBookingId(this), "user")
                                } else {
                                    snackBar(binding.nextBtn, "No Internet Connection!")
                                }
                            } else {
                                UserUtils.sendFCMtoSelectedServiceProvider(this, UserUtils.getBookingId(this), "user")
                            }
                            showWaitingForSPConfirmationDialog()
                        }
                    }
                    is NetworkResponse.Failure -> {
                        progressDialog.dismiss()
                        snackBar(binding.nextBtn, "SINGLE MOVE" + it.message!!)
                    }
                }
            })
        }  else {
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
            Log.e("SINGLE MOVE", Gson().toJson(requestBody))
            val factory = ViewModelFactory(BookingRepository())
            val viewModel = ViewModelProvider(this, factory)[BookingViewModel::class.java]
            viewModel.singleMoveBooking(this, requestBody).observe(this, {
                when (it) {
                    is NetworkResponse.Loading -> {
                        progressDialog.show()
                    }
                    is NetworkResponse.Success -> {
                        progressDialog.dismiss()
                        val jsonResponse = JSONObject(it.data!!)
                        Log.e("SINGLE MOVE", jsonResponse.toString())
                        if (jsonResponse.getInt("status") == 200) {
                            if (BookingAttachmentsScreen.FROM_BOOK_INSTANTLY) {
                                if (PermissionUtils.isNetworkConnected(this)) {
                                    UserUtils.sendFCMtoAllServiceProviders(this, UserUtils.getBookingId(this), "user")
                                } else {
                                    snackBar(binding.nextBtn, "No Internet Connection!")
                                }
                            } else {
                                UserUtils.sendFCMtoSelectedServiceProvider(this, UserUtils.getBookingId(this), "user")
                            }
                            showWaitingForSPConfirmationDialog()
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
                    dialog.dismiss()
                    weAreSorryDialog()
                    Checkout.preload(applicationContext)
                    makePayment()
                }
                if (seconds == 0) {
                    seconds = 59
                    minutes -= 1
                }
                if (UserUtils.getProviderAction(this@BookingAddressScreen).split("|")[0].isNotEmpty()) {
                    dialog.dismiss()
                    if (UserUtils.getProviderAction(this@BookingAddressScreen).split("|")[0].trim() == "accept") {
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

    override fun selectedMonth(position: Int, listType: String) {
        val tempAddress = arrayListOf<MonthsModel>()
        if (BookingAttachmentsScreen.FROM_BOOK_INSTANTLY) {
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
        binding.addressRv.adapter = MonthsAdapter(addressList, this@BookingAddressScreen, "AA")
    }

    private fun initializeProgressDialog() {
        progressDialog = ProgressDialog(this)
        progressDialog.setCancelable(false)
        progressDialog.setMessage("Loading...")
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI(data: Data) {
        binding.userName.text = "${data.fname} ${data.lname}"
        binding.occupation.text = data.profession
        binding.costPerHour.text = data.per_hour
    }

    override fun onBackPressed() {
        if (BookingAttachmentsScreen.FROM_BOOK_INSTANTLY) {
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
                    val intent = Intent(this, BookingDateAndTimeScreen::class.java)
                    intent.putExtra(getString(R.string.service_provider), data)
                    startActivity(intent)
                }
            }
        }
    }

    private fun makePayment() {
        val checkout = Checkout()
        checkout.setKeyID(getString(com.satrango.R.string.razorpay_api_key))
        try {
            val orderRequest = JSONObject()
            orderRequest.put("currency", "INR")
            orderRequest.put(
                "amount",
                data.per_hour.toInt() * 100
            ) // 500rs * 100 = 50000 paisa passed
            orderRequest.put("receipt", "order_rcptid_${System.currentTimeMillis()}")
            orderRequest.put("image", "https://dev.satrango.com/public/assets/img/logo-black.png")
            orderRequest.put("theme.color", R.color.blue)
            checkout.open(this, orderRequest)
        } catch (e: Exception) {
            toast(this, e.message!!)
        }
    }

    override fun onPaymentSuccess(paymentResponse: String?) {
        updateStatusInServer(paymentResponse, "Success")

    }

    override fun onPaymentError(p0: Int, paymentError: String?) {
        updateStatusInServer("", "Failure")
        snackBar(binding.nextBtn, "Payment Failed. Please Try Again!")
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
            Checkout.preload(applicationContext)
            makePayment()
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

    private fun updateStatusInServer(paymentResponse: String?, status: String) {
        val requestBody = PaymentConfirmReqModel(
            data.per_hour,
            UserUtils.getBookingId(this),
            UserUtils.scheduled_date,
            RetrofitBuilder.USER_KEY,
            status,
            paymentResponse!!,
            data.users_id.toInt(),
            UserUtils.time_slot_from,
            UserUtils.getUserId(this).toInt()
        )
        viewModel.confirmPayment(this, requestBody).observe(this, {
            when(it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    finish()
                    startActivity(Intent(this, UserDashboardScreen::class.java))
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.nextBtn, it.message!!)
                }
            }
        })
    }

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
                    if (BookingAttachmentsScreen.FROM_BOOK_INSTANTLY) {
                        if (PermissionUtils.isNetworkConnected(this)) {
                            UserUtils.sendFCMtoAllServiceProviders(this, UserUtils.getBookingId(this), "user")
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
                    if (BookingAttachmentsScreen.FROM_BOOK_INSTANTLY) {
                        if (PermissionUtils.isNetworkConnected(this)) {
                            UserUtils.sendFCMtoAllServiceProviders(this, UserUtils.getBookingId(this),"user")
                        } else {
                            snackBar(binding.nextBtn, "No Internet Connection!")
                        }
                    } else {
                        UserUtils.sendFCMtoSelectedServiceProvider(this, UserUtils.getBookingId(this),  "user")
                    }
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.nextBtn, "BLUE COLLAR" + it.message!!)
                }
            }
        })
    }


}