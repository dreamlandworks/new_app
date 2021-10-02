package com.satrango.ui.service_provider.provider_dashboard.alerts

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.satrango.R
import com.satrango.base.BaseFragment
import com.satrango.databinding.FragmentProviderAlertsScreenBinding
import com.satrango.remote.NetworkResponse
import com.satrango.ui.user.user_dashboard.user_alerts.UserAlertsAdapter
import com.satrango.utils.PermissionUtils
import com.satrango.utils.loadProfileImage
import com.satrango.utils.snackBar
import com.satrango.utils.toast
import de.hdodenhof.circleimageview.CircleImageView

class ProviderAlertsScreen :
    BaseFragment<ProviderAlertsViewModel, FragmentProviderAlertsScreenBinding, ProviderAlertRepository>() {

    private val ACTIONABLE: String = "1"
    private val NOT_ACTIONABLE: String = "2"

    override fun getFragmentViewModel(): Class<ProviderAlertsViewModel> =
        ProviderAlertsViewModel::class.java

    override fun getFragmentBinding(
        layoutInflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentProviderAlertsScreenBinding =
        FragmentProviderAlertsScreenBinding.inflate(layoutInflater, container, false)

    override fun getFragmentRepository(): ProviderAlertRepository = ProviderAlertRepository()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolBar = binding.root.findViewById<View>(R.id.toolBar)
        toolBar.findViewById<ImageView>(R.id.toolBarBackBtn)
            .setOnClickListener { activity?.onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarBackTVBtn)
            .setOnClickListener { activity?.onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text =
            resources.getString(R.string.notifications)
        val profilePic = toolBar.findViewById<CircleImageView>(R.id.toolBarImage)
        loadProfileImage(profilePic)

        loadProviderAlertsScreen()

    }

    private fun loadProviderAlertsScreen() {

        if (!PermissionUtils.isNetworkConnected(requireContext())) {
            PermissionUtils.connectionAlert(requireContext()) { loadProviderAlertsScreen() }
            return
        }

        loadNotActionableAlerts()

        binding.apply {

            actionNeededBtn.setOnClickListener {
                loadActionableAlerts()
            }

            regularBtn.setOnClickListener {
                loadNotActionableAlerts()
            }

        }

    }

    private fun loadNotActionableAlerts() {
        binding.regularBtn.setBackgroundResource(R.drawable.provider_btn_bg)
        binding.regularBtn.setTextColor(Color.parseColor(requireActivity().resources.getString(R.string.white_color)))
        binding.actionNeededBtn.setTextColor(
            Color.parseColor(
                requireActivity().resources.getString(
                    R.string.black_color
                )
            )
        )
        binding.actionNeededBtn.setBackgroundResource(R.drawable.purple_out_line)
        viewModel.getNormalAlerts(requireContext()).observe(viewLifecycleOwner, {
            when (it) {
                is NetworkResponse.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is NetworkResponse.Success -> {
                    binding.alertsRV.adapter = UserAlertsAdapter(it.data!!, ACTIONABLE)
                    binding.regularBadge.text = it.data.size.toString()
                    binding.regularBadge.visibility = View.VISIBLE
                    binding.progressBar.visibility = View.GONE
                }
                is NetworkResponse.Failure -> {
                    binding.progressBar.visibility = View.GONE
                    binding.regularBadge.visibility = View.GONE
                    binding.alertsRV.adapter = UserAlertsAdapter(emptyList(), ACTIONABLE)
                    toast(requireContext(), it.message!!)
                }
            }
        })
    }

    private fun loadActionableAlerts() {
        binding.actionNeededBtn.setBackgroundResource(R.drawable.provider_btn_bg)
        binding.actionNeededBtn.setTextColor(
            Color.parseColor(
                requireActivity().resources.getString(
                    R.string.white_color
                )
            )
        )
        binding.regularBtn.setTextColor(Color.parseColor(requireActivity().resources.getString(R.string.black_color)))
        binding.regularBtn.setBackgroundResource(R.drawable.purple_out_line)
        viewModel.getActionableAlerts(requireContext()).observe(viewLifecycleOwner, {
            when (it) {
                is NetworkResponse.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is NetworkResponse.Success -> {
                    binding.alertsRV.adapter = UserAlertsAdapter(it.data!!, NOT_ACTIONABLE)
                    binding.actionNeededBadge.text = it.data.size.toString()
                    binding.actionNeededBadge.visibility = View.VISIBLE
                    binding.progressBar.visibility = View.GONE
                }
                is NetworkResponse.Failure -> {
                    binding.progressBar.visibility = View.GONE
                    binding.actionNeededBadge.visibility = View.GONE
                    binding.alertsRV.adapter = UserAlertsAdapter(emptyList(), NOT_ACTIONABLE)
                    toast(requireContext(), it.message!!)
                }
            }
        })
    }

}