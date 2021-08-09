package com.satrango.ui.user.user_dashboard.search_service_providers.search_service_provider

import android.R
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivitySearchServiceProvidersScreenBinding
import com.satrango.remote.NetworkResponse
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.user.user_dashboard.UserDashboardScreen
import com.satrango.ui.user.user_dashboard.search_service_providers.SortAndFilterServiceProvider
import com.satrango.ui.user.user_dashboard.search_service_providers.models.SearchServiceProviderReqModel
import com.satrango.ui.user.user_dashboard.user_home_screen.models.Data
import com.satrango.ui.user.user_dashboard.user_home_screen.user_location_change.UserLocationSelectionScreen
import com.satrango.utils.UserUtils
import com.satrango.utils.snackBar
import com.satrango.utils.toast
import org.json.JSONObject

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
                loadSearchResults(keyword, subCategoryId)
            } else {
                snackBar(binding.goBtn, "Please enter keyword to Search Service Providers")
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun loadSearchResults(keywordId: String, subCategory: String) {
        val requestBody = SearchServiceProviderReqModel(UserUtils.getAddress(this), UserUtils.getCity(this), UserUtils.getCountry(this), RetrofitBuilder.USER_KEY, keywordId.toInt(), UserUtils.getPostalCode(this), UserUtils.getState(this), UserUtils.getLatitude(this), UserUtils.getLongitude(this), UserUtils.getUserId(this).toInt(), subCategory.toInt())
//        val requestBody = SearchServiceProviderReqModel(
//            "Near Mandovi Showroom",
//            "Mangalore",
//            "India",
//            RetrofitBuilder.USER_KEY,
//            keywordId.toInt(),
//            "575014",
//            "Karnataka",
//            "12.9951",
//            "74.8094",
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
                    binding.listCount.text = "Showing ${it.data!!.size} out of ${it.data.size} results"
                    binding.recyclerView.adapter = SearchServiceProviderAdapter(it.data)
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.recyclerView, it.message!!)
                }
            }
        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        startActivity(Intent(this, UserDashboardScreen::class.java))
    }
}