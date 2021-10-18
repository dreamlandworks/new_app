package com.satrango.ui.user.user_dashboard.search_service_providers.search_service_provider

import android.R
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.basusingh.beautifulprogressdialog.BeautifulProgressDialog
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.card.MaterialCardView
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.gson.Gson
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivitySearchServiceProvidersScreenBinding
import com.satrango.remote.NetworkResponse
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.user.bookings.booking_attachments.BookingAttachmentsScreen
import com.satrango.ui.user.user_dashboard.UserDashboardScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.PostJobTypeScreen
import com.satrango.ui.user.user_dashboard.search_service_providers.SortAndFilterServiceProvider
import com.satrango.ui.user.user_dashboard.search_service_providers.models.SearchFilterModel
import com.satrango.ui.user.user_dashboard.search_service_providers.models.SearchServiceProviderReqModel
import com.satrango.ui.user.user_dashboard.search_service_providers.models.SearchServiceProviderResModel
import com.satrango.ui.user.user_dashboard.user_home_screen.models.Data
import com.satrango.ui.user.user_dashboard.user_home_screen.user_location_change.UserLocationSelectionScreen
import com.satrango.utils.UserUtils
import com.satrango.utils.snackBar

class SearchServiceProvidersScreen : AppCompatActivity() {

    private lateinit var binding: ActivitySearchServiceProvidersScreenBinding
    private lateinit var viewModel: SearchServiceProviderViewModel
    private lateinit var progressDialog: BeautifulProgressDialog

