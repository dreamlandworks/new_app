package com.satrango.ui.auth

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import com.google.android.material.card.MaterialCardView
import com.satrango.R
import com.satrango.databinding.ActivityUserLoginTypeScreenBinding
import com.satrango.ui.service_provider.provider_dashboard.ProviderDashboard
import com.satrango.ui.user.user_dashboard.UserDashboardScreen

class UserLoginTypeScreen : AppCompatActivity() {

    private lateinit var binding: ActivityUserLoginTypeScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserLoginTypeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {

            userBtn.setOnClickListener {
                startActivity(Intent(this@UserLoginTypeScreen, UserDashboardScreen::class.java))
                finish()
            }

            serviceProviderBtn.setOnClickListener {
                startActivity(Intent(this@UserLoginTypeScreen, ProviderDashboard::class.java))
                finish()
            }

        }

    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }
}