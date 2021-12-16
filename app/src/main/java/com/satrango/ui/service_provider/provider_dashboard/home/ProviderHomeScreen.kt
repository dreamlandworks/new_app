package com.satrango.ui.service_provider.provider_dashboard.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.satrango.databinding.FragmentProviderHomeScreenBinding
import com.satrango.ui.service_provider.provider_dashboard.dashboard.leaderboard.LeaderBoardScreen

class ProviderHomeScreen : Fragment() {

    companion object {
        lateinit var binding: FragmentProviderHomeScreenBinding
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProviderHomeScreenBinding.inflate(inflater, container, false)

        binding.leaderBoard.setOnClickListener {
            startActivity(Intent(requireContext(), LeaderBoardScreen::class.java))
        }

        return binding.root
    }

}