package com.satrango.ui.service_provider.provider_dashboard

import android.annotation.SuppressLint
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.basusingh.beautifulprogressdialog.BeautifulProgressDialog
import com.satrango.R
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivityProviderRejectBookingScreenBinding
import com.satrango.remote.NetworkResponse
import com.satrango.remote.RetrofitBuilder
import com.satrango.remote.fcm.FCMService
import com.satrango.ui.service_provider.provider_dashboard.dashboard.ProviderDashboard
import com.satrango.ui.user.bookings.booking_address.BookingRepository
import com.satrango.ui.user.bookings.booking_address.BookingViewModel
import com.satrango.ui.user.bookings.view_booking_details.models.BookingDetailsResModel
import com.satrango.ui.user.bookings.view_booking_details.models.ProviderResponseReqModel
import com.satrango.utils.UserUtils
import com.satrango.utils.snackBar
import java.text.SimpleDateFormat
import java.util.*

class ProviderRejectBookingScreen : AppCompatActivity() {

    private lateinit var bookingViewModel: BookingViewModel
    private lateinit var progressDialog: BeautifulProgressDialog
    private lateinit var binding: ActivityProviderRejectBookingScreenBinding
    private var reason = ""

    companion object {
        var bookingType = ""
        var userId: String = ""
        var bookingId: String = ""
        var response: BookingDetailsResModel? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProviderRejectBookingScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeToolBar()
        initializeProgressDialog()

        val bookingFactory = ViewModelFactory(BookingRepository())
        bookingViewModel = ViewModelProvider(this, bookingFactory)[BookingViewModel::class.java]

        binding.apply {

            locationTooFarBtn.setOnClickListener {
                locationTooFarBtn.setBackgroundResource(R.drawable.provider_btn_bg)
                locationTooFarBtn.setTextColor(resources.getColor(R.color.white))
                notInterestedBtn.setBackgroundResource(R.drawable.purple_out_line)
                notInterestedBtn.setTextColor(resources.getColor(R.color.purple_500))
                busyAtTimeBtn.setBackgroundResource(R.drawable.purple_out_line)
                busyAtTimeBtn.setTextColor(resources.getColor(R.color.purple_500))
                skillsNotSetBtn.setBackgroundResource(R.drawable.purple_out_line)
                skillsNotSetBtn.setTextColor(resources.getColor(R.color.purple_500))
                othersBtn.setBackgroundResource(R.drawable.purple_out_line)
                othersBtn.setTextColor(resources.getColor(R.color.purple_500))
                reason = "Location too far"
            }

            notInterestedBtn.setOnClickListener {
                notInterestedBtn.setBackgroundResource(R.drawable.provider_btn_bg)
                notInterestedBtn.setTextColor(resources.getColor(R.color.white))
                locationTooFarBtn.setBackgroundResource(R.drawable.purple_out_line)
                locationTooFarBtn.setTextColor(resources.getColor(R.color.purple_500))
                busyAtTimeBtn.setBackgroundResource(R.drawable.purple_out_line)
                busyAtTimeBtn.setTextColor(resources.getColor(R.color.purple_500))
                skillsNotSetBtn.setBackgroundResource(R.drawable.purple_out_line)
                skillsNotSetBtn.setTextColor(resources.getColor(R.color.purple_500))
                othersBtn.setBackgroundResource(R.drawable.purple_out_line)
                othersBtn.setTextColor(resources.getColor(R.color.purple_500))
                reason = "Not Interested"
            }

            busyAtTimeBtn.setOnClickListener {
                busyAtTimeBtn.setBackgroundResource(R.drawable.provider_btn_bg)
                busyAtTimeBtn.setTextColor(resources.getColor(R.color.white))
                locationTooFarBtn.setBackgroundResource(R.drawable.purple_out_line)
                locationTooFarBtn.setTextColor(resources.getColor(R.color.purple_500))
                notInterestedBtn.setBackgroundResource(R.drawable.purple_out_line)
                notInterestedBtn.setTextColor(resources.getColor(R.color.purple_500))
                skillsNotSetBtn.setBackgroundResource(R.drawable.purple_out_line)
                skillsNotSetBtn.setTextColor(resources.getColor(R.color.purple_500))
                othersBtn.setBackgroundResource(R.drawable.purple_out_line)
                othersBtn.setTextColor(resources.getColor(R.color.purple_500))
                reason = "Busy at that time"
            }

            skillsNotSetBtn.setOnClickListener {
                skillsNotSetBtn.setBackgroundResource(R.drawable.provider_btn_bg)
                skillsNotSetBtn.setTextColor(resources.getColor(R.color.white))
                locationTooFarBtn.setBackgroundResource(R.drawable.purple_out_line)
                locationTooFarBtn.setTextColor(resources.getColor(R.color.purple_500))
                notInterestedBtn.setBackgroundResource(R.drawable.purple_out_line)
                notInterestedBtn.setTextColor(resources.getColor(R.color.purple_500))
                busyAtTimeBtn.setBackgroundResource(R.drawable.purple_out_line)
                busyAtTimeBtn.setTextColor(resources.getColor(R.color.purple_500))
                othersBtn.setBackgroundResource(R.drawable.purple_out_line)
                othersBtn.setTextColor(resources.getColor(R.color.purple_500))
                reason = "Skillset Not Matching"
            }

            othersBtn.setOnClickListener {
                othersBtn.setBackgroundResource(R.drawable.provider_btn_bg)
                othersBtn.setTextColor(resources.getColor(R.color.white))
                locationTooFarBtn.setBackgroundResource(R.drawable.purple_out_line)
                locationTooFarBtn.setTextColor(resources.getColor(R.color.purple_500))
                notInterestedBtn.setBackgroundResource(R.drawable.purple_out_line)
                notInterestedBtn.setTextColor(resources.getColor(R.color.purple_500))
                busyAtTimeBtn.setBackgroundResource(R.drawable.purple_out_line)
                busyAtTimeBtn.setTextColor(resources.getColor(R.color.purple_500))
                skillsNotSetBtn.setBackgroundResource(R.drawable.purple_out_line)
                skillsNotSetBtn.setTextColor(resources.getColor(R.color.purple_500))
                reason = "Others"
            }

            backBtn.setOnClickListener {
                onBackPressed()
            }

            submitBtn.setOnClickListener {

                validate()
            }

        }

    }

