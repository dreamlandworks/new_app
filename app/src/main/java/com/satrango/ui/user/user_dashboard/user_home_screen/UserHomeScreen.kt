package com.satrango.ui.user.user_dashboard.user_home_screen

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.basusingh.beautifulprogressdialog.BeautifulProgressDialog
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.gson.JsonSyntaxException
import com.satrango.R
import com.satrango.base.BaseFragment
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.FragmentUserHomeScreenBinding
import com.satrango.remote.NetworkResponse
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.user.bookings.view_booking_details.models.RescheduleStatusChangeReqModel
import com.satrango.ui.user.user_dashboard.UserDashboardScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.browse_categories.BrowseCategoriesAdapter
import com.satrango.ui.user.user_dashboard.drawer_menu.browse_categories.BrowseCategoriesInterface
import com.satrango.ui.user.user_dashboard.drawer_menu.browse_categories.BrowseCategoriesScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.browse_categories.models.BrowseCategoryReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.browse_categories.models.BrowserCategoryModel
import com.satrango.ui.user.user_dashboard.drawer_menu.browse_categories.models.BrowserSubCategoryModel
import com.satrango.ui.user.user_dashboard.search_service_providers.search_service_provider.SearchServiceProvidersScreen
import com.satrango.ui.user.user_dashboard.user_alerts.UserAlertScreen
import com.satrango.ui.user.user_dashboard.user_alerts.UserAlertsRepository
import com.satrango.ui.user.user_dashboard.user_alerts.UserAlertsViewModel
import com.satrango.ui.user.user_dashboard.user_alerts.models.Action
import com.satrango.utils.*
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import com.bumptech.glide.load.HttpException
import com.google.gson.Gson
import com.paytm.pgsdk.PaytmOrder
import com.paytm.pgsdk.PaytmPaymentTransactionCallback
import com.paytm.pgsdk.TransactionManager
import com.satrango.ui.user.user_dashboard.drawer_menu.my_profile.models.UserProfileReqModel
import java.net.SocketTimeoutException


class UserHomeScreen :
    BaseFragment<UserHomeViewModel, FragmentUserHomeScreenBinding, UserHomeRepository>(),
    BrowseCategoriesInterface {

    private lateinit var progressDialog: BeautifulProgressDialog
    private lateinit var categoriesList: ArrayList<BrowserCategoryModel>

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadHomeScreen()
        toast(requireContext(), UserUtils.getUserId(requireContext()))

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val requestBody = UserProfileReqModel(
                    RetrofitBuilder.USER_KEY,
                    UserUtils.getUserId(requireContext()).toInt(),
                    UserUtils.getCity(requireContext())
                )
//                toast(requireContext(), Gson().toJson(requestBody))
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
                UserUtils.saveSelectedAllSPDetails(requireContext(), "")
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
        viewModel.getBrowseCategories(requireContext()).observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    categoriesList = it.data as java.util.ArrayList<BrowserCategoryModel>
                    categoriesList.removeLast()
                    binding.categoryRV.adapter =
                        BrowseCategoriesAdapter(categoriesList, this@UserHomeScreen)
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    toast(requireContext(), it.message!!)
                }
            }
        }

        updatePopularServices("1")

    }

    private fun updatePopularServices(categoryId: String) {
        binding.userPopularServicesRv.layoutManager =
            GridLayoutManager(requireContext(), 2, GridLayoutManager.HORIZONTAL, false)
        viewModel.getPopularServicesList(requireContext(), categoryId).observe(viewLifecycleOwner) {
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
//                    Log.e("POPULARSERVICES:", list.toString())
                    binding.userPopularServicesRv.adapter = UserPopularServicesAdapter(list)
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    toast(requireContext(), it.message!!)
                }
            }
        }

        showPendingActionableAlerts()

    }

    private fun showPendingActionableAlerts() {
        val factory = ViewModelFactory(UserAlertsRepository())
        val viewModel = ViewModelProvider(this, factory)[UserAlertsViewModel::class.java]
        viewModel.getActionableAlerts(requireContext()).observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    if (it.data!!.isNotEmpty()) {
                        for (data in it.data) {
                            if (data.type_id == "9" && data.status == "2") {
                                showPendingActionableAlertsDialog(data)
                            }
                        }
                    }
                    progressDialog.dismiss()
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                }
            }
        }
    }

    private fun showPendingActionableAlertsDialog(data: Action) {
        val dialog = Dialog(requireContext())
        val dialogView = layoutInflater.inflate(R.layout.show_pending_actionable_alerts_dialog, null)
        val profilePic = dialogView.findViewById<CircleImageView>(R.id.profilePic)
        val description = dialogView.findViewById<TextView>(R.id.description)
        val skipBtn = dialogView.findViewById<TextView>(R.id.skipBtn)
        val acceptBtn = dialogView.findViewById<TextView>(R.id.acceptBtn)
        val rejectBtn = dialogView.findViewById<TextView>(R.id.rejectBtn)
        Glide.with(requireContext()).load(RetrofitBuilder.BASE_URL + data.profile_pic).error(R.drawable.images).into(profilePic)
        description.text = data.description
        skipBtn.setOnClickListener { dialog.dismiss() }
        acceptBtn.setOnClickListener {
            dialog.dismiss()
            rescheduleStatusChangeApiCall(
                data.booking_id.toInt(),
                data.reschedule_id.toInt(),
                data.user_id.toInt(),
                12,
                data.user_id.toInt()
            )
        }
        rejectBtn.setOnClickListener {
            dialog.dismiss()
            rescheduleStatusChangeApiCall(
                data.booking_id.toInt(),
                data.reschedule_id.toInt(),
                data.sp_id.toInt(),
                11,
                data.user_id.toInt()
            )
        }
        dialog.setCancelable(false)
        dialog.setContentView(dialogView)
        val window = dialog.window
        window!!.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        dialog.show()
    }

    private fun rescheduleStatusChangeApiCall(
        bookingId: Int,
        rescheduleId: Int,
        spId: Int,
        statusId: Int,
        userId: Int
    ) {
        val requestBody = RescheduleStatusChangeReqModel(
            bookingId,
            RetrofitBuilder.USER_KEY,
            rescheduleId,
            spId,
            statusId,
            UserAlertScreen.USER_TYPE,
            userId
        )
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = RetrofitBuilder.getUserRetrofitInstance().updateRescheduleStatus(requestBody)
                val jsonResponse = JSONObject(response.string())
                if (jsonResponse.getInt("status") == 200) {
                    toast(requireContext(), jsonResponse.getString("message"))
                } else {
                    toast(requireContext(), jsonResponse.getString("message"))
                }
            } catch (e: Exception) {
                toast(requireContext(), e.message!!)
            }
        }
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