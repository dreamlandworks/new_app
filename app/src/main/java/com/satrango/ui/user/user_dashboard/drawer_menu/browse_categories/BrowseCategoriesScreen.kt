package com.satrango.ui.user.user_dashboard.drawer_menu.browse_categories

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.satrango.R
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivityBrowseCategoriesScreenBinding
import com.satrango.remote.NetworkResponse
import com.satrango.ui.user.user_dashboard.drawer_menu.browse_categories.models.BrowserCategoryModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_profile.UserProfileScreen
import com.satrango.utils.PermissionUtils
import com.satrango.utils.UserUtils
import com.satrango.utils.loadProfileImage
import com.satrango.utils.toast
import de.hdodenhof.circleimageview.CircleImageView

class BrowseCategoriesScreen : AppCompatActivity(), BrowseCategoriesInterface {

    private lateinit var viewModel: BrowseCategoriesViewModel
    private lateinit var categoriesList: java.util.ArrayList<BrowserCategoryModel>
    private lateinit var binding: ActivityBrowseCategoriesScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBrowseCategoriesScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolBar = binding.root.findViewById<View>(R.id.toolBar)
        toolBar.findViewById<TextView>(R.id.toolBarBackTVBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<ImageView>(R.id.toolBarBackBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text = resources.getString(R.string.browse_categories)
        val imageView = toolBar.findViewById<CircleImageView>(R.id.toolBarImage)
        imageView.setOnClickListener {
            startActivity(Intent(this, UserProfileScreen::class.java))
        }
        loadProfileImage(imageView)
        loadBrowseCategoriesScreen()
    }

    private fun loadBrowseCategoriesScreen() {
        if (!PermissionUtils.isNetworkConnected(this)) {
            PermissionUtils.connectionAlert(this) { loadBrowseCategoriesScreen() }
            return
        }
        val factory = ViewModelFactory(BrowseCategoriesRepository())
        viewModel = ViewModelProvider(this, factory)[BrowseCategoriesViewModel::class.java]
        viewModel.getBrowseCategories(this).observe(this, {
            when(it) {
                is NetworkResponse.Loading -> {

                }
                is NetworkResponse.Success -> {
                    categoriesList = it.data as java.util.ArrayList<BrowserCategoryModel>
                    binding.categoryRV.layoutManager = LinearLayoutManager(this@BrowseCategoriesScreen, LinearLayoutManager.HORIZONTAL, false)
                    binding.categoryRV.adapter = BrowseCategoriesAdapter(categoriesList, this@BrowseCategoriesScreen)
                    displaySubCategories(categoriesList[0].id)
                }
                is NetworkResponse.Failure -> {
                    toast(this, it.message!!)
                }
            }

        })
    }

    override fun selectedCategory(categoryId: String, position: Int) {
        displaySubCategories(categoryId)
        updateCategoryListAndDisplay(position)
    }

    private fun updateCategoryListAndDisplay(position: Int) {
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

    private fun displaySubCategories(categoryId: String) {
        viewModel.getBrowseSubCategories(this, categoryId).observe(this, {
            when(it) {
                is NetworkResponse.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is NetworkResponse.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.subCategoryRV.adapter = BrowseSubCategoriesAdapter(it.data!!)
                }
                is NetworkResponse.Failure -> {
                    binding.progressBar.visibility = View.GONE
                    toast(this, it.message!!)
                }
            }
        })



    }
}