    @SuppressLint("SimpleDateFormat")
    private fun validate() {
        binding.apply {
            reason = feedBack.text.toString().trim()
            if (feedBack.text.toString().isEmpty() && reason == "Others") {
                snackBar(submitBtn, "Please Enter Reason")
            } else if (reason.isEmpty()) {
                snackBar(submitBtn, "Please Select or Enter Reason")
            } else {
                val requestBody = ProviderResponseReqModel(
                    response!!.booking_details.amount,
                    bookingId.toInt(),
                    SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date()),
                    reason,
                    RetrofitBuilder.USER_KEY,
                    UserUtils.getUserId(this@ProviderRejectBookingScreen).toInt(),
                    4,
                    userId.toInt()
                )
                bookingViewModel.setProviderResponse(this@ProviderRejectBookingScreen, requestBody)
                    .observe(this@ProviderRejectBookingScreen, {
                        when (it) {
                            is NetworkResponse.Loading -> {
                                progressDialog.show()
                            }
                            is NetworkResponse.Success -> {
                                progressDialog.dismiss()
                                UserUtils.sendFCM(this@ProviderRejectBookingScreen, response!!.booking_details.fcm_token, "reject", "reject", "reject|$bookingType|$reason")
                                if (FCMService.notificationManager != null) {
                                    FCMService.notificationManager.cancelAll()
                                }
                                binding.feedBack.setText("")
                                ProviderDashboard.bookingId = ""
                                UserUtils.saveFromFCMService(this@ProviderRejectBookingScreen, false)
                                ProviderDashboard.bottomSheetDialog!!.dismiss()
                                snackBar(binding.backBtn, "Booking Rejected Successfully")
                                Handler().postDelayed({
                                    onBackPressed()
                                },3000)
                            }
                            is NetworkResponse.Failure -> {
                                progressDialog.dismiss()
                                snackBar(binding.submitBtn, it.message!!)
                            }
                        }
                    })
            }

        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initializeProgressDialog() {
        progressDialog = BeautifulProgressDialog(this, BeautifulProgressDialog.withGIF, resources.getString(R.string.loading))
        progressDialog.setGifLocation(Uri.parse("android.resource://${packageName}/${R.drawable.blue_loading}"))
        progressDialog.setLayoutColor(resources.getColor(R.color.progressDialogColor))
    }

    private fun initializeToolBar() {
        val toolBar = binding.root.findViewById<View>(R.id.toolBar)
        val backTextBtn = toolBar.findViewById<TextView>(R.id.toolBarBackTVBtn)
        backTextBtn.text = resources.getString(R.string.back)
        backTextBtn.setOnClickListener { onBackPressed() }
        toolBar.findViewById<ImageView>(R.id.toolBarBackBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text =
            resources.getString(R.string.reject_the_booking)
        val imageView = toolBar.findViewById<ImageView>(R.id.toolBarImage)
        imageView.visibility = View.GONE
    }
}