    companion object {
        var FROM_POPULAR_SERVICES = false
        var userLocationText = ""
        var subCategoryId = ""
        var keyword = ""
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initializeProgressDialog() {
        progressDialog = BeautifulProgressDialog(
            this, BeautifulProgressDialog.withImage, resources.getString(
                com.satrango.R.string.loading
            )
        )
        progressDialog.setImageLocation(resources.getDrawable(com.satrango.R.drawable.circlelogo))
        progressDialog.setLayoutColor(resources.getColor(com.satrango.R.color.white))
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchServiceProvidersScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeToolBar()
        initializeProgressDialog()

        binding.sortFilterBtn.setOnClickListener {
            UserUtils.saveSearchFilter(this, "")
            SortAndFilterServiceProvider.FROM_PROVIDER = false
            startActivity(Intent(this, SortAndFilterServiceProvider::class.java))
        }

        binding.userLocation.setOnClickListener {
            startActivity(Intent(this, UserLocationSelectionScreen::class.java))
        }

        val factory = ViewModelFactory(SearchServiceProviderRepository())
        viewModel = ViewModelProvider(this, factory)[SearchServiceProviderViewModel::class.java]

        if (UserUtils.getSearchFilter(this).isNotEmpty() && UserUtils.getSelectedSPDetails(this)
                .isNotEmpty()
        ) {
            val spDetails = Gson().fromJson(
                UserUtils.getSelectedSPDetails(this),
                SearchServiceProviderResModel::class.java
            )
            val filter =
                Gson().fromJson(UserUtils.getSearchFilter(this), SearchFilterModel::class.java)
            val list =
                ArrayList<com.satrango.ui.user.user_dashboard.search_service_providers.models.Data>()
            for (sp in spDetails.data) {
                if (filter.priceRangeFrom.toDouble() <= sp.per_hour.toDouble() && filter.priceRangeTo.toDouble() >= sp.per_hour.toDouble()) {
                    if (filter.distance.toDouble() >= sp.distance_miles.toDouble()) {
                        if (filter.experience) {
                            if (sp.exp != "0-1 Year") {
                                list.add(sp)
                            }
                        } else if (filter.fresher) {
                            if (sp.exp == "0-1 Year") {
                                list.add(sp)
                            }
                        } else if (filter.any) {
                            list.add(sp)
                        } else {
                            list.add(sp)
                        }
                    }
                }
            }

            when {
                filter.lowToHigh -> {
                    binding.listCount.visibility = View.VISIBLE
                    binding.listCount.text = "${list.size} out of ${spDetails.data.size}"
                    binding.recyclerView.layoutManager = LinearLayoutManager(this)
                    binding.recyclerView.adapter =
                        SearchServiceProviderAdapter(list.sortedBy { data: com.satrango.ui.user.user_dashboard.search_service_providers.models.Data -> data.per_hour })
                }
                filter.highToLow -> {
                    binding.listCount.visibility = View.VISIBLE
                    binding.listCount.text = "${list.size} out of ${spDetails.data.size}"
                    binding.recyclerView.layoutManager = LinearLayoutManager(this)
                    binding.recyclerView.adapter =
                        SearchServiceProviderAdapter(list.sortedByDescending { data: com.satrango.ui.user.user_dashboard.search_service_providers.models.Data -> data.per_hour })
                }
                else -> {
                    binding.listCount.visibility = View.VISIBLE
                    binding.listCount.text = "${list.size} out of ${spDetails.data.size}"
                    binding.recyclerView.layoutManager = LinearLayoutManager(this)
                    binding.recyclerView.adapter = SearchServiceProviderAdapter(list)
                }
            }

        }

        viewModel.getKeywordsList(this).observe(this, {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    val keywordsList = it.data as ArrayList<Data>
                    val keywords = arrayListOf<String>()

                    keywordsList.forEach { keyword -> keywords.add(keyword.phrase) }

                    binding.searchBar.threshold = 3
                    val adapter =
                        ArrayAdapter(this, R.layout.simple_spinner_dropdown_item, keywords)
                    binding.searchBar.setAdapter(adapter)
                    binding.searchBar.setOnItemClickListener { _, _, position, _ ->
                        keyword = keywordsList[position].keywords_id
                        subCategoryId = keywordsList[position].subcategory_id
                        UserUtils.saveSelectedKeywordCategoryId(
                            this,
                            keywordsList[position].category_id
                        )
                    }
                    progressDialog.dismiss()
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.recyclerView, it.message!!)
                }
            }

        })

        binding.goBtn.setOnClickListener {
            if (binding.searchBar.text.toString().isNotEmpty()) {
                UserUtils.saveSearchFilter(this, "")
                loadSearchResults(keyword, subCategoryId)
            } else {
                snackBar(binding.goBtn, "Please enter keyword to Search Service Providers")
            }
        }

        if (FROM_POPULAR_SERVICES) {
            loadSearchResults(keyword, subCategoryId)
        }
    }

    private fun initializeToolBar() {
        binding.userLocation.text = UserUtils.getCity(this)
        binding.toolBarBackBtn.setOnClickListener {
            finish()
            startActivity(Intent(this, UserDashboardScreen::class.java))
        }
        binding.toolBarBackTVBtn.setOnClickListener {
            finish()
            startActivity(Intent(this, UserDashboardScreen::class.java))
        }
    }

    @SuppressLint("SetTextI18n")
    private fun loadSearchResults(keywordId: String, subCategory: String) {
        val requestBody = SearchServiceProviderReqModel(
            UserUtils.getAddress(this),
            UserUtils.getCity(this),
            UserUtils.getCountry(this),
            RetrofitBuilder.USER_KEY,
            keywordId.toInt(),
            UserUtils.getPostalCode(this),
            UserUtils.getState(this),
            UserUtils.getLatitude(this),
            UserUtils.getLongitude(this),
            UserUtils.getUserId(this).toInt(),
            subCategory.toInt()
        )

//        val requestBody = SearchServiceProviderReqModel(
//            "Indira Mahal",
//            "Hubli",
//            "India",
//            RetrofitBuilder.USER_KEY,
//            2,
//            "575001",
//            "Karnataka",
//            "12.2359",
//            "14.0496",
//            44,
//            2
//        )
//        toast(this, Gson().toJson(requestBody))
        viewModel.getSearchResults(this, requestBody).observe(this, {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    UserUtils.saveSelectedSPDetails(this, Gson().toJson(it.data!!))
                    if (it.data.data.isEmpty()) {
                        weAreSorryDialog()
                    } else {
                        showBookingTypeDialog(it.data)
                    }
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.recyclerView, it.message!!)
                }
            }
        })
    }

    override fun onBackPressed() {
        finish()
        startActivity(Intent(this, UserDashboardScreen::class.java))
    }

    @SuppressLint("SetTextI18n")
    private fun showBookingTypeDialog(data: SearchServiceProviderResModel) {
        val dialog = BottomSheetDialog(this)
        val dialogView = layoutInflater.inflate(com.satrango.R.layout.search_type_dialog, null)
        val viewResults = dialogView.findViewById<TextView>(com.satrango.R.id.viewResults)
        val bookInstantly = dialogView.findViewById<TextView>(com.satrango.R.id.bookInstantly)
        val providerCount = dialogView.findViewById<TextView>(com.satrango.R.id.providerCount)
        val jobLocation = dialogView.findViewById<TextView>(com.satrango.R.id.jobLocation)
        val closeBtn = dialogView.findViewById<MaterialCardView>(com.satrango.R.id.closeBtn)
        providerCount.text = data.data.size.toString()
        jobLocation.text = UserUtils.getCity(this)
        if (FROM_POPULAR_SERVICES) {
            keyword = "0"
        }
        val data = Gson().fromJson(
            UserUtils.getSelectedSPDetails(this),
            SearchServiceProviderResModel::class.java
        )
//        toast(this, JSONObject(Gson().toJson(data).toString()).toString())
        viewResults.setOnClickListener {
            dialog.dismiss()
            UserUtils.saveFromInstantBooking(this, false)
            binding.listCount.text = "${data.data.size} out of ${data.data.size}"
            binding.recyclerView.adapter = SearchServiceProviderAdapter(data.data)
            if (data.data.isNotEmpty()) {
                binding.sortFilterBtn.visibility = View.VISIBLE
            } else {
                binding.sortFilterBtn.visibility = View.GONE
            }
        }
        bookInstantly.setOnClickListener {
            dialog.dismiss()
            binding.listCount.text = "${data.data.size} out of ${data.data.size}"
            UserUtils.saveFromInstantBooking(this, true)
            binding.listCount.visibility = View.GONE
            binding.recyclerView.visibility = View.GONE
            startActivity(Intent(this, BookingAttachmentsScreen::class.java))
        }
        closeBtn.setOnClickListener {
            dialog.dismiss()
        }
        dialog.setContentView(dialogView)
        dialog.setCancelable(false)
        dialog.show()
    }

    @SuppressLint("SetTextI18n")
    private fun showWaitingForSPConfirmationDialog() {
        val dialog = BottomSheetDialog(this)
        dialog.setCancelable(false)
        val dialogView =
            layoutInflater.inflate(com.satrango.R.layout.waiting_for_sp_confirmation_dialog, null)
        val progressBar =
            dialogView.findViewById<CircularProgressIndicator>(com.satrango.R.id.progressBar)
        val time = dialogView.findViewById<TextView>(com.satrango.R.id.time)
        val closeBtn = dialogView.findViewById<MaterialCardView>(com.satrango.R.id.closeBtn)
        closeBtn.setOnClickListener {
            dialog.dismiss()
        }
        var minutes = 2
        var seconds = 59
        val mainHandler = Handler(Looper.getMainLooper())
        var progressTime = 180
        mainHandler.post(object : Runnable {
            override fun run() {
                time.text = "$minutes:$seconds"
                progressTime -= 1
                progressBar.progress = progressTime

                seconds -= 1
                if (minutes == 0 && seconds == 0) {
                    dialog.dismiss()
                    weAreSorryDialog()
                }
                if (seconds == 0) {
                    seconds = 59
                    minutes -= 1
                }
                mainHandler.postDelayed(this, 1000)
            }
        })
        dialog.setContentView(dialogView)
        dialog.show()
    }

    private fun weAreSorryDialog() {
        val dialog = BottomSheetDialog(this)
        dialog.setCancelable(false)
        val dialogView =
            layoutInflater.inflate(com.satrango.R.layout.no_service_provider_found, null)
        val yesBtn = dialogView.findViewById<TextView>(com.satrango.R.id.yesBtn)
        val noBtn = dialogView.findViewById<TextView>(com.satrango.R.id.noBtn)
        val closeBtn = dialogView.findViewById<MaterialCardView>(com.satrango.R.id.closeBtn)
        closeBtn.setOnClickListener {
            dialog.dismiss()
        }
        yesBtn.setOnClickListener {
            snackBar(yesBtn, "Post the Job")
            dialog.dismiss()
            finish()
            startActivity(Intent(this, PostJobTypeScreen::class.java))
        }
        noBtn.setOnClickListener {
            dialog.dismiss()
            showWaitingForSPConfirmationDialog()
        }
        dialog.setContentView(dialogView)
        dialog.show()
    }

}