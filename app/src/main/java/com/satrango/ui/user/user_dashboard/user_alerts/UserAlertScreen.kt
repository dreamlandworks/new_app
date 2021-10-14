package com.satrango.ui.user.user_dashboard.user_alerts

import android.annotation.SuppressLint
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.basusingh.beautifulprogressdialog.BeautifulProgressDialog
import com.satrango.R
import com.satrango.base.BaseFragment
import com.satrango.databinding.FragmentUserAlertScreenBinding
import com.satrango.remote.NetworkResponse
import com.satrango.utils.PermissionUtils
import com.satrango.utils.loadProfileImage
import com.satrango.utils.toast
import de.hdodenhof.circleimageview.CircleImageView

class UserAlertScreen :
    BaseFragment<UserAlertsViewModel, FragmentUserAlertScreenBinding, UserAlertsRepository>() {

    private val ACTIONABLE: String = "1"
    private val NOT_ACTIONABLE: String = "2"
    private lateinit var progressDialog: BeautifulProgressDialog

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeToolBar()
        initializeProgressDialog()

        loadUserAlertsScreen()

    }

    private fun initializeToolBar() {
        val toolBar = binding.root.findViewById<View>(R.id.toolBar)
        toolBar.findViewById<ImageView>(R.id.toolBarBackBtn).setOnClickListener { activity?.onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarBackTVBtn).setOnClickListener { activity?.onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text = resources.getString(R.string.alerts)
        val profilePic = toolBar.findViewById<CircleImageView>(R.id.toolBarImage)
        loadProfileImage(profilePic)
    }

    private fun loadUserAlertsScreen() {

        if (!PermissionUtils.isNetworkConnected(requireContext())) {
            PermissionUtils.connectionAlert(requireContext()) { loadUserAlertsScreen() }
            return
        }

        loadNotActionableAlerts()

        binding.actionNeededBtn.setOnClickListener {
            loadActionableAlerts()
        }

        binding.regularBtn.setOnClickListener {
            loadNotActionableAlerts()
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initializeProgressDialog() {
        progressDialog = BeautifulProgressDialog(requireActivity(), BeautifulProgressDialog.withGIF, resources.getString(R.string.loading))
        progressDialog.setGifLocation(Uri.parse("android.resource://${activity?.packageName}/${R.drawable.blue_loading}"))
        progressDialog.setLayoutColor(resources.getColor(R.color.progressDialogColor))
    }

    private fun loadNotActionableAlerts() {
        binding.regularBtn.setBackgroundResource(R.drawable.category_bg)
        binding.regularBtn.setTextColor(Color.parseColor(requireActivity().resources.getString(R.string.white_color)))
        binding.actionNeededBtn.setTextColor(Color.parseColor(requireActivity().resources.getString(
                    R.string.black_color
                )))
        binding.actionNeededBtn.setBackgroundResource(R.drawable.blue_out_line)
        viewModel.getNormalAlerts(requireContext()).observe(viewLifecycleOwner, {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    if (it.data!!.isNotEmpty()) {
                        binding.alertsRV.adapter = UserAlertsAdapter(it.data, ACTIONABLE)
                        binding.regularBadge.text = it.data.size.toString()
                    } else {
                        binding.regularBadge.visibility = View.GONE
                    }
                    progressDialog.dismiss()
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    toast(requireContext(), it.message!!)
                }
            }
        })
    }

    private fun loadActionableAlerts() {
        binding.actionNeededBtn.setBackgroundResource(R.drawable.category_bg)
        binding.actionNeededBtn.setTextColor(Color.parseColor(requireActivity().resources.getString(
                    R.string.white_color
                )))
        binding.regularBtn.setTextColor(Color.parseColor(requireActivity().resources.getString(R.string.black_color)))
        binding.regularBtn.setBackgroundResource(R.drawable.blue_out_line)
        viewModel.getActionableAlerts(requireContext()).observe(viewLifecycleOwner, {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    if (it.data!!.isNotEmpty()) {
                        binding.alertsRV.adapter = UserAlertsAdapter(it.data, NOT_ACTIONABLE)
                        binding.actionNeededBadge.text = it.data.size.toString()
                    } else {
                        binding.actionNeededBadge.visibility = View.GONE
                    }
                    progressDialog.dismiss()
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    toast(requireContext(), it.message!!)
                }
            }
        })
    }


    override fun getFragmentViewModel(): Class<UserAlertsViewModel> =
        UserAlertsViewModel::class.java

    override fun getFragmentBinding(
        layoutInflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentUserAlertScreenBinding =
        FragmentUserAlertScreenBinding.inflate(layoutInflater, container, false)

    override fun getFragmentRepository(): UserAlertsRepository = UserAlertsRepository()

}