package com.satrango.ui.user_dashboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.satrango.R
import com.satrango.databinding.FragmentUserHomeScreenBinding

class UserHomeScreen : Fragment() {

    private lateinit var binding: FragmentUserHomeScreenBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserHomeScreenBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

}