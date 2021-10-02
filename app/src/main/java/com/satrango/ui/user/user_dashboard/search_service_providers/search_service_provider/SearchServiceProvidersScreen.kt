package com.satrango.ui.user.user_dashboard.search_service_providers.search_service_provider

import android.R
import android.annotation.SuppressLint
import android.app.ProgressDialog
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
    private lateinit var progressDialog: ProgressDialog

    companion object {
        var userLocationText = ""
        var subCategoryId = ""
        var keyword = ""
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchServiceProvidersScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressDialog = ProgressDialog(this)
        progressDialog.setCancelable(false)
        progressDialog.setMessage("Loading...")

        binding.userLocation.text = UserUtils.getCity(this)

        binding.toolBarBackBtn.setOnClickListener {
            finish()
            startActivity(Intent(this, UserDashboardScreen::class.java))
        }
        binding.toolBarBackTVBtn.setOnClickListener {
            finish()
            startActivity(Intent(this, UserDashboardScreen::class.java))
        }

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

        if (UserUtils.getSearchFilter(this).isNotEmpty() && UserUtils.getSelectedSPDetails(this).isNotEmpty()) {
            val spDetails = Gson().fromJson(UserUtils.getSelectedSPDetails(this), SearchServiceProviderResModel::class.java)
            val filter = Gson().fromJson(UserUtils.getSearchFilter(this), SearchFilterModel::class.java)
            val list = ArrayList<com.satrango.ui.user.user_dashboard.search_service_providers.models.Data>()
            for (sp in spDetails.data) {
                if (filter.priceRangeFrom.toDouble() <= sp.per_hour.toDouble() && filter.priceRangeTo.toDouble() >= sp.per_hour.toDouble()) {
                    if (filter.distance.toDouble() >= sp.distance_miles.toDouble()) {
                        if (filter.experience) {
                            if(sp.exp != "0-1 Year") {
                                list.add(sp)
                            }
                        } else if (filter.fresher) {
                            if(sp.exp == "0-1 Year") {
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

            if (filter.lowToHigh) {
                binding.listCount.text = "Showing ${spDetails.data.size} out of ${spDetails.data.size} results"
                binding.recyclerView.layoutManager = LinearLayoutManager(this)
                binding.recyclerView.adapter = SearchServiceProviderAdapter(list.sortedBy { data: com.satrango.ui.user.user_dashboard.search_service_providers.models.Data -> data.per_hour })
            } else if (filter.highToLow) {
                binding.listCount.text = "Showing ${spDetails.data.size} out of ${spDetails.data.size} results"
                binding.recyclerView.layoutManager = LinearLayoutManager(this)
                binding.recyclerView.adapter = SearchServiceProviderAdapter(list.sortedByDescending { data: com.satrango.ui.user.user_dashboard.search_service_providers.models.Data -> data.per_hour })
            } else {
                binding.listCount.text = "Showing ${spDetails.data.size} out of ${spDetails.data.size} results"
                binding.recyclerView.layoutManager = LinearLayoutManager(this)
                binding.recyclerView.adapter = SearchServiceProviderAdapter(list)
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
                    val adapter = ArrayAdapter(this, R.layout.simple_spinner_dropdown_item, keywords)
                    binding.searchBar.setAdapter(adapter)
                    binding.searchBar.setOnItemClickListener { _, _, position, _ ->
                        keyword = keywordsList[position].keywords_id
                        subCategoryId = keywordsList[position].subcategory_id
                        UserUtils.saveSelectedKeywordCategoryId(this, keywordsList[position].category_id)
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
                showBookingTypeDialog()
            } else {
                snackBar(binding.goBtn, "Please enter keyword to Search Service Providers")
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun loadSearchResults(keywordId: String, subCategory: String, type: String) {
        val requestBody = SearchServiceProviderReqModel(UserUtils.getAddress(this), UserUtils.getCity(this), UserUtils.getCountry(this), RetrofitBuilder.USER_KEY, keywordId.toInt(), UserUtils.getPostalCode(this), UserUtils.getState(this), UserUtils.getLatitude(this), UserUtils.getLongitude(this), UserUtils.getUserId(this).toInt(), subCategory.toInt())
//        val requestBody = SearchServiceProviderReqModel(
//            "Near Mandovi Showroom",
//            "Chilakaluripet",
//            "India",
//            RetrofitBuilder.USER_KEY,
//            keywordId.toInt(),
//            "575014",
//            "Karnataka",
//            "16.0948",
//            "80.1656",
//            UserUtils.getUserId(this).toInt(),
//            subCategory.toInt()
//        )

        viewModel.getSearchResults(this, requestBody).observe(this, {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    UserUtils.saveSelectedSPDetails(this, Gson().toJson(it.data!!))
                    if (type == "ViewResults") {
                        UserUtils.saveFromInstantBooking(this, false)
                        binding.listCount.text = "Showing ${it.data.data.size} out of ${it.data.data.size} results"
                        binding.recyclerView.adapter = SearchServiceProviderAdapter(it.data.data)
                        if (it.data.data.isNotEmpty()) {
                            binding.sortFilterBtn.visibility = View.VISIBLE
                        } else {
                            binding.sortFilterBtn.visibility = View.GONE
                        }
                    } else {
                        UserUtils.saveFromInstantBooking(this, true)
                        binding.listCount.visibility = View.GONE
                        binding.recyclerView.visibility = View.GONE
                        startActivity(Intent(this, BookingAttachmentsScreen::class.java))
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

    private fun showBookingTypeDialog() {
        val dialog = BottomSheetDialog(this)
        val dialogView = layoutInflater.inflate(com.satrango.R.layout.search_type_dialog, null)
        val viewResults = dialogView.findViewById<TextView>(com.satrango.R.id.viewResults)
        val bookInstantly = dialogView.findViewById<TextView>(com.satrango.R.id.bookInstantly)
        val closeBtn = dialogView.findViewById<MaterialCardView>(com.satrango.R.id.closeBtn)
        viewResults.setOnClickListener {
            dialog.dismiss()
            loadSearchResults(keyword, subCategoryId, "ViewResults")
        }
        bookInstantly.setOnClickListener {
            dialog.dismiss()
            loadSearchResults(keyword, subCategoryId, "BookInstantly")
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
        val dialogView = layoutInflater.inflate(com.satrango.R.layout.waiting_for_sp_confirmation_dialog, null)
        val progressBar = dialogView.findViewById<CircularProgressIndicator>(com.satrango.R.id.progressBar)
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
        val dialogView = layoutInflater.inflate(com.satrango.R.layout.no_service_provider_found, null)
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
            startActivity(Intent(this, UserDashboardScreen::class.java))
        }
        noBtn.setOnClickListener {
            dialog.dismiss()
            showWaitingForSPConfirmationDialog()
        }
        dialog.setContentView(dialogView)
        dialog.show()
    }

}