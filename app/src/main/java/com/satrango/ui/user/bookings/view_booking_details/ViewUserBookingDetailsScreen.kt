package com.satrango.ui.user.bookings.view_booking_details

import android.annotation.SuppressLint
import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.satrango.R
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivityViewUserBookingDetailsScreenBinding
import com.satrango.remote.NetworkResponse
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.user.bookings.booking_address.BookingRepository
import com.satrango.ui.user.bookings.booking_address.BookingViewModel
import com.satrango.ui.user.bookings.booking_attachments.AttachmentsAdapter
import com.satrango.ui.user.bookings.view_booking_details.models.BookingDetailsReqModel
import com.satrango.ui.user.bookings.view_booking_details.models.BookingDetailsResModel
import com.satrango.ui.user.bookings.view_booking_details.models.ProviderResponseReqModel
import com.satrango.utils.UserUtils
import com.satrango.utils.snackBar
import java.text.SimpleDateFormat
import java.util.*

class ViewUserBookingDetailsScreen : AppCompatActivity() {

    private lateinit var response: BookingDetailsResModel
    private lateinit var binding: ActivityViewUserBookingDetailsScreenBinding
    private lateinit var progressDialog: ProgressDialog

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

        val bookingId = intent.getStringExtra(getString(R.string.booking_id))!!
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
                viewModel.setProviderResponse(this@ViewUserBookingDetailsScreen, requestBody).observe(this@ViewUserBookingDetailsScreen, {
                    when(it) {
                        is NetworkResponse.Loading -> {
                            progressDialog.show()
                        }
                        is NetworkResponse.Success -> {
                            progressDialog.dismiss()
                            Log.e("AMOUNT", response.booking_details.amount)
                            UserUtils.sendFCM(this@ViewUserBookingDetailsScreen, response.booking_details.fcm_token, "accept",  "accept|" + response.booking_details.amount + "|${response.booking_details.sp_id}|provider")
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
                viewModel.setProviderResponse(this@ViewUserBookingDetailsScreen, requestBody).observe(this@ViewUserBookingDetailsScreen, {
                    when(it) {
                        is NetworkResponse.Loading -> {
                            progressDialog.show()
                        }
                        is NetworkResponse.Success -> {
                            progressDialog.dismiss()
                            UserUtils.sendFCM(this@ViewUserBookingDetailsScreen, response.booking_details.fcm_token, "reject",   "provider")
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

    @SuppressLint("SetTextI18n")
    private fun updateUI(response: BookingDetailsResModel) {
        binding.userName.text = response.booking_details.fname + " " + response.booking_details.lname
        binding.mobileNo.text = response.booking_details.mobile
        binding.scheduleDate.text = response.booking_details.scheduled_date
        binding.fromDate.text = response.booking_details.from
        binding.amount.text = "Rs ${response.booking_details.amount}"
        binding.estimateTime.text = response.booking_details.estimate_time + " " + response.booking_details.estimate_type
        binding.jobDetailsRV.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.attachmentsRV.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.jobDetailsRV.adapter = JobDetailsAdapter(response.job_details)
        binding.attachmentsRV.adapter = JobDetailsAttachmentsAdapter(response.attachments)
    }
}