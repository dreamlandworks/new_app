package com.satrango.ui.user_dashboard

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.satrango.databinding.FragmentUserHomeScreenBinding
import com.satrango.utils.PermissionUtils
import com.satrango.utils.UserUtils
import com.satrango.utils.networkAvailable

class UserHomeScreen : Fragment() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var binding: FragmentUserHomeScreenBinding
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserHomeScreenBinding.inflate(layoutInflater, container, false)

        if (PermissionUtils.checkGPSStatus(requireContext()) && networkAvailable(requireContext())) {
            UserDashboardScreen.fetchLocation(requireContext())
        }
        if (UserUtils.getUserName(requireContext()).isNotEmpty()) {
            binding.userName.text = "Hiii, ${UserUtils.getUserName(requireContext())}"
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        UserDashboardScreen.binding.toolBarLayout.visibility = View.VISIBLE
    }

}