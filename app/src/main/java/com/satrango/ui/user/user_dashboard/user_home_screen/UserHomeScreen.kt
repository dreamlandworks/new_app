package com.satrango.ui.user.user_dashboard.user_home_screen

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.basusingh.beautifulprogressdialog.BeautifulProgressDialog
import com.google.android.material.snackbar.Snackbar
import com.google.gson.JsonSyntaxException
import com.satrango.R
import com.satrango.base.BaseFragment
import com.satrango.databinding.FragmentUserHomeScreenBinding
import com.satrango.remote.NetworkResponse
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.user.user_dashboard.UserDashboardScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.browse_categories.BrowseCategoriesAdapter
import com.satrango.ui.user.user_dashboard.drawer_menu.browse_categories.BrowseCategoriesInterface
import com.satrango.ui.user.user_dashboard.drawer_menu.browse_categories.BrowseCategoriesScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.browse_categories.models.BrowseCategoryReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.browse_categories.models.BrowserCategoryModel
import com.satrango.ui.user.user_dashboard.drawer_menu.browse_categories.models.BrowserSubCategoryModel
import com.satrango.ui.user.user_dashboard.search_service_providers.search_service_provider.SearchServiceProvidersScreen
import com.satrango.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.File
import java.net.SocketTimeoutException

class UserHomeScreen :
    BaseFragment<UserHomeViewModel, FragmentUserHomeScreenBinding, UserHomeRepository>(),
    BrowseCategoriesInterface {

    private lateinit var progressDialog: BeautifulProgressDialog
    private lateinit var categoriesList: ArrayList<BrowserCategoryModel>

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        if (PermissionUtils.checkGPSStatus(requireActivity()) && networkAvailable(requireContext())) {
//            UserDashboardScreen.fetchLocation(requireContext())
//        }
        if (UserUtils.getUserName(requireContext()).isNotEmpty()) {

        }
        loadHomeScreen()
        toast(requireContext(), UserUtils.getUserId(requireContext()))

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val requestBody = BrowseCategoryReqModel(
                    UserUtils.getUserId(requireContext()),
                    RetrofitBuilder.USER_KEY
                )
                val response = RetrofitBuilder.getUserRetrofitInstance().getUserProfile(requestBody)
                val responseData = response.data
                if (response.status == 200) {
                    binding.userName.text = "Hiii, ${responseData.fname} ${responseData.lname}"
                } else {
                    Snackbar.make(
                        UserDashboardScreen.binding.navigationView,
                        "Something went wrong!",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            } catch (e: HttpException) {
                Snackbar.make(UserDashboardScreen.binding.navigationView, "Server Busy", Snackbar.LENGTH_SHORT).show()
            } catch (e: JsonSyntaxException) {
                Snackbar.make(UserDashboardScreen.binding.navigationView, "Something Went Wrong", Snackbar.LENGTH_SHORT)
                    .show()
            } catch (e: SocketTimeoutException) {
                Snackbar.make(
                    UserDashboardScreen.binding.navigationView,
                    "Please check internet Connection",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initializeProgressDialog() {
        progressDialog = BeautifulProgressDialog(requireActivity(), BeautifulProgressDialog.withGIF, resources.getString(R.string.loading))
        progressDialog.setGifLocation(Uri.parse("android.resource://${activity?.packageName}/${R.drawable.blue_loading}"))
        progressDialog.setLayoutColor(resources.getColor(R.color.progressDialogColor))
    }

    private fun loadHomeScreen() {

        if (!PermissionUtils.isNetworkConnected(requireContext())) {
            PermissionUtils.connectionAlert(requireContext()) { loadHomeScreen() }
            return
        }

        initializeProgressDialog()

        binding.searchBar.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                UserUtils.saveSearchFilter(requireContext(), "")
                UserUtils.saveSelectedSPDetails(requireContext(), "")
                SearchServiceProvidersScreen.FROM_POPULAR_SERVICES = false
                SearchServiceProvidersScreen.offerId = 0
                startActivity(Intent(requireContext(), SearchServiceProvidersScreen::class.java))
            }
        }
        binding.searchBar.setOnClickListener {
            UserUtils.saveSearchFilter(requireContext(), "")
            startActivity(Intent(requireContext(), SearchServiceProvidersScreen::class.java))
        }
        binding.browseAll.setOnClickListener {
            startActivity(Intent(requireContext(), BrowseCategoriesScreen::class.java))
        }

        binding.categoryRV.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        viewModel.getBrowseCategories(requireContext()).observe(viewLifecycleOwner, {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    categoriesList = it.data as java.util.ArrayList<BrowserCategoryModel>
                    binding.categoryRV.adapter =
                        BrowseCategoriesAdapter(categoriesList, this@UserHomeScreen)
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    toast(requireContext(), it.message!!)
                }
            }
        })

        updatePopularServices("1")

    }

    private fun updatePopularServices(categoryId: String) {
        binding.userPopularServicesRv.layoutManager =
            GridLayoutManager(requireContext(), 2, GridLayoutManager.HORIZONTAL, false)
        viewModel.getPopularServicesList(requireContext(), categoryId).observe(viewLifecycleOwner, {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    val list = it.data!! as ArrayList<BrowserSubCategoryModel>
                    list.addAll(list)
                    list.addAll(list)
                    shrinkTo(list, 6)
                    Log.e("POPULARSERVICES:", list.toString())
                    binding.userPopularServicesRv.adapter = UserPopularServicesAdapter(list)
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    toast(requireContext(), it.message!!)
                }
            }
        })

    }

    override fun onResume() {
        super.onResume()
        UserDashboardScreen.binding.toolBarLayout.visibility = View.VISIBLE
    }

    override fun getFragmentViewModel(): Class<UserHomeViewModel> = UserHomeViewModel::class.java

    override fun getFragmentBinding(
        layoutInflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentUserHomeScreenBinding =
        FragmentUserHomeScreenBinding.inflate(layoutInflater, container, false)

    override fun getFragmentRepository(): UserHomeRepository = UserHomeRepository()

    override fun selectedCategory(categoryId: String, position: Int) {
        val tempList = ArrayList<BrowserCategoryModel>()
        categoriesList.forEachIndexed { index, browserCategoryModel ->
            if (position == index) {
                tempList.add(
                    BrowserCategoryModel(
                        browserCategoryModel.category,
                        browserCategoryModel.id,
                        browserCategoryModel.image,
                        true
                    )
                )
            } else {
                tempList.add(
                    BrowserCategoryModel(
                        browserCategoryModel.category,
                        browserCategoryModel.id,
                        browserCategoryModel.image,
                        false
                    )
                )
            }
        }
        if (position == categoriesList.size - 1) {
            binding.categoryRV.scrollToPosition(categoriesList.size - 1)
        }
        binding.categoryRV.adapter = BrowseCategoriesAdapter(tempList, this)
        updatePopularServices(categoryId)
    }

    fun shrinkTo(list: ArrayList<BrowserSubCategoryModel>, newSize: Int) {
        val size = list.size
        if (newSize >= size) return
        for (i in newSize until size) {
            list.removeAt(list.size - 1)
        }
    }

}