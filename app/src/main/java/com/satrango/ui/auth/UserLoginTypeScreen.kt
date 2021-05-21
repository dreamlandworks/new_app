package com.satrango.ui.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.satrango.databinding.ActivityUserLoginTypeScreenBinding
import com.satrango.ui.provider_dashboard.ProviderDashboard
import com.satrango.ui.user_dashboard.UserDashboardScreen

class UserLoginTypeScreen : AppCompatActivity() {

    private lateinit var binding: ActivityUserLoginTypeScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserLoginTypeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {

            userBtn.setOnClickListener {
                startActivity(Intent(this@UserLoginTypeScreen, UserDashboardScreen::class.java))
            }

            serviceProviderBtn.setOnClickListener {
                startActivity(Intent(this@UserLoginTypeScreen, ProviderDashboard::class.java))
            }

        }

    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }
}