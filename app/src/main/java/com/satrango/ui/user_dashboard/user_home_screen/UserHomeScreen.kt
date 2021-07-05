package com.satrango.ui.user_dashboard.user_home_screen

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.satrango.base.BaseFragment
import com.satrango.databinding.FragmentUserHomeScreenBinding
import com.satrango.ui.user_dashboard.UserDashboardScreen
import com.satrango.ui.user_dashboard.drawer_menu.browse_categories.BrowseCategoriesAdapter
import com.satrango.ui.user_dashboard.drawer_menu.browse_categories.BrowseCategoriesInterface
import com.satrango.ui.user_dashboard.drawer_menu.browse_categories.models.BrowserCategoryModel
import com.satrango.utils.PermissionUtils
import com.satrango.utils.UserUtils
import com.satrango.utils.networkAvailable

class UserHomeScreen : BaseFragment<UserHomeViewModel, FragmentUserHomeScreenBinding, UserHomeRepository>(), BrowseCategoriesInterface {

    private lateinit var categoriesList: java.util.ArrayList<BrowserCategoryModel>

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (PermissionUtils.checkGPSStatus(requireContext()) && networkAvailable(requireContext())) {
            UserDashboardScreen.fetchLocation(requireContext())
        }
        if (UserUtils.getUserName(requireContext()).isNotEmpty()) {
            binding.userName.text = "Hiii, ${UserUtils.getUserName(requireContext())}"
        }

        loadHomeScreen()

    }

    private fun loadHomeScreen() {

        if (!PermissionUtils.isNetworkConnected(requireContext())) {
            PermissionUtils.connectionAlert(requireContext()) { loadHomeScreen() }
            return
        }

        binding.userPopularServicesRv.layoutManager = GridLayoutManager(requireContext(), 2, GridLayoutManager.HORIZONTAL, false)
        viewModel.getPopularServicesList().observe(viewLifecycleOwner, {
            binding.userPopularServicesRv.adapter = UserPopularServicesAdapter(it)
        })

        binding.categoryRV.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        viewModel.getBrowseCategories().observe(viewLifecycleOwner, {
            categoriesList = it as java.util.ArrayList<BrowserCategoryModel>
            binding.categoryRV.adapter = BrowseCategoriesAdapter(categoriesList, this@UserHomeScreen)
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
    ): FragmentUserHomeScreenBinding = FragmentUserHomeScreenBinding.inflate(layoutInflater, container, false)

    override fun getFragmentRepository(): UserHomeRepository = UserHomeRepository()

    override fun selectedCategory(categoryId: String, position: Int) {
        val tempList = ArrayList<BrowserCategoryModel>()
        categoriesList.forEachIndexed { index, browserCategoryModel ->
            if (position == index) {
                tempList.add(BrowserCategoryModel(browserCategoryModel.category, browserCategoryModel.id, browserCategoryModel.image, true))
            } else {
                tempList.add(BrowserCategoryModel(browserCategoryModel.category, browserCategoryModel.id, browserCategoryModel.image, false))
            }
        }
        if (position == categoriesList.size - 1) {
            binding.categoryRV.scrollToPosition(categoriesList.size - 1)
        }
        binding.categoryRV.adapter = BrowseCategoriesAdapter(tempList, this)

    }

}