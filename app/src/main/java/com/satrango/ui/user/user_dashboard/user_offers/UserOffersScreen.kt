package com.satrango.ui.user.user_dashboard.user_offers

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.basusingh.beautifulprogressdialog.BeautifulProgressDialog
import com.google.android.gms.location.*
import com.satrango.R
import com.satrango.base.BaseFragment
import com.satrango.databinding.FragmentUserOffersScreenBinding
import com.satrango.remote.NetworkResponse
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.service_provider.provider_dashboard.offers.ProviderOffersScreen
import com.satrango.ui.user.user_dashboard.user_alerts.UserAlertsRepository
import com.satrango.ui.user.user_dashboard.user_alerts.UserAlertsViewModel
import com.satrango.ui.user.user_dashboard.user_offers.models.OffersListReqModel
import com.satrango.utils.*
import com.satrango.utils.UserUtils.isProvider
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

class UserOffersScreen :
    BaseFragment<UserAlertsViewModel, FragmentUserOffersScreenBinding, UserAlertsRepository>() {

    private lateinit var progressDialog: BeautifulProgressDialog

    override fun getFragmentViewModel(): Class<UserAlertsViewModel> =
        UserAlertsViewModel::class.java

    override fun getFragmentBinding(
        layoutInflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentUserOffersScreenBinding =
        FragmentUserOffersScreenBinding.inflate(layoutInflater, container, false)

    override fun getFragmentRepository(): UserAlertsRepository = UserAlertsRepository()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeToolBar()
        initializeProgressDialog()

//        if (PermissionUtils.checkGPSStatus(requireActivity()) && networkAvailable(requireContext())) {
//            UserDashboardScreen.fetchLocation(requireContext())
//        }

//        val requestBody = OffersListReqModel(
//            UserUtils.getCity(requireContext()),
//            UserUtils.getCountry(requireContext()),
//            RetrofitBuilder.USER_KEY,
//            2,
//            UserUtils.getPostalCode(requireContext()),
//            UserUtils.getState(requireContext()),
//            UserUtils.getUserId(requireContext()).toInt(),
//            ""
//        )
        isProvider(requireContext(), false)

        binding.latestOfferRv.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.expiryOfferRv.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.referralOfferRv.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        val requestBody = OffersListReqModel(
            "",
            "",
            RetrofitBuilder.USER_KEY,
            2,
            "",
            "",
            UserUtils.getUserId(requireContext()).toInt(),
            "latest"
        )
        viewModel.getUserOffers(requireContext(), requestBody).observe(requireActivity()) {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    binding.latestOfferRv.adapter = UserLatestOffersAdapter(it.data!!)
//                    toast(requireContext(), it.data.size.toString())
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.root, it.message!!)
                }
            }
        }

        val expiryRequestBody = OffersListReqModel(
            "",
            "",
            RetrofitBuilder.USER_KEY,
            2,
            "",
            "",
            UserUtils.getUserId(requireContext()).toInt(),
            "expiry"
        )
        viewModel.getUserOffers(requireActivity(), expiryRequestBody).observe(requireActivity()) {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    binding.expiryOfferRv.adapter = UserExpiryOffersAdapter(it.data!!)
//                    toast(requireContext(), it.data.size.toString())
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.root, it.message!!)
                }
            }
        }

        val referralRequestBody = OffersListReqModel(
            "",
            "",
            RetrofitBuilder.USER_KEY,
            5,
            "",
            "",
            UserUtils.getUserId(requireContext()).toInt(),
            ""
        )
        viewModel.getUserOffers(requireContext(), referralRequestBody).observe(requireActivity()) {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    binding.referralOfferRv.adapter = UserReferralOffersAdapter(it.data!!)
//                    toast(requireContext(), it.data.size.toString())
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.root, it.message!!)
                }
            }
        }

    }

    private fun initializeToolBar() {
        val toolBar = binding.root.findViewById<View>(R.id.toolBar)
        toolBar.findViewById<ImageView>(R.id.toolBarBackBtn)
            .setOnClickListener { activity?.onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarBackTVBtn)
            .setOnClickListener { activity?.onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text =
            resources.getString(R.string.offers)
        val profilePic = toolBar.findViewById<CircleImageView>(R.id.toolBarImage)
        loadProfileImage(profilePic)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initializeProgressDialog() {
        progressDialog = BeautifulProgressDialog(
            requireActivity(),
            BeautifulProgressDialog.withGIF,
            resources.getString(R.string.loading)
        )
        progressDialog.setGifLocation(Uri.parse("android.resource://${activity?.packageName}/${R.drawable.blue_loading}"))
        progressDialog.setLayoutColor(resources.getColor(R.color.progressDialogColor))
    }

}