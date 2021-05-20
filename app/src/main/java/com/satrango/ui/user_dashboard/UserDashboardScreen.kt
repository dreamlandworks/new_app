package com.satrango.ui.user_dashboard

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.satrango.databinding.ActivityUserDashboardScreenBinding

class UserDashboardScreen : AppCompatActivity() {

    private lateinit var binding: ActivityUserDashboardScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserDashboardScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}