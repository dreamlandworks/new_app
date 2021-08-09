package com.satrango.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import com.satrango.databinding.ActivityUserLoginTypeScreenBinding
import com.satrango.ui.auth.provider_signup.provider_sign_up_one.ProviderSignUpOne
import com.satrango.ui.service_provider.provider_dashboard.dashboard.ProviderDashboard
import com.satrango.ui.user.user_dashboard.UserDashboardScreen
import com.satrango.utils.toast
import org.json.JSONObject
import java.lang.Exception


class UserLoginTypeScreen : AppCompatActivity(), PaymentResultListener {

    private lateinit var binding: ActivityUserLoginTypeScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserLoginTypeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM ERROR:", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            val token = task.result
            toast(this, token)
            Log.e("FCM TOKEN", token)
        })

        binding.apply {

            userBtn.setOnClickListener {
                startActivity(Intent(this@UserLoginTypeScreen, UserDashboardScreen::class.java))
                finish()
            }

            serviceProviderBtn.setOnClickListener {
                startActivity(Intent(this@UserLoginTypeScreen, ProviderDashboard::class.java))
//                startActivity(Intent(this@UserLoginTypeScreen, ProviderSignUpOne::class.java))
                finish()
            }

        }

//        Checkout.preload(applicationContext)
//        makePayment()
    }

    private fun makePayment() {
        val checkout = Checkout()
        checkout.setKeyID("KEY")
        try {
            val orderRequest = JSONObject()
            orderRequest.put("amount", 50000) //500rs * 100 = 50000
            orderRequest.put("currency", "INR")
            orderRequest.put("receipt", "order_rcptid_11")
            checkout.open(this, orderRequest)
        } catch (e: Exception) {
            toast(this, e.message!!)
        }

    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }

    override fun onPaymentSuccess(response: String?) {
        toast(this, response!!)
    }

    override fun onPaymentError(p0: Int, response: String?) {
        toast(this, response!!)
    }
}