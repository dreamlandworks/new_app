package com.satrango.ui.service_provider.provider_dashboard.offers

import android.app.ProgressDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.satrango.R
import com.satrango.base.BaseFragment
import com.satrango.databinding.FragmentProviderOffersScreenBinding
import com.satrango.remote.NetworkResponse
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.user.user_dashboard.user_offers.UserOffersAdapter
import com.satrango.ui.user.user_dashboard.user_offers.models.OffersListReqModel
import com.satrango.utils.PermissionUtils
import com.satrango.utils.UserUtils
import com.satrango.utils.loadProfileImage
import com.satrango.utils.snackBar
import de.hdodenhof.circleimageview.CircleImageView

class ProviderOffersScreen : BaseFragment<ProviderOfferViewModel, FragmentProviderOffersScreenBinding, ProviderOfferRepository>() {

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

        loadProviderOffersScreen()
    }

    private fun loadProviderOffersScreen() {

        if (!PermissionUtils.isNetworkConnected(requireContext())) {
            PermissionUtils.connectionAlert(requireContext()) { loadProviderOffersScreen() }
            return
        }

        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Loading...")
        progressDialog.setCancelable(false)

        val requestBody = OffersListReqModel(
            UserUtils.getCity(requireContext()),
            UserUtils.getCountry(requireContext()),
            RetrofitBuilder.USER_KEY,
            3,
            UserUtils.getPostalCode(requireContext()),
            UserUtils.getState(requireContext()),
            UserUtils.getUserId(requireContext()).toInt()
        )

        viewModel.getOffers(requireContext(), requestBody).observe(requireActivity(), {
            when(it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    binding.recyclerView.adapter = UserOffersAdapter(it.data!!)
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.root, it.message!!)
                }
            }
        })

    }

}