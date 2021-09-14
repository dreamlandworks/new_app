package com.satrango.ui.user.bookings.view_booking_details

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.satrango.R
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivityViewUserBookingDetailsScreenBinding
import com.satrango.remote.NetworkResponse
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.user.bookings.booking_address.BookingRepository
import com.satrango.ui.user.bookings.booking_address.BookingViewModel
import com.satrango.ui.user.bookings.booking_date_time.BookingDateAndTimeScreen
import com.satrango.ui.user.bookings.view_booking_details.models.BookingDetailsReqModel
import com.satrango.ui.user.bookings.view_booking_details.models.BookingDetailsResModel
import com.satrango.ui.user.bookings.view_booking_details.models.ProviderResponseReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_bookings.MyBookingsRepository
import com.satrango.ui.user.user_dashboard.drawer_menu.my_bookings.MyBookingsViewModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.view_bids.ViewBidsScreen
import com.satrango.utils.UserUtils
import com.satrango.utils.snackBar
import com.satrango.utils.toast
import java.text.SimpleDateFormat
import java.util.*

class ViewUserBookingDetailsScreen : AppCompatActivity() {

    private var bookingId = ""
    private lateinit var response: BookingDetailsResModel
    private lateinit var binding: ActivityViewUserBookingDetailsScreenBinding
    private lateinit var progressDialog: ProgressDialog

    companion object {
        var RESCHEDULE = false
        var FROM_MY_BOOKINGS_SCREEN = false
    }

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewUserBookingDetailsScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Loading...")
        progressDialog.setCancelable(false)

        val factory = ViewModelFactory(BookingRepository())
        val viewModel = ViewModelProvider(this, factory)[BookingViewModel::class.java]

        if (FROM_MY_BOOKINGS_SCREEN) {
            binding.toolBar.title = "My Booking Details"
            binding.toolBar.setBackgroundColor(Color.parseColor("#0A84FF"))
            binding.providerBtnsLayout.visibility = View.GONE
            binding.userBtnsLayout.visibility = View.VISIBLE
        } else {
            binding.providerBtnsLayout.visibility = View.VISIBLE
        }

        bookingId = intent.getStringExtra(getString(R.string.booking_id))!!
        val categoryId = intent.getStringExtra(getString(R.string.category_id))!!
        val userId = intent.getStringExtra(getString(R.string.user_id))!!

        val requestBody = BookingDetailsReqModel(
            bookingId.toInt(),
            categoryId.toInt(),
            RetrofitBuilder.USER_KEY,
            userId.toInt()
        )
        Log.e("PROVIDER RESPONSE", Gson().toJson(requestBody))
        viewModel.viewBookingDetails(this, requestBody).observe(this, {
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
                    snackBar(binding.acceptBtn, it.message!!)
                }
            }
        })
        binding.apply {

            acceptBtn.setOnClickListener {
                val requestBody = ProviderResponseReqModel(
                    response.booking_details.amount,
                    bookingId.toInt(),
                    SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date()),
                    "",
                    RetrofitBuilder.USER_KEY,
                    response.booking_details.sp_id.toInt(),
                    5
                )
                viewModel.setProviderResponse(this@ViewUserBookingDetailsScreen, requestBody)
                    .observe(this@ViewUserBookingDetailsScreen, {
                        when (it) {
                            is NetworkResponse.Loading -> {
                                progressDialog.show()
                            }
                            is NetworkResponse.Success -> {
                                progressDialog.dismiss()
                                Log.e("AMOUNT", response.booking_details.amount)
                                UserUtils.sendFCM(
                                    this@ViewUserBookingDetailsScreen,
                                    response.booking_details.fcm_token,
                                    "accept",
                                    "accept|" + response.booking_details.amount + "|${response.booking_details.sp_id}|provider"
                                )
                            }
                            is NetworkResponse.Failure -> {
                                progressDialog.dismiss()
                                snackBar(binding.acceptBtn, it.message!!)
                            }
                        }
                    })
            }

            rejectBtn.setOnClickListener {
                val requestBody = ProviderResponseReqModel(
                    response.booking_details.amount,
                    bookingId.toInt(),
                    SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date()),
                    "",
                    RetrofitBuilder.USER_KEY,
                    response.booking_details.sp_id.toInt(),
                    5
                )
                viewModel.setProviderResponse(this@ViewUserBookingDetailsScreen, requestBody)
                    .observe(this@ViewUserBookingDetailsScreen, {
                        when (it) {
                            is NetworkResponse.Loading -> {
                                progressDialog.show()
                            }
                            is NetworkResponse.Success -> {
                                progressDialog.dismiss()
                                UserUtils.sendFCM(
                                    this@ViewUserBookingDetailsScreen,
                                    response.booking_details.fcm_token,
                                    "reject",
                                    "reject|" + response.booking_details.amount + "|${response.booking_details.sp_id} + |provider"
                                )
                            }
                            is NetworkResponse.Failure -> {
                                progressDialog.dismiss()
                                snackBar(binding.acceptBtn, it.message!!)
                            }
                        }
                    })
            }

        }

    }

    private fun otpDialog() {

        var requestedOTP = 0

        val dialog = BottomSheetDialog(this)
        val dialogView = layoutInflater.inflate(R.layout.booking_status_change_otp_dialog, null)
        val otp = dialogView.findViewById<TextInputEditText>(R.id.otp)
        val submitBtn = dialogView.findViewById<TextView>(R.id.submitBtn)
        submitBtn.setOnClickListener {

            if (otp.text.toString().trim().isNotEmpty()) {
                if (requestedOTP == otp.text.toString().toInt()) {
                    toast(this, "Confirmed")
                } else {
                    toast(this, "OTP Invalid")
                }
            } else {
                toast(this, "Enter OTP")
            }

        }
        dialog.setCancelable(false)
        dialog.setContentView(dialogView)

        val factory = ViewModelFactory(MyBookingsRepository())
        val viewModel = ViewModelProvider(this, factory)[MyBookingsViewModel::class.java]
        viewModel.otpRequest(this, bookingId.toInt()).observe(this, {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    requestedOTP = it.data!!
                    dialog.show()
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.acceptBtn, it.message!!)
                }
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI(response: BookingDetailsResModel) {
        binding.userName.text =
            response.booking_details.fname + " " + response.booking_details.lname
        binding.mobileNo.text = response.booking_details.mobile
        binding.date.text = response.booking_details.scheduled_date
        binding.amount.text = "Rs ${response.booking_details.amount}"
        binding.time.text = response.booking_details.from
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
        binding.jobDetailsRV.adapter = JobDetailsAdapter(response.job_details)
        binding.attachmentsRV.adapter = JobDetailsAttachmentsAdapter(response.attachments)

        binding.cancelBookingBtn.setOnClickListener { onBackPressed() }
        binding.reScheduleBtn.setOnClickListener {
            ViewBidsScreen.bookingId = bookingId.toInt()
            UserUtils.re_scheduled_date = response.booking_details.scheduled_date
            UserUtils.re_scheduled_time_slot_from = response.booking_details.time_slot_id
            UserUtils.spid = response.booking_details.sp_id
            RESCHEDULE = true
            startActivity(Intent(this, BookingDateAndTimeScreen::class.java))
        }

    }

}