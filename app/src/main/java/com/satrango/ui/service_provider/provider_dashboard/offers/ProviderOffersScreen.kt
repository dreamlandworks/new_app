package com.satrango.ui.service_provider.provider_dashboard.offers

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.basusingh.beautifulprogressdialog.BeautifulProgressDialog
import com.satrango.R
import com.satrango.base.BaseFragment
import com.satrango.databinding.FragmentProviderOffersScreenBinding
import com.satrango.remote.NetworkResponse
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.user.user_dashboard.user_offers.*
import com.satrango.ui.user.user_dashboard.user_offers.models.OffersListReqModel
import com.satrango.utils.PermissionUtils
import com.satrango.utils.UserUtils
import com.satrango.utils.loadProfileImage
import com.satrango.utils.snackBar
import de.hdodenhof.circleimageview.CircleImageView

class ProviderOffersScreen : BaseFragment<ProviderOfferViewModel, FragmentProviderOffersScreenBinding, ProviderOfferRepository>() {

    private lateinit var progressDialog: BeautifulProgressDialog

    companion object {
        var FROM_PROVIDER = false
    }
    override fun getFragmentViewModel(): Class<ProviderOfferViewModel> = ProviderOfferViewModel::class.java

    override fun getFragmentBinding(
        layoutInflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentProviderOffersScreenBinding = FragmentProviderOffersScreenBinding.inflate(layoutInflater, container, false)

    override fun getFragmentRepository(): ProviderOfferRepository = ProviderOfferRepository()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolBar = binding.root.findViewById<View>(R.id.toolBar)
        toolBar.findViewById<ImageView>(R.id.toolBarBackBtn).setOnClickListener { activity?.onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarBackTVBtn).setOnClickListener { activity?.onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text = resources.getString(R.string.offers)
        val profilePic = toolBar.findViewById<CircleImageView>(R.id.toolBarImage)
        loadProfileImage(profilePic)

        initializeProgressDialog()

        loadProviderOffersScreen()
    }

    private fun loadProviderOffersScreen() {

        if (!PermissionUtils.isNetworkConnected(requireContext())) {
            PermissionUtils.connectionAlert(requireContext()) { loadProviderOffersScreen() }
            return
        }
        FROM_PROVIDER = true

        val requestBody = OffersListReqModel(
            "",
            "",
            RetrofitBuilder.USER_KEY,
            3,
            "",
            "",
            UserUtils.getUserId(requireContext()).toInt(),
            "latest"
        )
        viewModel.getOffers(requireContext(), requestBody).observe(requireActivity(), {
            when(it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    binding.latestOfferRv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                    binding.latestOfferRv.adapter = UserLatestOffersAdapter(it.data!!)
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.root, it.message!!)
                }
            }
        })

        val expiryRequestBody = OffersListReqModel(
            "",
            "",
            RetrofitBuilder.USER_KEY,
            3,
            "",
            "",
            UserUtils.getUserId(requireContext()).toInt(),
            "expiry"
        )
        viewModel.getOffers(requireContext(), expiryRequestBody).observe(requireActivity(), {
            when(it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    binding.expiryOfferRv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                    binding.expiryOfferRv.adapter = UserExpiryOffersAdapter(it.data!!)
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.root, it.message!!)
                }
            }
        })

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
        viewModel.getOffers(requireContext(), referralRequestBody).observe(requireActivity(), {
            when(it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    binding.referralOfferRv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                    binding.referralOfferRv.adapter = UserReferralOffersAdapter(it.data!!)
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.root, it.message!!)
                }
            }
        })


    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initializeProgressDialog() {
        progressDialog = BeautifulProgressDialog(requireActivity(), BeautifulProgressDialog.withGIF, resources.getString(R.string.loading))
        progressDialog.setGifLocation(Uri.parse("android.resource://${activity?.packageName}/${R.drawable.blue_loading}"))
        progressDialog.setLayoutColor(resources.getColor(R.color.progressDialogColor))
    }

}