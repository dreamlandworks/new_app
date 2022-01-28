package com.satrango.ui.service_provider.provider_dashboard.home

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProvider
import com.basusingh.beautifulprogressdialog.BeautifulProgressDialog
import com.google.gson.Gson
import com.satrango.R
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.FragmentProviderHomeScreenBinding
import com.satrango.remote.NetworkResponse
import com.satrango.ui.service_provider.provider_dashboard.dashboard.leaderboard.LeaderBoardScreen
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.training.ProviderMyTrainingRepository
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.training.ProviderMyTrainingViewModel
import com.satrango.utils.UserUtils
import com.satrango.utils.snackBar
import com.satrango.utils.toast
import java.util.*

class ProviderHomeScreen : Fragment() {

    private lateinit var viewModel: ProviderMyTrainingViewModel
    private lateinit var progressDialog: BeautifulProgressDialog

    companion object {
        lateinit var binding: FragmentProviderHomeScreenBinding
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProviderHomeScreenBinding.inflate(inflater, container, false)

        initializeProgressDialog()

        binding.leaderBoard.setOnClickListener {
            startActivity(Intent(requireContext(), LeaderBoardScreen::class.java))
        }

        val factory = ViewModelFactory(ProviderMyTrainingRepository())
        viewModel = ViewModelProvider(this, factory)[ProviderMyTrainingViewModel::class.java]

        viewModel.getCitiesList(requireContext()).observe(requireActivity(), {
            when(it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    val cities = it.data!!.data
                    for (city in cities) {
                        if (UserUtils.getCity(requireContext())
                                .lowercase(Locale.getDefault()) == city.city.lowercase(Locale.getDefault())
                        ) {
                            loadProviderDashboardDetails(city.id)
                        }
                    }
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.bidCount, it.message!!)
                }
            }
        })


        return binding.root
    }

    @SuppressLint("SetTextI18n")
    private fun loadProviderDashboardDetails(cityId: String) {
        viewModel.providerDashboardDetails(requireContext(), cityId).observe(requireActivity(), {
            when(it) {
                is NetworkResponse.Loading -> {
//                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    val data = it.data!!
                    toast(requireContext(), Gson().toJson(data))
                    binding.bookingCount.text = "${data.bookings_completed}/${data.total_bookings}"
                    binding.bidCount.text = "${data.bids_awarded}/${data.total_bids}"
                    binding.earningText.text = data.earnings
                    binding.commissionCount.text = data.commission.toString()
                    binding.myRank.text = "#${data.sp_rank}"
                    binding.ratingCount.text = data.sp_rating.toString()
                    binding.pointsCount.text = data.sp_points.toString()
                    binding.cName.text = data.competitor_name
                    binding.cRank.text = data.competitor_rank.toString()
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.bidCount, it.message!!)
                }

            }
        })
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initializeProgressDialog() {
        progressDialog = BeautifulProgressDialog(requireActivity(), BeautifulProgressDialog.withGIF, resources.getString(
            R.string.loading))
        progressDialog.setGifLocation(Uri.parse("android.resource://${activity?.packageName}/${R.drawable.purple_loading}"))
        progressDialog.setLayoutColor(resources.getColor(R.color.progressDialogColor))
    }

}