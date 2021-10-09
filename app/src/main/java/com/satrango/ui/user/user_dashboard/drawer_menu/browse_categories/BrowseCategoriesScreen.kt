package com.satrango.ui.user.user_dashboard.drawer_menu.browse_categories

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.basusingh.beautifulprogressdialog.BeautifulProgressDialog
import com.bumptech.glide.Glide
import com.satrango.R
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivityBrowseCategoriesScreenBinding
import com.satrango.remote.NetworkResponse
import com.satrango.ui.user.user_dashboard.drawer_menu.browse_categories.models.BrowserCategoryModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_profile.UserProfileScreen
import com.satrango.utils.*
import de.hdodenhof.circleimageview.CircleImageView

class BrowseCategoriesScreen : AppCompatActivity(), BrowseCategoriesInterface {

    private lateinit var progressDialog: BeautifulProgressDialog
    private lateinit var viewModel: BrowseCategoriesViewModel
    private lateinit var categoriesList: java.util.ArrayList<BrowserCategoryModel>
    private lateinit var binding: ActivityBrowseCategoriesScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBrowseCategoriesScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeToolBar()
        initializeProgressDialog()
        loadBrowseCategoriesScreen()
    }

    private fun initializeToolBar() {
        val toolBar = binding.root.findViewById<View>(R.id.toolBar)
        toolBar.findViewById<TextView>(R.id.toolBarBackTVBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<ImageView>(R.id.toolBarBackBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text = resources.getString(R.string.browse_categories)
        val imageView = toolBar.findViewById<CircleImageView>(R.id.toolBarImage)
        imageView.setOnClickListener {
            startActivity(Intent(this, UserProfileScreen::class.java))
        }
        loadProfileImage(imageView)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initializeProgressDialog() {
        progressDialog = BeautifulProgressDialog(this, BeautifulProgressDialog.withImage, resources.getString(R.string.loading))
        progressDialog.setImageLocation(resources.getDrawable(R.drawable.circlelogo))
        progressDialog.setLayoutColor(resources.getColor(R.color.white))
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
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    categoriesList = it.data as java.util.ArrayList<BrowserCategoryModel>
                    binding.categoryRV.layoutManager = LinearLayoutManager(this@BrowseCategoriesScreen, LinearLayoutManager.HORIZONTAL, false)
                    binding.categoryRV.adapter = BrowseCategoriesAdapter(categoriesList, this@BrowseCategoriesScreen)
                    displaySubCategories(categoriesList[0].id)
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.categoryRV, it.message!!)
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
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    binding.subCategoryRV.adapter = BrowseSubCategoriesAdapter(it.data!!)
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.categoryRV, it.message!!)
                }
            }
        })



    }
}