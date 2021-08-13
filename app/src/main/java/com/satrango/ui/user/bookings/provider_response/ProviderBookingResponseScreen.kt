package com.satrango.ui.user.bookings.provider_response

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import com.satrango.R
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivityProviderBookingResponseScreenBinding
import com.satrango.remote.NetworkResponse
import com.satrango.remote.RetrofitBuilder
import com.satrango.remote.fcm.FCMService
import com.satrango.ui.user.bookings.booking_address.BookingRepository
import com.satrango.ui.user.bookings.booking_address.BookingViewModel
import com.satrango.ui.user.user_dashboard.UserDashboardScreen
import com.satrango.ui.user.user_dashboard.search_service_providers.models.SearchServiceProviderResModel
import com.satrango.utils.UserUtils
import com.satrango.utils.snackBar
import com.satrango.utils.toast
import org.json.JSONObject

class ProviderBookingResponseScreen : AppCompatActivity(), PaymentResultListener {

    private lateinit var userId: String
    private lateinit var amount: String
    private lateinit var binding: ActivityProviderBookingResponseScreenBinding
    private lateinit var viewModel: BookingViewModel
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProviderBookingResponseScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val response = intent.getStringExtra("response")!!
        val factory = ViewModelFactory(BookingRepository())
        viewModel = ViewModelProvider(this, factory)[BookingViewModel::class.java]
        initializeProgressDialog()
        amount = response.split("|")[1]
        toast(this, amount)
        userId = response.split("|")[2]

        if (response.split("|")[0] == "accept") {
            FCMService.INSTANT_BOOKED = true
            binding.reject.visibility = View.GONE
            binding.accept.visibility = View.VISIBLE

            val spDetails = Gson().fromJson(UserUtils.getSelectedSPDetails(this), SearchServiceProviderResModel::class.java)
            for (sp in spDetails.data) {
                if (sp.users_id == userId) {
                    amount = sp.per_hour
                }
            }

            Handler().postDelayed({
                Checkout.preload(applicationContext)
                makePayment()
            }, 3000)

        } else {
            binding.accept.visibility = View.GONE
            binding.reject.visibility = View.VISIBLE
        }

    }

    private fun makePayment() {
        val checkout = Checkout()
        checkout.setKeyID(getString(R.string.razorpay_api_key))
        try {
            val orderRequest = JSONObject()
            orderRequest.put("currency", "INR")
            orderRequest.put("amount", amount.toInt() * 100) // 500rs * 100 = 50000 paisa passed
            orderRequest.put("receipt", "order_rcptid_${System.currentTimeMillis()}")
            orderRequest.put("image", "https://dev.satrango.com/public/assets/img/logo-black.png")
            orderRequest.put("theme.color", R.color.blue)
            checkout.open(this, orderRequest)
        } catch (e: Exception) {
            toast(this, e.message!!)
        }
    }

    override fun onPaymentSuccess(response: String?) {
        updateStatusInServer(response, "Success")
    }

    override fun onPaymentError(p0: Int, error: String?) {
        updateStatusInServer("", "failure")
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
                    snackBar(binding.accept, it.message!!)
                }
            }
        })
    }

    private fun initializeProgressDialog() {
        progressDialog = ProgressDialog(this)
        progressDialog.setCancelable(false)
        progressDialog.setMessage("Loading...")
    }
}