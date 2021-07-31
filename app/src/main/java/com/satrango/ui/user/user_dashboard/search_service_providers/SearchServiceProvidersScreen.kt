package com.satrango.ui.user.user_dashboard.search_service_providers

import android.R
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivitySearchServiceProvidersScreenBinding
import com.satrango.remote.NetworkResponse
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.user.user_dashboard.search_service_providers.models.SearchServiceProviderReqModel
import com.satrango.ui.user.user_dashboard.user_home_screen.models.Data
import com.satrango.utils.UserUtils
import com.satrango.utils.snackBar
import com.satrango.utils.toast

class SearchServiceProvidersScreen : AppCompatActivity() {

    private lateinit var binding: ActivitySearchServiceProvidersScreenBinding
    private lateinit var viewModel: SearchServiceProviderViewModel
    private lateinit var keywordsList: ArrayList<Data>
    private lateinit var progressDialog: ProgressDialog

    companion object {
        var userLocationText = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchServiceProvidersScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressDialog = ProgressDialog(this)
        progressDialog.setCancelable(false)
        progressDialog.setMessage("Loading...")

        binding.userLocation.text = userLocationText

        val factory = ViewModelFactory(SearchServiceProviderRepository())
        viewModel = ViewModelProvider(this, factory)[SearchServiceProviderViewModel::class.java]

        viewModel.getKeywordsList(this).observe(this, {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    keywordsList = it.data as ArrayList<Data>
                    val keywords = arrayListOf<String>()

                    keywordsList.forEach { keyword -> keywords.add(keyword.keyword) }

                    binding.searchBar.threshold = 1
                    val adapter =
                        ArrayAdapter(this, R.layout.simple_spinner_dropdown_item, keywords)
                    binding.searchBar.setAdapter(adapter)
                    binding.searchBar.setOnItemClickListener { _, _, position, _ ->
                        toast(this, Gson().toJson(keywordsList[position]))
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
            loadSearchResults()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun loadSearchResults() {
//        val requestBody = SearchServiceProviderReqModel(UserUtils.address, UserUtils.city, UserUtils.country, RetrofitBuilder.USER_KEY, 4, UserUtils.postalCode, UserUtils.state, UserUtils.latitude, UserUtils.longitute, UserUtils.getUserId(this).toInt())
        val requestBody = SearchServiceProviderReqModel(
            "Near Mandovi Showroom",
            "Mangalore",
            "India",
            RetrofitBuilder.USER_KEY,
            4,
            "575014",
            "Karnataka",
            "12.9951",
            "74.8094",
            UserUtils.getUserId(this).toInt()
        )

        viewModel.getSearchResults(this, requestBody).observe(this, {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    binding.listCount.text =
                        "Showing ${it.data!!.size} out of ${it.data.size} results"
                    binding.recyclerView.adapter = SearchServiceProviderAdapter(it.data)
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.recyclerView, it.message!!)
                }
            }
        })
    }
}