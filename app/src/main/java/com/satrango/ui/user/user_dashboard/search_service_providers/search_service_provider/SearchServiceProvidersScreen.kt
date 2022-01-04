package com.satrango.ui.user.user_dashboard.search_service_providers.search_service_provider

import android.R
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
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
import com.satrango.utils.toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.widget.Toast

import android.widget.AdapterView.OnItemClickListener


class SearchServiceProvidersScreen : AppCompatActivity() {

    private lateinit var binding: ActivitySearchServiceProvidersScreenBinding
    private lateinit var viewModel: SearchServiceProviderViewModel
    private lateinit var progressDialog: BeautifulProgressDialog

    companion object {
        var FROM_POPULAR_SERVICES = false
        var userLocationText = ""
        var subCategoryId = "0"
        var categoryId = "0"
        var keyword = "0"
        var offerId = 0
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchServiceProvidersScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeToolBar()
        initializeProgressDialog()

        binding.sortFilterBtn.setOnClickListener {
            SortAndFilterServiceProvider.FROM_PROVIDER = false
            startActivity(Intent(this, SortAndFilterServiceProvider::class.java))
        }

        binding.userLocation.setOnClickListener {
            UserLocationSelectionScreen.FROM_USER_DASHBOARD = false
            startActivity(Intent(this, UserLocationSelectionScreen::class.java))
        }

        if (UserUtils.getSearchFilter(this).isNotEmpty() && UserUtils.getSelectedSPDetails(this).isNotEmpty()) {

            val spDetails = Gson().fromJson(UserUtils.getSelectedSPDetails(this), SearchServiceProviderResModel::class.java)
            val filter = Gson().fromJson(UserUtils.getSearchFilter(this), SearchFilterModel::class.java)

            Log.e("FILTER:", Gson().toJson(filter))
            Log.e("SPDETAILS:", Gson().toJson(spDetails))

            val spDetailsList = spDetails.data
            val list = ArrayList<com.satrango.ui.user.user_dashboard.search_service_providers.models.Data>()
            for (sp in spDetailsList) {
                if (filter.priceRangeFrom.toDouble() <= sp.final_amount.toDouble() && filter.priceRangeTo.toDouble() >= sp.final_amount.toDouble()) {
                    if (filter.distance != "0") {
                        if (filter.distance.toDouble() < sp.distance_kms.toDouble()) {
                            continue
                        }
                    }
                    if (filter.experience) {
                        if (sp.exp == "0-1 Year") {
                            continue
                        }
                    }
                    if (filter.fresher) {
                        if (sp.exp != "0-1 Year") {
                            continue
                        }
                    }
                    list.add(sp)
                }
            }
            Log.e("FILTERED DATA:", Gson().toJson(list))

            when {
                filter.ranking -> {
                    binding.listCount.visibility = View.VISIBLE
                    binding.listCount.text = "${list.size} out of ${spDetails.data.size}"
                    binding.recyclerView.layoutManager = LinearLayoutManager(this)
                    binding.recyclerView.adapter = SearchServiceProviderAdapter(list.sortedBy { data: com.satrango.ui.user.user_dashboard.search_service_providers.models.Data -> data.rank }, this)
                }
                filter.nearMe -> {
                    binding.listCount.visibility = View.VISIBLE
                    binding.listCount.text = "${list.size} out of ${spDetails.data.size}"
                    binding.recyclerView.layoutManager = LinearLayoutManager(this)
                    binding.recyclerView.adapter = SearchServiceProviderAdapter(list.sortedBy { data: com.satrango.ui.user.user_dashboard.search_service_providers.models.Data -> data.distance_kms }, this)
                }
                filter.rating -> {
                    binding.listCount.visibility = View.VISIBLE
                    binding.listCount.text = "${list.size} out of ${spDetails.data.size}"
                    binding.recyclerView.layoutManager = LinearLayoutManager(this)
                    binding.recyclerView.adapter = SearchServiceProviderAdapter(list.sortedByDescending { data: com.satrango.ui.user.user_dashboard.search_service_providers.models.Data -> data.rating }, this)
                }
                filter.lowToHigh -> {
                    binding.listCount.visibility = View.VISIBLE
                    binding.listCount.text = "${list.size} out of ${spDetails.data.size}"
                    binding.recyclerView.layoutManager = LinearLayoutManager(this)
                    binding.recyclerView.adapter = SearchServiceProviderAdapter(list.sortedBy { data: com.satrango.ui.user.user_dashboard.search_service_providers.models.Data -> data.final_amount }, this)
                }
                filter.highToLow -> {
                    binding.listCount.visibility = View.VISIBLE
                    binding.listCount.text = "${list.size} out of ${spDetails.data.size}"
                    binding.recyclerView.layoutManager = LinearLayoutManager(this)
                    binding.recyclerView.adapter = SearchServiceProviderAdapter(list.sortedByDescending { data: com.satrango.ui.user.user_dashboard.search_service_providers.models.Data -> data.final_amount }, this)
                }
                else -> {
                    binding.listCount.visibility = View.VISIBLE
                    binding.listCount.text = "${list.size} out of ${spDetails.data.size}"
                    binding.recyclerView.layoutManager = LinearLayoutManager(this)
                    binding.recyclerView.adapter = SearchServiceProviderAdapter(list, this)
                }
            }
        } else if (UserUtils.getSelectedSPDetails(this).isNotEmpty()) {
            val spDetails = Gson().fromJson(UserUtils.getSelectedSPDetails(this), SearchServiceProviderResModel::class.java)
            binding.listCount.text = "${spDetails.data.size} out of ${spDetails.data.size}"
            binding.recyclerView.layoutManager = LinearLayoutManager(this)
            binding.recyclerView.adapter = SearchServiceProviderAdapter(spDetails.data, this)
        }

        val factory = ViewModelFactory(SearchServiceProviderRepository())
        viewModel = ViewModelProvider(this, factory)[SearchServiceProviderViewModel::class.java]
        viewModel.getKeywordsList(this).observe(this, {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    val keywordsList = it.data as ArrayList<Data>
                    val keywords = arrayListOf<String>()
                    keywordsList.forEach { keyword -> keywords.add(keyword.phrase) }
                    Log.e("KEYS:", keywordsList.toString())

                    binding.searchBar.threshold = 3
                    val adapter = ArrayAdapter(this, R.layout.simple_spinner_dropdown_item, keywords)
                    binding.searchBar.setAdapter(adapter)
                    binding.searchBar.onItemClickListener = OnItemClickListener { parent, arg1, position, id ->
                            val value = adapter.getItem(position) ?: ""
                            for (key in keywordsList) {
                                if (key.phrase == value) {
                                    keyword = key.id
                                    subCategoryId = key.subcategory_id
                                    UserUtils.saveSelectedKeywordCategoryId(this@SearchServiceProvidersScreen, key.category_id)
                                }
                            }
//                            toast(this, UserUtils.getSelectedKeywordCategoryId(this))
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
            if (binding.searchBar.text.toString().isEmpty()) {
                snackBar(binding.goBtn, "Please enter keyword to Search Service Providers")
            } else if (UserUtils.getLatitude(this).isEmpty() && UserUtils.getLongitude(this).isEmpty()) {
                snackBar(binding.goBtn, "Please select location")
            } else {
                UserUtils.saveSearchFilter(this, "")
                loadSearchResults(subCategoryId)
            }
        }

        if (FROM_POPULAR_SERVICES) {
            loadSearchResults(subCategoryId)
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
    private fun loadSearchResults(subCategory: String) {
        if (keyword == "0") {
            keyword = binding.searchBar.text.toString()
            if (keyword.isEmpty()) keyword = "0"
        }
//        toast(this, subCategory)
        val requestBody = SearchServiceProviderReqModel(
            UserUtils.getAddress(this),
            UserUtils.getCity(this),
            UserUtils.getCountry(this),
            RetrofitBuilder.USER_KEY,
            keyword,
            UserUtils.getPostalCode(this),
            UserUtils.getState(this),
            UserUtils.getLatitude(this),
            UserUtils.getLongitude(this),
            UserUtils.getUserId(this).toInt(),
            subCategory.toInt(),
            offerId
        )
        Log.e("SEARCHREQUEST:", Gson().toJson(requestBody))
//        {
//            "address": "54-14/7-78",
//            "city": "Vijayawada",
//            "country": "India",
//            "key": "BbJOTPWmcOaAJdnvCda74vDFtiJQCSYL",
//            "offer_id": 0,
//            "postal_code": "520008",
//            "search_phrase_id": "Android App Developer",
//            "state": "Andhra Pradesh",
//            "subcat_id": 0,
//            "user_lat": "16.5094445",
//            "user_long": "80.6706229",
//            "users_id": 76
//        }


//        val requestBody = SearchServiceProviderReqModel(
//            "54-14/7-78",
//            "Vijayawada",
//            "India",
//            RetrofitBuilder.USER_KEY,
//            "Android App Developer",
//            "520008",
//            "Andhra Pradesh",
//            "16.5094445",
//            "80.6706229",
//            76,
//            0,
//            offerId
//        )

        CoroutineScope(Dispatchers.Main).launch {
            progressDialog.show()
            val response = RetrofitBuilder.getUserRetrofitInstance().getUserSearchResults(requestBody)
            if (response.status == 200) {
                keyword = "0"
                if (!FROM_POPULAR_SERVICES) {
                    subCategoryId = "0"
                }
                progressDialog.dismiss()
                binding.recyclerView.layoutManager = LinearLayoutManager(this@SearchServiceProvidersScreen)
                binding.recyclerView.adapter = SearchServiceProviderAdapter(emptyList(), this@SearchServiceProvidersScreen)
                if (response.data.isEmpty()) {
                    weAreSorryDialog()
                } else {
                    UserUtils.saveSelectedSPDetails(this@SearchServiceProvidersScreen, Gson().toJson(response))
                    showBookingTypeDialog(response)
                }
            } else {
                progressDialog.dismiss()
                snackBar(binding.recyclerView, response.message)
            }
        }
    }

    override fun onBackPressed() {
        if (offerId == 0) {
            finish()
            UserUtils.saveSearchFilter(this, "")
            UserUtils.saveSelectedSPDetails(this, "")
            startActivity(Intent(this, UserDashboardScreen::class.java))
        } else {
            super.onBackPressed()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showBookingTypeDialog(spList: SearchServiceProviderResModel) {
        val data = spList
        val showBookingTypeBottomSheetDialog = BottomSheetDialog(this)
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
        viewResults.setOnClickListener {
            UserUtils.bookingType = "selected"
            showBookingTypeBottomSheetDialog.dismiss()
            UserUtils.saveFromInstantBooking(this, false)
            binding.listCount.text = "${data.data.size} out of ${data.data.size}"
            binding.recyclerView.adapter = SearchServiceProviderAdapter(data.data, this)
        }
        bookInstantly.setOnClickListener {
            UserUtils.bookingType = "instant"
            UserUtils.data = null
            binding.listCount.visibility = View.GONE
            showBookingTypeBottomSheetDialog.dismiss()
            binding.recyclerView.visibility = View.GONE
            UserUtils.saveFromInstantBooking(this, true)
            binding.listCount.text = "${data.data.size} out of ${data.data.size}"

            var existed = 0
            data.data.forEachIndexed { index, sp ->
                for (spSlot in data.slots_data) {
                    if (sp.users_id == spSlot.user_id) {
                        for (booking in spSlot.blocked_time_slots) {
                            if (UserUtils.getComingHour() == booking.time_slot_from.split(":")[0].toInt()) {
                                existed += 1
                            }
                        }
                    }
                }
            }

            if (data.data.isNotEmpty()) {
                if (existed < data.data.size) {
                    startActivity(Intent(this, BookingAttachmentsScreen::class.java))
                } else {
                    showBookingTypeBottomSheetDialog.dismiss()
                    showBookingInstantNotProceedDialog(data)
                }
            }
        }
        closeBtn.setOnClickListener {
            showBookingTypeBottomSheetDialog.dismiss()
        }
        showBookingTypeBottomSheetDialog.setContentView(dialogView)
        showBookingTypeBottomSheetDialog.setCancelable(false)
        showBookingTypeBottomSheetDialog.show()
    }

    @SuppressLint("SetTextI18n")
    private fun showBookingInstantNotProceedDialog(spListData: SearchServiceProviderResModel) {
        val data = spListData
        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setCancelable(false)
        val dialogView = LayoutInflater.from(this).inflate(com.satrango.R.layout.instant_booking_not_available_dialog, null)
        val closeBtn = dialogView.findViewById<MaterialCardView>(com.satrango.R.id.closeBtn)
        val showResultsBtn = dialogView.findViewById<TextView>(com.satrango.R.id.viewResults)
        val postJobBtn = dialogView.findViewById<TextView>(com.satrango.R.id.postJob)
        closeBtn.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        showResultsBtn.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                UserUtils.saveFromInstantBooking(this@SearchServiceProvidersScreen, false)
                binding.listCount.text = "${data.data.size} out of ${data.data.size}"
                binding.recyclerView.adapter = SearchServiceProviderAdapter(data.data, this@SearchServiceProvidersScreen)
            }
            bottomSheetDialog.dismiss()
        }
        postJobBtn.setOnClickListener {
            startActivity(Intent(this, PostJobTypeScreen::class.java))
        }
        bottomSheetDialog.setContentView(dialogView)
        bottomSheetDialog.show()
    }

    @SuppressLint("SetTextI18n")
    private fun showWaitingForSPConfirmationDialog() {
        val waitingForSpBottomSheetDialog = BottomSheetDialog(this)
        waitingForSpBottomSheetDialog.setCancelable(false)
        val dialogView =
            LayoutInflater.from(this).inflate(com.satrango.R.layout.waiting_for_sp_confirmation_dialog, null)
        val progressBar =
            dialogView.findViewById<CircularProgressIndicator>(com.satrango.R.id.progressBar)
        val time = dialogView.findViewById<TextView>(com.satrango.R.id.time)
        val closeBtn = dialogView.findViewById<MaterialCardView>(com.satrango.R.id.closeBtn)
        closeBtn.setOnClickListener {
            waitingForSpBottomSheetDialog.dismiss()
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
                    waitingForSpBottomSheetDialog.dismiss()
                    weAreSorryDialog()
                }
                if (seconds == 0) {
                    seconds = 59
                    minutes -= 1
                }
                mainHandler.postDelayed(this, 1000)
            }
        })
        waitingForSpBottomSheetDialog.setContentView(dialogView)
        waitingForSpBottomSheetDialog.show()
    }

    private fun weAreSorryDialog() {
        val weAreSorryBottomSheetDialog = BottomSheetDialog(this)
        weAreSorryBottomSheetDialog.setCancelable(false)
        val dialogView = LayoutInflater.from(this).inflate(com.satrango.R.layout.no_service_provider_found, null)
        val yesBtn = dialogView.findViewById<TextView>(com.satrango.R.id.yesBtn)
        val noBtn = dialogView.findViewById<TextView>(com.satrango.R.id.noBtn)
        val closeBtn = dialogView.findViewById<MaterialCardView>(com.satrango.R.id.closeBtn)
        closeBtn.setOnClickListener {
            weAreSorryBottomSheetDialog.dismiss()
        }
        yesBtn.setOnClickListener {
            snackBar(yesBtn, "Post the Job")
            weAreSorryBottomSheetDialog.dismiss()
            finish()
            startActivity(Intent(this, PostJobTypeScreen::class.java))
        }
        noBtn.setOnClickListener {
            weAreSorryBottomSheetDialog.dismiss()
            showWaitingForSPConfirmationDialog()
        }
        weAreSorryBottomSheetDialog.setContentView(dialogView)
        weAreSorryBottomSheetDialog.show()
    }
    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initializeProgressDialog() {
        progressDialog = BeautifulProgressDialog(this, BeautifulProgressDialog.withGIF, resources.getString(
                com.satrango.R.string.loading
            ))
        progressDialog.setGifLocation(Uri.parse("android.resource://${packageName}/${com.satrango.R.drawable.blue_loading}"))
        progressDialog.setLayoutColor(resources.getColor(com.satrango.R.color.progressDialogColor))
    }


}