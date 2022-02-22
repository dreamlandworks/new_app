package com.satrango.ui.user.bookings.view_booking_details

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.marginRight
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.basusingh.beautifulprogressdialog.BeautifulProgressDialog
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.satrango.R
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivityViewUserBookingDetailsScreenBinding
import com.satrango.remote.NetworkResponse
import com.satrango.remote.RetrofitBuilder
import com.satrango.remote.fcm.FCMService
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.ProviderBookingRepository
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.ProviderBookingViewModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.ProviderMyBookingsScreen
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.ProviderBookingDetailsScreen
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.invoice.ProviderInVoiceScreen
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.models.ChangeExtraDemandStatusReqModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.models.ExpenditureIncurredReqModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.models.ExtraDemandReqModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.release_goals.ProviderReleaseGoalsScreen
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.review.ProviderRatingReviewScreen
import com.satrango.ui.user.bookings.booking_address.BookingRepository
import com.satrango.ui.user.bookings.booking_address.BookingViewModel
import com.satrango.ui.user.bookings.booking_date_time.BookingDateAndTimeScreen
import com.satrango.ui.user.bookings.cancel_booking.UserBookingCancelScreen
import com.satrango.ui.user.bookings.view_booking_details.installments_request.UserInstallmentsRequestScreen
import com.satrango.ui.user.bookings.view_booking_details.models.BookingDetailsReqModel
import com.satrango.ui.user.bookings.view_booking_details.models.BookingDetailsResModel
import com.satrango.ui.user.user_dashboard.UserDashboardScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.my_bookings.MyBookingsRepository
import com.satrango.ui.user.user_dashboard.drawer_menu.my_bookings.MyBookingsViewModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_bookings.UserMyBookingsScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.view_bids.ViewBidsScreen
import com.satrango.utils.UserUtils
import com.satrango.utils.loadProfileImage
import com.satrango.utils.snackBar
import com.satrango.utils.toast
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.util.*
import android.app.Activity
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ViewUserBookingDetailsScreen : AppCompatActivity() {

    private lateinit var toolBar: View
    private var userId = ""
    private var categoryId = ""
    private var bookingId = ""
    private lateinit var response: BookingDetailsResModel
    private lateinit var progressDialog: BeautifulProgressDialog
    private lateinit var binding: ActivityViewUserBookingDetailsScreenBinding

    companion object {
        var RESCHEDULE = false
        var FROM_MY_BOOKINGS_SCREEN = false
        var FROM_PROVIDER = false
        var FROM_PENDING = false
        var FCM_TOKEN = ""
        var FROM_COMPLETED = false
    }

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewUserBookingDetailsScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeToolBar()
        initializeProgressDialog()
        initializeProgressDialog()

        registerReceiver(otpReceiver, IntentFilter(FCMService.OTP_INTENT_FILTER));
        registerReceiver(otpResponseReceiver, IntentFilter(FCMService.OTP_RESPONSE_INTENT_FILTER));

        val factory = ViewModelFactory(BookingRepository())
        val viewModel = ViewModelProvider(this, factory)[BookingViewModel::class.java]

        if (FROM_MY_BOOKINGS_SCREEN) {
            toolBar.setBackgroundColor(resources.getColor(R.color.blue))
            if (FROM_PROVIDER) {
                updateSPUI()
                if (FROM_PENDING) {
                    binding.userLayout.visibility = View.VISIBLE
                    binding.spLayout.visibility = View.GONE
                    binding.completedBtn.text = "Start"
                    binding.completedBtn.setOnClickListener {
                        requestOTP("SP")
                    }
                    binding.startBtn.setOnClickListener {
                        requestOTP("SP")
                    }
                } else {
                    binding.spLayout.visibility = View.VISIBLE
                    binding.userLayout.visibility = View.GONE
                    binding.completedBtn.text = "Mark Complete"
                    binding.completedBtn.setOnClickListener {
                        if (response.booking_details.extra_demand_total_amount != "0") {
                            ProviderInVoiceScreen.isExtraDemandRaised = "1"
                            if (response.booking_details.extra_demand_status == "2") {
                                finalExpenditureDialog()
                            } else {
                                divertToInvoiceScreen()
                            }
                        } else {
                            ProviderInVoiceScreen.isExtraDemandRaised = "0"
                            divertToInvoiceScreen()
                        }
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        val window: Window = window
                        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                        window.statusBarColor = resources.getColor(R.color.purple_700)
                    }
                    binding.inProgressViewStatusBtn.setOnClickListener { onBackPressed() }
                }
            } else {
                if (FROM_PENDING) {
                    binding.userLayout.visibility = View.VISIBLE
                    binding.spLayout.visibility = View.GONE
                    binding.startBtn.visibility = View.GONE
                    registerReceiver(myReceiver, IntentFilter(FCMService.EXTRA_DEMAND_ACCEPT_REJECT))
                } else {
                    binding.spLayout.visibility = View.VISIBLE
                    binding.userLayout.visibility = View.GONE
                    binding.viewFilesBtn.setBackgroundResource(R.drawable.blue_out_line)
                    binding.viewFilesBtn.setTextColor(resources.getColor(R.color.blue))
                    binding.inProgressViewStatusBtn.setBackgroundResource(R.drawable.blue_out_line)
                    binding.inProgressViewStatusBtn.setTextColor(resources.getColor(R.color.blue))
                    binding.completedBtn.setBackgroundResource(R.drawable.blue_out_line)
                    binding.completedBtn.setTextColor(resources.getColor(R.color.blue))
                    binding.completedBtn.text = "Mark Complete"
                    binding.requestInstallmentBtn.visibility = View.GONE
                    binding.raiseExtraDemandBtn.visibility = View.GONE
                    binding.completedBtn.setOnClickListener {
                        if (FROM_PROVIDER) {
                            ProviderInVoiceScreen.FROM_PROVIDER = FROM_PROVIDER
                            val intent = Intent(this, ProviderInVoiceScreen::class.java)
                            intent.putExtra(binding.root.context.getString(R.string.booking_id), bookingId)
                            intent.putExtra(binding.root.context.getString(R.string.category_id), categoryId)
                            intent.putExtra(binding.root.context.getString(R.string.user_id), userId)
                            startActivity(intent)
                        } else {
                            ProviderInVoiceScreen.FROM_PROVIDER = FROM_PROVIDER
                            val intent = Intent(this, ProviderInVoiceScreen::class.java)
                            intent.putExtra(binding.root.context.getString(R.string.booking_id), bookingId)
                            intent.putExtra(binding.root.context.getString(R.string.category_id), categoryId)
                            intent.putExtra(binding.root.context.getString(R.string.user_id), userId)
                            startActivity(intent)
                        }
                    }
                    binding.inProgressViewStatusBtn.setOnClickListener { onBackPressed() }
                    if (categoryId == "2") {
                        binding.viewFilesBtn.visibility = View.VISIBLE
                    } else {
                        binding.viewFilesBtn.visibility = View.GONE
                    }
                }
            }
        } else {
            binding.spLayout.visibility = View.VISIBLE
            binding.firstLayout.visibility = View.VISIBLE
            binding.secondLayout.visibility = View.GONE
            binding.viewFilesBtn.visibility = View.VISIBLE
            binding.completedBtn.visibility = View.GONE
            if (!FROM_PROVIDER) {
                binding.viewFilesBtn.setTextColor(resources.getColor(R.color.blue))
                binding.inProgressViewStatusBtn.setTextColor(resources.getColor(R.color.blue))
                binding.viewFilesBtn.setBackgroundResource(R.drawable.btn_bg_white_blue_border)
                binding.inProgressViewStatusBtn.setBackgroundResource(R.drawable.btn_bg_white_blue_border)
            } else {
                updateSPUI()
            }
            binding.viewFilesBtn.setOnClickListener {

            }
            binding.inProgressViewStatusBtn.setOnClickListener {
                if (FROM_PROVIDER) {
                    ProviderBookingDetailsScreen.bookingId = bookingId
                    ProviderBookingDetailsScreen.categoryId = categoryId
                    ProviderBookingDetailsScreen.userId = userId
                    startActivity(Intent(this, ProviderBookingDetailsScreen::class.java))
                } else {
                    UserMyBookingDetailsScreen.bookingId = bookingId
                    UserMyBookingDetailsScreen.categoryId = categoryId
                    UserMyBookingDetailsScreen.userId = userId
                    startActivity(Intent(this, UserMyBookingDetailsScreen::class.java))
                }
            }
            binding.userLayout.visibility = View.GONE
        }

        bookingId = intent.getStringExtra(getString(R.string.booking_id))!!
        categoryId = intent.getStringExtra(getString(R.string.category_id))!!
        userId = intent.getStringExtra(getString(R.string.user_id))!!

        val requestBody = BookingDetailsReqModel(
            bookingId.toInt(),
            categoryId.toInt(),
            RetrofitBuilder.USER_KEY,
            userId.toInt()
        )
        Log.e("PROVIDER RESPONSE", Gson().toJson(requestBody))
//        toast(this, Gson().toJson(requestBody))
        viewModel.viewBookingDetails(this, requestBody).observe(this) {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    response = it.data!!
                    updateUI(response)
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.inProgressViewStatusBtn, it.message!!)
                }
            }
        }

    }

    private fun divertToInvoiceScreen() {
        ProviderInVoiceScreen.FROM_PROVIDER = true
        val intent = Intent(this, ProviderInVoiceScreen::class.java)
        intent.putExtra(binding.root.context.getString(R.string.booking_id), ProviderBookingDetailsScreen.bookingId)
        intent.putExtra(binding.root.context.getString(R.string.category_id), ProviderBookingDetailsScreen.categoryId)
        intent.putExtra(binding.root.context.getString(R.string.user_id), ProviderBookingDetailsScreen.userId)
        startActivity(intent)
    }

    private fun updateSPUI() {
        binding.card.setCardBackgroundColor(resources.getColor(R.color.purple_500))
        binding.reScheduleBtn.setBackgroundResource(R.drawable.purple_out_line)
        binding.reScheduleBtn.setTextColor(resources.getColor(R.color.purple_500))
        binding.startBtn.setBackgroundResource(R.drawable.provider_btn_bg)
        binding.cancelBookingBtn.setBackgroundResource(R.drawable.purple_out_line)
        binding.cancelBookingBtn.setTextColor(resources.getColor(R.color.purple_500))
        binding.layoutOne.setBackgroundResource(R.drawable.purple_out_line)
        binding.layoutTwo.setBackgroundResource(R.drawable.purple_out_line)
        binding.layoutThree.setBackgroundResource(R.drawable.purple_out_line)
        binding.layoutFour.setBackgroundResource(R.drawable.purple_out_line)
        toolBar.setBackgroundColor(resources.getColor(R.color.purple_500))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = resources.getColor(R.color.purple_700)
        }
    }

    private fun initializeToolBar() {
        toolBar = binding.root.findViewById<View>(R.id.toolBar)
        toolBar.findViewById<ImageView>(R.id.toolBarBackBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarBackTVBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text =
            resources.getString(R.string.view_details)
        val profilePic = toolBar.findViewById<CircleImageView>(R.id.toolBarImage)
        loadProfileImage(profilePic)
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

    private fun finalExpenditureDialog() {
        val dialog = BottomSheetDialog(this)
        val dialogView =
            layoutInflater.inflate(R.layout.provider_final_extra_expenditure_dialog, null)
        val closeBtn = dialogView.findViewById<MaterialCardView>(R.id.closeBtn)
        val raisedExtraDemand = dialogView.findViewById<TextView>(R.id.raiseExtraDemand)
        val finalExpenditure = dialogView.findViewById<EditText>(R.id.finalExpenditure)
        val submitBtn = dialogView.findViewById<TextView>(R.id.submitBtn)
        raisedExtraDemand.text = response.booking_details.material_advance

        closeBtn.setOnClickListener { dialog.dismiss() }
        submitBtn.setOnClickListener {
            if (finalExpenditure.text.toString().isEmpty()) {
                toast(this, "Enter Expenditure Incurred")
            } else {
                val factory = ViewModelFactory(ProviderBookingRepository())
                val viewModel =
                    ViewModelProvider(this, factory)[ProviderBookingViewModel::class.java]
                val requestBody = ExpenditureIncurredReqModel(
                    bookingId.toInt(),
                    finalExpenditure.text.toString().toInt(),
                    RetrofitBuilder.PROVIDER_KEY
                )
                viewModel.expenditureIncurred(this, requestBody).observe(this) {
                    when (it) {
                        is NetworkResponse.Loading -> {
                            progressDialog.show()
                        }
                        is NetworkResponse.Success -> {
                            progressDialog.dismiss()
                            dialog.dismiss()
                            ProviderInVoiceScreen.FROM_PROVIDER = true
                            val intent = Intent(this, ProviderInVoiceScreen::class.java)
                            intent.putExtra(binding.root.context.getString(R.string.booking_id), ProviderBookingDetailsScreen.bookingId)
                            intent.putExtra(binding.root.context.getString(R.string.category_id), ProviderBookingDetailsScreen.categoryId)
                            intent.putExtra(binding.root.context.getString(R.string.user_id), ProviderBookingDetailsScreen.userId)
                            startActivity(intent)
                        }
                        is NetworkResponse.Failure -> {
                            progressDialog.dismiss()
                            toast(this, it.message!!)
                        }
                    }
                }
            }
        }
        dialog.setContentView(dialogView)
        dialog.setCancelable(false)
        dialog.show()
    }

    private fun requestOTP(userType: String) {
        val factory = ViewModelFactory(MyBookingsRepository())
        val viewModel = ViewModelProvider(this, factory)[MyBookingsViewModel::class.java]
        viewModel.otpRequest(this, bookingId.toInt(), userType)
            .observe(this) {
                when (it) {
                    is NetworkResponse.Loading -> {
                        progressDialog.show()
                    }
                    is NetworkResponse.Success -> {
                        progressDialog.dismiss()
                        val requestedOTP = it.data!!
                        toast(this, requestedOTP.toString())
                        UserUtils.sendOTPFCM(this, FCM_TOKEN, bookingId, requestedOTP.toString())
                        otpDialog(requestedOTP, bookingId)
                    }
                    is NetworkResponse.Failure -> {
                        progressDialog.dismiss()
                        snackBar(binding.inProgressViewStatusBtn, it.message!!)
                    }
                }
            }
    }

    @SuppressLint("SetTextI18n")
    fun otpDialog(requestedOTP: Int, bookingId: String) {

        val dialog = BottomSheetDialog(this)
        val dialogView = layoutInflater.inflate(R.layout.booking_status_change_otp_dialog, null)
        val closeBtn = dialogView.findViewById<MaterialCardView>(R.id.closeBtn)
        val firstNo = dialogView.findViewById<EditText>(R.id.firstNo)
        val secondNo = dialogView.findViewById<EditText>(R.id.secondNo)
        val thirdNo = dialogView.findViewById<EditText>(R.id.thirdNo)
        val fourthNo = dialogView.findViewById<EditText>(R.id.fourthNo)
        val title = dialogView.findViewById<TextView>(R.id.title)
        val submitBtn = dialogView.findViewById<TextView>(R.id.submitBtn)

        closeBtn.setOnClickListener { dialog.dismiss() }

        if (FROM_PROVIDER) {
            title.text = "OTP to Start Job"
            title.setTextColor(resources.getColor(R.color.purple_500))
            firstNo.setBackgroundResource(R.drawable.purpleborderbutton)
            secondNo.setBackgroundResource(R.drawable.purpleborderbutton)
            thirdNo.setBackgroundResource(R.drawable.purpleborderbutton)
            fourthNo.setBackgroundResource(R.drawable.purpleborderbutton)
            submitBtn.setBackgroundResource(R.drawable.provider_btn_bg)
        }

        firstNo.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (s!!.length == 1) {
                    firstNo.clearFocus()
                    secondNo.requestFocus()
                }
            }

        })
        secondNo.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (s!!.length == 1) {
                    secondNo.clearFocus()
                    thirdNo.requestFocus()
                }
            }

        })
        thirdNo.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (s!!.length == 1) {
                    thirdNo.clearFocus()
                    fourthNo.requestFocus()
                }
            }

        })
        fourthNo.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (s!!.length == 1) {
                    fourthNo.clearFocus()
                }
            }

        })

        submitBtn.setOnClickListener {

            if (firstNo.text.toString().trim().isEmpty()) {
                snackBar(binding.startBtn, "Invalid OTP")
            } else if (secondNo.text.toString().trim().isEmpty()) {
                snackBar(binding.startBtn, "Invalid OTP")
            } else if (thirdNo.text.toString().trim().isEmpty()) {
                snackBar(binding.startBtn, "Invalid OTP")
            } else if (fourthNo.text.toString().trim().isEmpty()) {
                snackBar(binding.startBtn, "Invalid OTP")
            } else {
                val otp = firstNo.text.toString().trim() + secondNo.text.toString()
                    .trim() + thirdNo.text.toString().trim() + fourthNo.text.toString().trim()
                if (requestedOTP == otp.toInt()) {
                    if (FROM_PROVIDER) {
                        binding.startBtn.visibility = View.GONE
                    } else {
                        UserUtils.spid = "0"
                    }
                    val factory = ViewModelFactory(MyBookingsRepository())
                    val viewModel = ViewModelProvider(this, factory)[MyBookingsViewModel::class.java]
                    viewModel.validateOTP(this, bookingId.toInt(), UserUtils.spid.toInt())
                        .observe(this) {
                            when (it) {
                                is NetworkResponse.Loading -> {
                                    progressDialog.show()
                                }
                                is NetworkResponse.Success -> {
                                    progressDialog.dismiss()
                                    dialog.dismiss()
                                    if (!FROM_PROVIDER) {
                                        val intent = Intent(this, ProviderInVoiceScreen::class.java)
                                        intent.putExtra(
                                            binding.root.context.getString(R.string.booking_id),
                                            bookingId
                                        )
                                        intent.putExtra(
                                            binding.root.context.getString(R.string.category_id),
                                            categoryId
                                        )
                                        intent.putExtra(
                                            binding.root.context.getString(R.string.user_id),
                                            userId
                                        )
                                        startActivity(intent)
                                    } else {
                                        UserUtils.sendOTPResponseFCM(
                                            this,
                                            FCM_TOKEN,
                                            "$bookingId|$categoryId|$userId|sp"
                                        )
                                        startActivity(
                                            Intent(
                                                this,
                                                ProviderMyBookingsScreen::class.java
                                            )
                                        )
                                    }
                                    snackBar(
                                        binding.inProgressViewStatusBtn,
                                        "OTP Verification Success"
                                    )
                                }
                                is NetworkResponse.Failure -> {
                                    progressDialog.dismiss()
                                    dialog.dismiss()
                                    snackBar(binding.inProgressViewStatusBtn, it.message!!)
                                }
                            }
                        }
                } else {
                    toast(this, "Invalid OTP")
                }
            }
        }
        dialog.setCancelable(false)
        dialog.setContentView(dialogView)
        dialog.show()
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI(response: BookingDetailsResModel) {
        binding.date.text = response.booking_details.scheduled_date
        binding.amount.text = "Rs ${response.booking_details.amount}"
        binding.time.text = response.booking_details.from
        FCM_TOKEN = response.booking_details.fcm_token
        if (FROM_PROVIDER) {
            binding.userName.text = "${response.booking_details.fname} ${response.booking_details.lname}"
            binding.occupation.text = "User"
            Glide.with(binding.profilePic)
                .load(RetrofitBuilder.BASE_URL + response.booking_details.user_profile_pic)
                .error(R.drawable.images)
                .into(binding.profilePic)
//            if (response.booking_details.otp_raised_by != response.booking_details.sp_id && response.booking_details.otp_raised_by != "0") {
//                if (FROM_COMPLETED && !FROM_PENDING) {
                if (!FROM_PENDING) {
                    binding.otpText.text = resources.getString(R.string.time_lapsed)
                    binding.otp.text = response.booking_details.time_lapsed
                } else {
                    binding.otpText.text = resources.getString(R.string.otp)
                    binding.otp.text = response.booking_details.otp
                }
//            }
        } else {
            binding.userName.text = "${response.booking_details.sp_fname} ${response.booking_details.sp_lname}"
            binding.occupation.text = response.booking_details.sp_profession
            Glide.with(binding.profilePic)
                .load(RetrofitBuilder.BASE_URL + response.booking_details.sp_profile_pic)
                .error(R.drawable.images)
                .into(binding.profilePic)
//            if (response.booking_details.otp_raised_by == response.booking_details.sp_id) {
//                if (FROM_COMPLETED && !FROM_PENDING) {
                if (!FROM_PENDING) {
                    binding.otpText.text = resources.getString(R.string.time_lapsed)
                    binding.otp.text = response.booking_details.time_lapsed
                } else {
                    binding.otpText.text = resources.getString(R.string.otp)
                    binding.otp.text = response.booking_details.otp
                }
//            }
        }
        if (FROM_COMPLETED) {
            binding.otp.text = response.booking_details.time_lapsed
        }
        if (FROM_PENDING) {
            binding.otpText.text = resources.getString(R.string.otp)
            binding.otp.text = response.booking_details.otp
        }

        binding.bookingIdText.text = bookingId

        binding.jobDetailsRV.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.attachmentsRV.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        if (response.job_details.isEmpty()) {
            binding.jobDetailsText.visibility = View.GONE
        }
        if (response.attachments.isEmpty()) {
            binding.attachmentsText.visibility = View.GONE
        }

        binding.jobDetailsRV.adapter = JobDetailsAdapter(response.job_details, categoryId)
        binding.attachmentsRV.adapter = JobDetailsAttachmentsAdapter(response.attachments)

        binding.cancelBookingBtn.setOnClickListener {
            if (FROM_PROVIDER) {
                UserBookingCancelScreen.FROM_PROVIDER = true
            }
            val intent = Intent(binding.root.context, UserBookingCancelScreen::class.java)
            intent.putExtra(binding.root.context.getString(R.string.booking_id), bookingId)
            intent.putExtra(binding.root.context.getString(R.string.category_id), categoryId)
            intent.putExtra(binding.root.context.getString(R.string.user_id), userId)
            binding.root.context.startActivity(intent)
        }
        binding.reScheduleBtn.setOnClickListener {
            RESCHEDULE = true
            ViewBidsScreen.bookingId = bookingId.toInt()
            BookingDateAndTimeScreen.FROM_PROVIDER = FROM_PROVIDER
            UserUtils.spid = response.booking_details.sp_id
            UserUtils.re_scheduled_date = response.booking_details.scheduled_date
            UserUtils.re_scheduled_time_slot_from = response.booking_details.time_slot_id
            startActivity(Intent(this, BookingDateAndTimeScreen::class.java))
        }

        binding.raiseExtraDemandBtn.setOnClickListener {
            showExtraDemandDialog()
        }

        if (!FROM_PROVIDER) {
            binding.requestInstallmentBtn.setBackgroundResource(R.drawable.blue_out_line)
            binding.requestInstallmentBtn.setTextColor(resources.getColor(R.color.blue))
            if (response.booking_details.post_job_id != "0") {
                binding.requestInstallmentBtn.visibility = View.VISIBLE
            } else {
                binding.requestInstallmentBtn.visibility = View.GONE
            }
            if (!response.booking_details.extra_demand_status.isNullOrBlank()) {
                if (response.booking_details.extra_demand_total_amount != "0") {
                    if (response.booking_details.extra_demand_status == "0") {
                        showExtraDemandAcceptDialog(
                            bookingId.toInt(),
                            response.booking_details.material_advance,
                            response.booking_details.technician_charges,
                            response.booking_details.extra_demand_total_amount,
                            progressDialog
                        )
                    }
                }
            }
        } else {
            binding.requestInstallmentBtn.visibility = View.GONE
        }

        binding.requestInstallmentBtn.setOnClickListener {
            UserInstallmentsRequestScreen.postJobId = response.booking_details.post_job_id.toInt()
            UserUtils.spid = response.booking_details.sp_id
            ProviderReleaseGoalsScreen.userId = userId
            if (FROM_PROVIDER) {
                ProviderReleaseGoalsScreen.userId = response.booking_details.post_job_id
                startActivity(Intent(this, ProviderReleaseGoalsScreen::class.java))
            } else {
                startActivity(Intent(this, UserInstallmentsRequestScreen::class.java))
            }

        }

        if (categoryId == "2") {
            binding.viewFilesBtn.visibility = View.VISIBLE
        } else {
            binding.viewFilesBtn.visibility = View.GONE
        }

    }

    private fun showExtraDemandAcceptDialog(
        bookingId: Int,
        materialAdvance: String,
        technicalCharges: String,
        extraDemandTotalAmount: String,
        progressDialog: BeautifulProgressDialog
    ) {
        val extraDemandAcceptRejectDialog = BottomSheetDialog(this)
        val dialogView = layoutInflater.inflate(R.layout.provider_extra_demand_accept_dialog, null)
        val materialCharges = dialogView.findViewById<TextView>(R.id.materialCharges)
        val technicianCharges = dialogView.findViewById<TextView>(R.id.technicianCharges)
        val totalCost = dialogView.findViewById<TextView>(R.id.totalCost)
        val acceptBtn = dialogView.findViewById<TextView>(R.id.acceptBtn)
        val rejectBtn = dialogView.findViewById<TextView>(R.id.rejectBtn)
        val closeBtn = dialogView.findViewById<MaterialCardView>(R.id.closeBtn)
        materialCharges.text = materialAdvance
        technicianCharges.text = technicalCharges
        totalCost.text = extraDemandTotalAmount

        closeBtn.setOnClickListener { extraDemandAcceptRejectDialog.dismiss() }

        acceptBtn.setOnClickListener {
            changeExtraDemandStatus(bookingId, 1, extraDemandAcceptRejectDialog, progressDialog)
        }

        rejectBtn.setOnClickListener {
            changeExtraDemandStatus(bookingId, 2, extraDemandAcceptRejectDialog, progressDialog)
        }

        extraDemandAcceptRejectDialog.setCancelable(false)
        extraDemandAcceptRejectDialog.setContentView(dialogView)
        extraDemandAcceptRejectDialog.show()
    }

    private fun changeExtraDemandStatus(
        bookingId: Int,
        status: Int,
        dialog: BottomSheetDialog,
        progressDialog: BeautifulProgressDialog
    ) {

        val factory = ViewModelFactory(BookingRepository())
        val viewModel = ViewModelProvider(this, factory)[BookingViewModel::class.java]
        val requestBody =
            ChangeExtraDemandStatusReqModel(bookingId, RetrofitBuilder.USER_KEY, status)
        viewModel.changeExtraDemandStatus(this, requestBody).observe(this) {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    dialog.dismiss()
                    if (status == 1) {
                        UserUtils.sendExtraDemandFCM(
                            this, response.booking_details.sp_fcm_token,
                            bookingId.toString(),
                            categoryId,
                            "$userId|1"
                        )
                        snackBar(binding.inProgressViewStatusBtn, "Extra Demand Accepted")
                    } else {
                        UserUtils.sendExtraDemandFCM(
                            this, response.booking_details.sp_fcm_token,
                            bookingId.toString(),
                            categoryId,
                            "$userId|2"
                        )
                        snackBar(binding.inProgressViewStatusBtn, "Extra Demand Rejected")
                    }
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    toast(this, it.message!!)
                }
            }
        }

    }

    @SuppressLint("SimpleDateFormat")
    private fun showExtraDemandDialog() {
        val dialog = BottomSheetDialog(this)
        val dialogView = layoutInflater.inflate(R.layout.provider_extra_demand_dialog, null)
        val closeBtn = dialogView.findViewById<MaterialCardView>(R.id.closeBtn)
        val materialCharges = dialogView.findViewById<TextInputEditText>(R.id.materialCharges)
        val technicianCharges = dialogView.findViewById<TextInputEditText>(R.id.technicianCharges)
        val submitBtn = dialogView.findViewById<TextView>(R.id.submitBtn)
        val totalCost = dialogView.findViewById<TextView>(R.id.totalCost)

        materialCharges.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            @SuppressLint("SetTextI18n")
            override fun afterTextChanged(s: Editable?) {
                if (materialCharges.text.toString().isNotEmpty()) {
                    totalCost.text = materialCharges.text.toString()
                }
                if (technicianCharges.text.toString().isNotEmpty()) {
                    totalCost.text = materialCharges.text.toString()
                }
                if (technicianCharges.text.toString()
                        .isNotEmpty() && materialCharges.text.toString().isNotEmpty()
                ) {
                    totalCost.text =
                        (materialCharges.text.toString().toInt() + technicianCharges.text.toString()
                            .toInt()).toString()
                }
            }

        })
        technicianCharges.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            @SuppressLint("SetTextI18n")
            override fun afterTextChanged(s: Editable?) {
                if (materialCharges.text.toString().isNotEmpty()) {
                    totalCost.text = materialCharges.text.toString()
                }
                if (technicianCharges.text.toString().isNotEmpty()) {
                    totalCost.text = materialCharges.text.toString()
                }
                if (technicianCharges.text.toString()
                        .isNotEmpty() && materialCharges.text.toString().isNotEmpty()
                ) {
                    totalCost.text =
                        (materialCharges.text.toString().toInt() + technicianCharges.text.toString()
                            .toInt()).toString()
                }
            }

        })

        submitBtn.setOnClickListener {

            when {
                materialCharges.text.toString().isEmpty() && technicianCharges.text.toString()
                    .isEmpty() -> {
                    toast(this, "Enter Material Charges or Technician Charges")
                }
                else -> {

                    val mCharges = materialCharges.text.toString()
                    val tCharges = technicianCharges.text.toString()

                    val factory = ViewModelFactory(ProviderBookingRepository())
                    val viewModel =
                        ViewModelProvider(this, factory)[ProviderBookingViewModel::class.java]
                    val requestBody = ExtraDemandReqModel(
                        bookingId,
                        SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format((Date())),
                        totalCost.text.toString().trim(),
                        mCharges,
                        tCharges,
                        RetrofitBuilder.PROVIDER_KEY
                    )
                    viewModel.extraDemand(this, requestBody).observe(this) {
                        when (it) {
                            is NetworkResponse.Loading -> {
                                progressDialog.show()
                            }
                            is NetworkResponse.Success -> {
                                UserUtils.sendExtraDemandFCM(
                                    this,
                                    response.booking_details.fcm_token,
                                    bookingId,
                                    categoryId,
                                    userId
                                )
                                progressDialog.dismiss()
                                dialog.dismiss()
                                snackBar(binding.inProgressViewStatusBtn, "Extra Demand Raised")
                            }
                            is NetworkResponse.Failure -> {
                                progressDialog.dismiss()
                                toast(this, it.message!!)
                            }
                        }
                    }
                }
            }
        }
        closeBtn.setOnClickListener { dialog.dismiss() }
        dialog.setContentView(dialogView)
        dialog.show()
    }

    @SuppressLint("SetTextI18n")
    private fun showotpInDialog(otp: String) {
        val bottomSheetDialog = BottomSheetDialog(this)
        val bottomSheet = layoutInflater.inflate(R.layout.booking_closing_dialog, null)
        val title = bottomSheet.findViewById<TextView>(R.id.title)
        val firstNo = bottomSheet.findViewById<TextView>(R.id.firstNo)
        val secondNo = bottomSheet.findViewById<TextView>(R.id.secondNo)
        val thirdNo = bottomSheet.findViewById<TextView>(R.id.thirdNo)
        val fourthNo = bottomSheet.findViewById<TextView>(R.id.fourthNo)
        val submitBtn = bottomSheet.findViewById<TextView>(R.id.submitBtn)
        val closeBtn = bottomSheet.findViewById<MaterialCardView>(R.id.closeBtn)
        title.text = "OTP to Start Job"
        if (FROM_PROVIDER) {
            firstNo.setBackgroundResource(R.drawable.otp_digit_purple_bg)
            secondNo.setBackgroundResource(R.drawable.otp_digit_purple_bg)
            thirdNo.setBackgroundResource(R.drawable.otp_digit_purple_bg)
            fourthNo.setBackgroundResource(R.drawable.otp_digit_purple_bg)
        } else {
            firstNo.setBackgroundResource(R.drawable.otp_digit_blue_bg)
            secondNo.setBackgroundResource(R.drawable.otp_digit_blue_bg)
            thirdNo.setBackgroundResource(R.drawable.otp_digit_blue_bg)
            fourthNo.setBackgroundResource(R.drawable.otp_digit_blue_bg)
        }
        firstNo.text = otp[0].toString()
        secondNo.text = otp[1].toString()
        thirdNo.text = otp[2].toString()
        fourthNo.text = otp[3].toString()
        submitBtn.text = "Close"
        closeBtn.setOnClickListener {
            if (FROM_PROVIDER) {
                ProviderRatingReviewScreen.FROM_PROVIDER = true
                ProviderRatingReviewScreen.bookingId = ProviderBookingDetailsScreen.bookingId
                ProviderRatingReviewScreen.categoryId = ProviderBookingDetailsScreen.categoryId
                ProviderRatingReviewScreen.userId = ProviderBookingDetailsScreen.userId
                startActivity(Intent(this@ViewUserBookingDetailsScreen, ProviderRatingReviewScreen::class.java))
            } else {
                startActivity(Intent(this, UserMyBookingsScreen::class.java))
                bottomSheetDialog.dismiss()
            }
        }
        submitBtn.setOnClickListener {
            if (FROM_PROVIDER) {
                ProviderRatingReviewScreen.FROM_PROVIDER = true
                ProviderRatingReviewScreen.bookingId = ProviderBookingDetailsScreen.bookingId
                ProviderRatingReviewScreen.categoryId = ProviderBookingDetailsScreen.categoryId
                ProviderRatingReviewScreen.userId = ProviderBookingDetailsScreen.userId
                startActivity(Intent(this@ViewUserBookingDetailsScreen, ProviderRatingReviewScreen::class.java))
            } else {
                bottomSheetDialog.dismiss()
                startActivity(Intent(this, UserMyBookingsScreen::class.java))
            }
        }
        bottomSheetDialog.setContentView(bottomSheet)
        bottomSheetDialog.setCancelable(false)
        bottomSheetDialog.show()
    }

    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(this).registerReceiver((myReceiver), IntentFilter(FCMService.EXTRA_DEMAND_ACCEPT_REJECT))
        LocalBroadcastManager.getInstance(this).registerReceiver((otpReceiver), IntentFilter(FCMService.OTP_INTENT_FILTER))
        LocalBroadcastManager.getInstance(this).registerReceiver((otpResponseReceiver), IntentFilter(FCMService.OTP_RESPONSE_INTENT_FILTER))
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(myReceiver)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(otpReceiver)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(otpResponseReceiver)
    }

    private val myReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun onReceive(context: Context, intent: Intent) {
            val bookingId = intent.getStringExtra(getString(R.string.booking_id))!!
            val categoryId = intent.getStringExtra(getString(R.string.category_id))!!
            val userId = intent.getStringExtra(getString(R.string.user_id))!!
            openBookingDetails(bookingId, categoryId, userId)
        }
    }

    private val otpReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun onReceive(context: Context, intent: Intent) {
            val bookingId = intent.getStringExtra(getString(R.string.booking_id))!!
            val otp = intent.getStringExtra(getString(R.string.category_id))!!
            val userId = intent.getStringExtra(getString(R.string.user_id))!!
            if (!(context as Activity).isFinishing) {
                showotpInDialog(otp)
            }
        }
    }

    private val otpResponseReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun onReceive(context: Context, intent: Intent) {
            val intentService = Intent(context, UserMyBookingDetailsScreen::class.java)
            intentService.putExtra(getString(R.string.booking_id), intent.getStringExtra(getString(R.string.booking_id))!!)
            intentService.putExtra(getString(R.string.category_id), intent.getStringExtra(getString(R.string.category_id))!!)
            intentService.putExtra(getString(R.string.user_id), intent.getStringExtra(getString(R.string.user_id))!!)
            startActivity(intentService)
        }
    }

    fun openBookingDetails(bookingId: String, categoryId: String, userId: String) {
        val factory = ViewModelFactory(BookingRepository())
        val viewModel = ViewModelProvider(this, factory)[BookingViewModel::class.java]
        val requestBody = BookingDetailsReqModel(bookingId.toInt(), categoryId.toInt(), RetrofitBuilder.USER_KEY, userId.toInt())
        Log.e("PROVIDER RESPONSE", Gson().toJson(requestBody))
        CoroutineScope(Dispatchers.Main).launch {
            progressDialog.show()
            val response = RetrofitBuilder.getUserRetrofitInstance().getUserBookingDetails(requestBody)
            if (response.status == 200) {
                progressDialog.dismiss()
                updateUI(response, bookingId)
            } else {
                progressDialog.dismiss()
                snackBar(binding.inProgressViewStatusBtn, response.message)
            }
        }
//        viewModel.viewBookingDetails(this, requestBody).observe(this, {
//            when (it) {
//                is NetworkResponse.Loading -> {
//                    progressDialog.show()
//                }
//                is NetworkResponse.Success -> {
//                    progressDialog.dismiss()
//                    updateUI(it.data!!, bookingId)
//                }
//                is NetworkResponse.Failure -> {
//                    progressDialog.dismiss()
//                    snackBar(UserDashboardScreen.binding.navigationView, it.message!!)
//                }
//            }
//        })
    }

    private fun updateUI(
        response: BookingDetailsResModel,
        bookingId: String
    ) {
        if (!response.booking_details.extra_demand_status.isNullOrBlank()) {
            if (response.booking_details.extra_demand_total_amount != "0") {
                if (response.booking_details.extra_demand_status == "0") {
                    showExtraDemandAcceptDialog(
                        bookingId.toInt(),
                        response.booking_details.material_advance,
                        response.booking_details.technician_charges,
                        response.booking_details.extra_demand_total_amount,
                        progressDialog
                    )
                }
            }
        }
    }

}