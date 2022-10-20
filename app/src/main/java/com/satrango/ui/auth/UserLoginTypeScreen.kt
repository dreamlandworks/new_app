package com.satrango.ui.auth

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.satrango.databinding.ActivityUserLoginTypeScreenBinding
import com.satrango.ui.service_provider.provider_dashboard.dashboard.ProviderDashboard
import com.satrango.ui.user.user_dashboard.UserDashboardScreen
import com.satrango.utils.UserUtils


class UserLoginTypeScreen : AppCompatActivity() {

    private lateinit var binding: ActivityUserLoginTypeScreenBinding

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserLoginTypeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Firebase.messaging.isAutoInitEnabled = true

        if (!UserUtils.updateNewFCMToken(this)) {
//            snackBar(binding.userBtn, "Please check internet connection!")
            Handler().postDelayed({
                finish()
            }, 3000)
        }

        binding.apply {

            userBtn.setOnClickListener {
                startActivity(Intent(this@UserLoginTypeScreen, UserDashboardScreen::class.java))
                finish()
            }
            serviceProviderBtn.setOnClickListener {
                UserUtils.saveFromFCMService(this@UserLoginTypeScreen, false)
                startActivity(Intent(this@UserLoginTypeScreen, ProviderDashboard::class.java))
                finish()
            }

        }
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }


}