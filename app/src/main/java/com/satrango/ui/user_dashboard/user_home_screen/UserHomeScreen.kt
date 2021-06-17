package com.satrango.ui.user_dashboard.user_home_screen

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.gson.JsonSyntaxException
import com.satrango.base.BaseFragment
import com.satrango.databinding.FragmentUserHomeScreenBinding
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.user_dashboard.UserDashboardScreen
import com.satrango.ui.user_dashboard.drawer_menu.browse_categories.BrowseCategoriesAdapter
import com.satrango.ui.user_dashboard.drawer_menu.browse_categories.BrowseCategoriesInterface
import com.satrango.ui.user_dashboard.drawer_menu.browse_categories.models.BrowserCategoryModel
import com.satrango.utils.PermissionUtils
import com.satrango.utils.UserUtils
import com.satrango.utils.networkAvailable
import com.satrango.utils.toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.HttpException
import java.net.SocketTimeoutException

class UserHomeScreen : BaseFragment<UserHomeViewModel, FragmentUserHomeScreenBinding, UserHomeRepository>(),
    BrowseCategoriesInterface {

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
        binding.userPopularServicesRv.layoutManager = GridLayoutManager(requireContext(), 2, GridLayoutManager.HORIZONTAL, false)
        viewModel.getPopularServicesList().observe(viewLifecycleOwner, {
            binding.userPopularServicesRv.adapter = UserPopularServicesAdapter(it)
        })

        loadCategories()

    }

    private fun loadCategories() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = RetrofitBuilder.getRetrofitInstance().userBrowseCategories()
                val responseObject = JSONObject(response.string())
                if (responseObject.getInt("status") == 200) {
                    val categoriesArray = responseObject.getJSONArray("data")
                    categoriesList = ArrayList()
                    for (index in 0 until categoriesArray.length()) {
                        val category = categoriesArray.getJSONObject(index)
                        if (index == 0) {
                            categoriesList.add(BrowserCategoryModel(category.getString("category"), category.getString("id"), category.getString("image"), true))
                        } else {
                            categoriesList.add(BrowserCategoryModel(category.getString("category"), category.getString("id"), category.getString("image"), false))
                        }
                    }
                    binding.categoryRV.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                    binding.categoryRV.adapter = BrowseCategoriesAdapter(categoriesList, this@UserHomeScreen)
                }
            } catch (e: HttpException) {
                Snackbar.make(binding.categoryRV, "Server Busy", Snackbar.LENGTH_SHORT).show()
            } catch (e: JsonSyntaxException) {
                Snackbar.make(binding.categoryRV, "Something Went Wrong", Snackbar.LENGTH_SHORT).show()
            } catch (e: SocketTimeoutException) {
                Snackbar.make(binding.categoryRV, "Please check internet Connection", Snackbar.LENGTH_SHORT).show()
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