package com.satrango.ui.service_provider.provider_dashboard.provider_dashboard.my_bids

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.satrango.R
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivityProviderMyBidsScreenBinding
import com.satrango.remote.NetworkResponse
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.service_provider.provider_dashboard.provider_dashboard.ProviderDashboard
import com.satrango.ui.service_provider.provider_dashboard.provider_dashboard.my_bids.models.JobPostDetail
import com.satrango.ui.service_provider.provider_dashboard.provider_dashboard.my_bids.models.ProviderMyBidsResModel
import com.satrango.ui.service_provider.provider_dashboard.provider_dashboard.my_bookings.models.ProviderBookingReqModel
import com.satrango.ui.user.user_dashboard.search_service_providers.SortAndFilterServiceProvider
import com.satrango.ui.user.user_dashboard.search_service_providers.models.Data
import com.satrango.ui.user.user_dashboard.search_service_providers.models.SearchFilterModel
import com.satrango.ui.user.user_dashboard.search_service_providers.models.SearchServiceProviderResModel
import com.satrango.ui.user.user_dashboard.search_service_providers.search_service_provider.SearchServiceProviderAdapter
import com.satrango.utils.UserUtils
import com.satrango.utils.loadProfileImage
import com.satrango.utils.snackBar
import de.hdodenhof.circleimageview.CircleImageView

class ProviderMyBidsScreen : AppCompatActivity() {

    private lateinit var viewModel: ProviderMyBidsViewModel
    private lateinit var binding: ActivityProviderMyBidsScreenBinding
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProviderMyBidsScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Loading...")
        progressDialog.setCancelable(false)

        val toolBar = binding.root.findViewById<View>(R.id.toolBar)
        toolBar.findViewById<ImageView>(R.id.toolBarBackBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarBackTVBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text = resources.getString(R.string.my_bids)
        val profilePic = toolBar.findViewById<CircleImageView>(R.id.toolBarImage)
        loadProfileImage(profilePic)

        if (UserUtils.getSearchFilter(this).isNotEmpty() && UserUtils.getSelectedSPDetails(this).isNotEmpty()) {
            val newJobsList = Gson().fromJson(UserUtils.getSelectedSPDetails(this), ProviderMyBidsResModel::class.java)
            val filter = Gson().fromJson(UserUtils.getSearchFilter(this), SearchFilterModel::class.java)
            val list = ArrayList<JobPostDetail>()
            for (sp in newJobsList.job_post_details) {
                if (filter.priceRangeFrom.toDouble() <= sp.amount.toDouble() && filter.priceRangeTo.toDouble() >= sp.amount.toDouble()) {
                    if (filter.distance.toDouble() >= sp.distance_miles.toDouble()) {
//                        if (filter.experience) {
//                            if(sp.exp != "0-1 Year") {
//                                list.add(sp)
//                            }
//                        } else if (filter.fresher) {
//                            if(sp.exp == "0-1 Year") {
//                                list.add(sp)
//                            }
//                        } else if (filter.any) {
//                            list.add(sp)
//                        } else {
//
//                        }
                        list.add(sp)
                    }
                }
            }

            if (filter.lowToHigh) {
                binding.recyclerView.adapter = ProviderMyBidsAdapter(list.sortedBy { data: JobPostDetail -> data.amount })
            } else if (filter.highToLow) {
                binding.recyclerView.adapter = ProviderMyBidsAdapter(list.sortedByDescending { data: JobPostDetail -> data.amount })
            } else {
                binding.recyclerView.adapter = ProviderMyBidsAdapter(list)
            }
        }

        val factory = ViewModelFactory(ProviderMyBidsRepository())
        viewModel = ViewModelProvider(this, factory)[ProviderMyBidsViewModel::class.java]

        updateUIWithNewJobs()
        binding.newJobsBtn.setOnClickListener {
            binding.newJobsBtn.setBackgroundResource(R.drawable.provider_btn_bg)
            binding.awardedBtn.setBackgroundResource(0)
            binding.bidsPlacedBtn.setBackgroundResource(0)
            binding.newJobsBtn.setTextColor(Color.parseColor("#FFFFFF"))
            binding.awardedBtn.setTextColor(Color.parseColor("#000000"))
            binding.bidsPlacedBtn.setTextColor(Color.parseColor("#000000"))
            updateUIWithNewJobs()
        }
        binding.bidsPlacedBtn.setOnClickListener {
            binding.bidsPlacedBtn.setBackgroundResource(R.drawable.provider_btn_bg)
            binding.awardedBtn.setBackgroundResource(0)
            binding.newJobsBtn.setBackgroundResource(0)
            binding.newJobsBtn.setTextColor(Color.parseColor("#000000"))
            binding.awardedBtn.setTextColor(Color.parseColor("#000000"))
            binding.bidsPlacedBtn.setTextColor(Color.parseColor("#FFFFFF"))
            updateUI("Open")
        }
        binding.awardedBtn.setOnClickListener {
            binding.awardedBtn.setBackgroundResource(R.drawable.provider_btn_bg)
            binding.newJobsBtn.setBackgroundResource(0)
            binding.bidsPlacedBtn.setBackgroundResource(0)
            binding.newJobsBtn.setTextColor(Color.parseColor("#000000"))
            binding.awardedBtn.setTextColor(Color.parseColor("#FFFFFF"))
            binding.bidsPlacedBtn.setTextColor(Color.parseColor("#000000"))
            updateUI("Awarded")
        }
        binding.sortFilterBtn.setOnClickListener {
            UserUtils.saveSearchFilter(this, "")
            SortAndFilterServiceProvider.FROM_PROVIDER = true
            startActivity(Intent(this, SortAndFilterServiceProvider::class.java))
        }

    }

    private fun updateUIWithNewJobs() {
        val requestBody = ProviderBookingReqModel(RetrofitBuilder.PROVIDER_KEY, UserUtils.getUserId(this).toInt())
        viewModel.jobsList(this, requestBody).observe(this, {
            when(it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    val data = it.data!!
                    binding.recyclerView.adapter = ProviderMyBidsAdapter(data)
                    if (it.data.isNotEmpty()) {
                        binding.sortFilterBtn.visibility = View.VISIBLE
                    } else {
                        binding.sortFilterBtn.visibility = View.GONE
                    }
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.recyclerView, it.message!!)
                }
            }
        })
    }

    private fun updateUI(status: String) {
//        val requestBody = ProviderBookingReqModel(RetrofitBuilder.PROVIDER_KEY, UserUtils.getUserId(this).toInt())
        val requestBody = ProviderBookingReqModel(RetrofitBuilder.PROVIDER_KEY, 2)
        viewModel.bidsList(this, requestBody).observe(this, {
            when(it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    val list = it.data!!
                    val filterList = ArrayList<JobPostDetail>()
                    if (status == "Open") {
                        for (bid in list) {
                            if (bid.bid_type == status) {
                                filterList.add(bid)
                            }
                        }
                    } else {
                        for (bid in list) {
                            if (bid.bid_type != "Open") {
                                filterList.add(bid)
                            }
                        }
                    }
                    binding.recyclerView.adapter = ProviderMyBidsAdapter(filterList)
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.recyclerView, it.message!!)
                }
            }
        })
    }

    override fun onBackPressed() {
        startActivity(Intent(this, ProviderDashboard::class.java))
    }
}