package com.satrango.service_provider_dashboard

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.satrango.databinding.ActivityServiceProviderDashboardBinding

class ServiceProviderDashboard : AppCompatActivity() {

    private lateinit var binding: ActivityServiceProviderDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityServiceProviderDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}