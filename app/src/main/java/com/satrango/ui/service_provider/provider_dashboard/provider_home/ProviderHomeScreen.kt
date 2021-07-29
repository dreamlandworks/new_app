package com.satrango.ui.service_provider.provider_dashboard.provider_home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.satrango.databinding.FragmentProviderHomeScreenBinding

class ProviderHomeScreen : Fragment() {

    companion object {
        lateinit var binding: FragmentProviderHomeScreenBinding
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProviderHomeScreenBinding.inflate(inflater, container, false)



        return binding.root
    }

}