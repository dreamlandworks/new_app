package com.satrango.ui.user.user_dashboard.search_service_providers

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.satrango.databinding.ActivitySearchServiceProvidersScreenBinding

class SearchServiceProvidersScreen : AppCompatActivity() {

    private lateinit var binding: ActivitySearchServiceProvidersScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchServiceProvidersScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)



    }
}