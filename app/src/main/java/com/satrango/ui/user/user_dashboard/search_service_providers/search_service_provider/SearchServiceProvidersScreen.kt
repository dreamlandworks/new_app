package com.satrango.ui.user.user_dashboard.search_service_providers.search_service_provider

import android.R
import android.annotation.SuppressLint
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
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
import com.satrango.ui.user.user_dashboard.search_service_providers.models.SearchServiceProviderReqModel
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
            startActivity(Intent(this, SortAndFilterServiceProvider::class.java))
        }

        binding.userLocation.setOnClickListener {
            startActivity(Intent(this, UserLocationSelectionScreen::class.java))
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

                    keywordsList.forEach { keyword -> keywords.add(keyword.keyword) }

                    binding.searchBar.threshold = 1
                    val adapter = ArrayAdapter(this, R.layout.simple_spinner_dropdown_item, keywords)
                    binding.searchBar.setAdapter(adapter)
                    binding.searchBar.setOnItemClickListener { _, _, position, _ ->
                        keyword = keywordsList[position].keyword_id
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
                showBookingTypeDialog()
            } else {
                snackBar(binding.goBtn, "Please enter keyword to Search Service Providers")
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun loadSearchResults(keywordId: String, subCategory: String, type: String) {
//        val requestBody = SearchServiceProviderReqModel(UserUtils.getAddress(this), UserUtils.getCity(this), UserUtils.getCountry(this), RetrofitBuilder.USER_KEY, keywordId.toInt(), UserUtils.getPostalCode(this), UserUtils.getState(this), UserUtils.getLatitude(this), UserUtils.getLongitude(this), UserUtils.getUserId(this).toInt(), subCategory.toInt())
        val requestBody = SearchServiceProviderReqModel(
            "Near Mandovi Showroom",
            "Chilakaluripet",
            "India",
            RetrofitBuilder.USER_KEY,
            keywordId.toInt(),
            "575014",
            "Karnataka",
            "16.0948",
            "80.1656",
            UserUtils.getUserId(this).toInt(),
            subCategory.toInt()
        )

        viewModel.getSearchResults(this, requestBody).observe(this, {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    UserUtils.saveSelectedSPDetails(this, Gson().toJson(it.data!!))
                    if (type == "ViewResults") {
                        BookingAttachmentsScreen.FROM_BOOK_INSTANTLY = false
                        binding.listCount.text = "Showing ${it.data.data.size} out of ${it.data.data.size} results"
                        binding.recyclerView.adapter = SearchServiceProviderAdapter(it.data.data)
                    } else {
                        BookingAttachmentsScreen.FROM_BOOK_INSTANTLY = true
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
        val dialog = Dialog(this)
        dialog.setCancelable(false)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(com.satrango.R.layout.search_type_dialog)
        val window = dialog.window
        window?.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        val viewResults = dialog.findViewById<TextView>(com.satrango.R.id.viewResults)
        val bookInstantly = dialog.findViewById<TextView>(com.satrango.R.id.bookInstantly)
        viewResults.setOnClickListener {
            dialog.dismiss()
            loadSearchResults(keyword, subCategoryId, "ViewResults")
        }
        bookInstantly.setOnClickListener {
            dialog.dismiss()
            loadSearchResults(keyword, subCategoryId, "BookInstantly")
        }
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
//                    Checkout.preload(applicationContext)
//                    makePayment()
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