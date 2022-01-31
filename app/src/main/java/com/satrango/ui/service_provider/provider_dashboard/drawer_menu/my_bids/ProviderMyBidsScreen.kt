package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bids

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.basusingh.beautifulprogressdialog.BeautifulProgressDialog
import com.google.gson.Gson
import com.satrango.R
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivityProviderMyBidsScreenBinding
import com.satrango.remote.NetworkResponse
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.service_provider.provider_dashboard.dashboard.ProviderDashboard
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bids.models.JobPostDetail
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bids.models.ProviderMyBidsResModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.models.ProviderBookingReqModel
import com.satrango.ui.user.user_dashboard.search_service_providers.SortAndFilterServiceProvider
import com.satrango.ui.user.user_dashboard.search_service_providers.models.SearchFilterModel
import com.satrango.utils.UserUtils
import com.satrango.utils.loadProfileImage
import de.hdodenhof.circleimageview.CircleImageView

class ProviderMyBidsScreen : AppCompatActivity() {

    private lateinit var viewModel: ProviderMyBidsViewModel
    private lateinit var binding: ActivityProviderMyBidsScreenBinding
    private lateinit var progressDialog: BeautifulProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProviderMyBidsScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.setStatusBarColor(resources.getColor(R.color.purple_700))
        }

        initializeProgressDialog()

        val toolBar = binding.root.findViewById<View>(R.id.toolBar)
        toolBar.findViewById<ImageView>(R.id.toolBarBackBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarBackTVBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text = resources.getString(R.string.my_bids)
        val profilePic = toolBar.findViewById<CircleImageView>(R.id.toolBarImage)
        loadProfileImage(profilePic)

        if (UserUtils.getSearchFilter(this).isNotEmpty() && UserUtils.getSelectedAllSPDetails(this).isNotEmpty()) {
            val newJobsList = Gson().fromJson(UserUtils.getSelectedAllSPDetails(this), ProviderMyBidsResModel::class.java)
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
                binding.note.visibility = View.GONE
                binding.recyclerView.adapter = ProviderMyBidsAdapter(list.sortedBy { data: JobPostDetail -> data.amount })
            } else if (filter.highToLow) {
                binding.note.visibility = View.GONE
                binding.recyclerView.adapter = ProviderMyBidsAdapter(list.sortedByDescending { data: JobPostDetail -> data.amount })
            } else {
                binding.note.visibility = View.GONE
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
                    binding.note.visibility = View.GONE
                    if (it.data.isNotEmpty()) {
                        binding.sortFilterBtn.visibility = View.VISIBLE
                    } else {
                        binding.sortFilterBtn.visibility = View.GONE
                    }
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    binding.note.visibility = View.VISIBLE
//                    snackBar(binding.recyclerView, it.message!!)
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
                    binding.note.visibility = View.GONE
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    binding.note.visibility = View.VISIBLE
//                    snackBar(binding.recyclerView, it.message!!)
                }
            }
        })
    }

    override fun onBackPressed() {
        UserUtils.saveFromFCMService(this, false)
//        ProviderDashboard.FROM_FCM_SERVICE = false
        startActivity(Intent(this, ProviderDashboard::class.java))
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initializeProgressDialog() {
        progressDialog = BeautifulProgressDialog(this, BeautifulProgressDialog.withGIF, resources.getString(R.string.loading))
        progressDialog.setGifLocation(Uri.parse("android.resource://${packageName}/${R.drawable.purple_loading}"))
        progressDialog.setLayoutColor(resources.getColor(R.color.progressDialogColor))
    }
}