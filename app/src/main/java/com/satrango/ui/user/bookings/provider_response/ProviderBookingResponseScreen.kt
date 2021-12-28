package com.satrango.ui.user.bookings.provider_response

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.basusingh.beautifulprogressdialog.BeautifulProgressDialog
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import com.satrango.R
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivityProviderBookingResponseScreenBinding
import com.satrango.remote.NetworkResponse
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.user.bookings.PaymentScreen
import com.satrango.ui.user.bookings.booking_address.BookingRepository
import com.satrango.ui.user.bookings.booking_address.BookingViewModel
import com.satrango.ui.user.user_dashboard.UserDashboardScreen
import com.satrango.ui.user.user_dashboard.search_service_providers.search_service_provider.SearchServiceProvidersScreen
import com.satrango.utils.UserUtils
import com.satrango.utils.snackBar
import com.satrango.utils.toast
import org.json.JSONObject

class ProviderBookingResponseScreen : AppCompatActivity(), PaymentResultListener {

    private lateinit var userId: String
    private lateinit var amount: String
    private lateinit var binding: ActivityProviderBookingResponseScreenBinding
    private lateinit var viewModel: BookingViewModel
    private lateinit var progressDialog: BeautifulProgressDialog

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProviderBookingResponseScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val response = intent.getStringExtra("response")!!
        val factory = ViewModelFactory(BookingRepository())
        viewModel = ViewModelProvider(this, factory)[BookingViewModel::class.java]
        initializeProgressDialog()

        if (response.split("|")[0] == "accept") {
            UserUtils.saveInstantBooking(this, true)
            binding.reject.visibility = View.GONE
            binding.paymentSuccessLayout.visibility = View.GONE
            binding.accept.visibility = View.VISIBLE

            amount = response.split("|")[1]
            userId = response.split("|")[2]

            Handler().postDelayed({
                PaymentScreen.FROM_PROVIDER_BOOKING_RESPONSE = true
                PaymentScreen.FROM_USER_PLANS = false
                PaymentScreen.FROM_PROVIDER_PLANS = false
                PaymentScreen.FROM_USER_BOOKING_ADDRESS = false
                PaymentScreen.FROM_USER_SET_GOALS = false
                PaymentScreen.amount = amount.toDouble()
                startActivity(Intent(this, PaymentScreen::class.java))
//                makePayment()
            }, 3000)
            UserUtils.sendFCMtoAllServiceProviders(this, "accepted", "accepted")
        } else {
            binding.accept.visibility = View.GONE
            binding.paymentSuccessLayout.visibility = View.GONE
            binding.reject.visibility = View.VISIBLE
            binding.rejectNote.text = Html.fromHtml("Looks like Service Provider <b>${response.split("|")[4]}</b>. Shall we choose the best for your need.")
            UserUtils.sendFCMtoAllServiceProviders(this, "accepted", "accepted")
        }

        binding.successCloseBtn.setOnClickListener {
            finish()
            startActivity(Intent(this, SearchServiceProvidersScreen::class.java))
        }

        binding.acceptCloseBtn.setOnClickListener {
            finish()
            startActivity(Intent(this, SearchServiceProvidersScreen::class.java))
        }

        binding.rejectCloseBtn.setOnClickListener {
            finish()
            startActivity(Intent(this, SearchServiceProvidersScreen::class.java))
        }

        binding.noBtn.setOnClickListener {
            finish()
            startActivity(Intent(this, SearchServiceProvidersScreen::class.java))
        }

        binding.yesBtn.setOnClickListener {
            finish()
            startActivity(Intent(this, SearchServiceProvidersScreen::class.java))
        }
    }

    private fun makePayment() {
        val checkout = Checkout()
        checkout.setKeyID(getString(R.string.razorpay_api_key))
        try {
            val orderRequest = JSONObject()
            orderRequest.put("name", "Satrango")
            orderRequest.put("currency", "INR")
            orderRequest.put("amount", amount.toInt() * 100) // 500rs * 100 = 50000 paisa passed
            orderRequest.put("receipt", "order_rcptid_${System.currentTimeMillis()}")
            //orderRequest.put("image", "https://dev.satrango.com/public/assets/img/icon256.png")
            orderRequest.put("theme.color", R.color.blue)
            checkout.open(this, orderRequest)
        } catch (e: Exception) {
            toast(this, e.message!!)
        }
    }

    override fun onPaymentSuccess(response: String?) {
        updateStatusInServer(response, "Success")
        showPaymentSuccessDialog()
    }

    override fun onPaymentError(p0: Int, error: String?) {
        updateStatusInServer("", "Failure")
        snackBar(binding.accept, "Payment Failed, Please try Again!")
    }


    private fun updateStatusInServer(paymentResponse: String?, status: String) {
        val requestBody = PaymentConfirmReqModel(
            amount,
            UserUtils.getBookingId(this),
            UserUtils.scheduled_date,
            RetrofitBuilder.USER_KEY,
            status,
            paymentResponse!!,
            userId.toInt(),
            UserUtils.time_slot_from,
            UserUtils.getUserId(this).toInt()
        )
        viewModel.confirmPayment(this, requestBody).observe(this, {
            when (it) {
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
                    snackBar(binding.accept, it.message!!)
                }
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun showPaymentSuccessDialog() {
        binding.accept.visibility = View.GONE
        binding.reject.visibility = View.GONE
        binding.paymentSuccessLayout.visibility = View.VISIBLE
        binding.orderRefId.text = "Your order is successfully placed. Booking id: ${UserUtils.getBookingRefId(this)}"
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initializeProgressDialog() {
        progressDialog = BeautifulProgressDialog(this, BeautifulProgressDialog.withGIF, resources.getString(R.string.loading))
        progressDialog.setGifLocation(Uri.parse("android.resource://${packageName}/${R.drawable.blue_loading}"))
        progressDialog.setLayoutColor(resources.getColor(R.color.progressDialogColor))
    }

    override fun onBackPressed() {
        finish()
        startActivity(Intent(this, UserDashboardScreen::class.java))
    }
}