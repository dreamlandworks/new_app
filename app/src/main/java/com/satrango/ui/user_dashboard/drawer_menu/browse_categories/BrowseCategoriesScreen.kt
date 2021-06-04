package com.satrango.ui.user_dashboard.drawer_menu.browse_categories

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.gson.JsonSyntaxException
import com.satrango.R
import com.satrango.databinding.ActivityBrowseCategoriesScreenBinding
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.user_dashboard.drawer_menu.my_profile.UserProfileScreen
import com.satrango.utils.UserUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.HttpException
import java.net.SocketTimeoutException

class BrowseCategoriesScreen : AppCompatActivity(), BrowseCategoriesInterface {

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
        val imageView = toolBar.findViewById<ImageView>(R.id.toolBarImage)
        imageView.setOnClickListener {
            startActivity(Intent(this, UserProfileScreen::class.java))
        }
        if (UserUtils.getUserProfilePic(this).isNotEmpty()) {
            Glide.with(imageView).load(UserUtils.getUserProfilePic(this)).into(imageView)
        }

        binding.apply {
            CoroutineScope(Main).launch {
                try {
                    val response = RetrofitBuilder.getRetrofitInstance().userBrowseCategories()
                    val responseObject = JSONObject(response.string())
                    if (responseObject.getInt("status") == 200) {
                        val categoriesArray = responseObject.getJSONArray("data")
                        categoriesList = ArrayList<BrowserCategoryModel>()
                        for (index in 0 until categoriesArray.length()) {
                            val category = categoriesArray.getJSONObject(index)
                            if (index == 0) {
                                categoriesList.add(BrowserCategoryModel(category.getString("category"), category.getString("id"), category.getString("image"), true))
                            } else {
                                categoriesList.add(BrowserCategoryModel(category.getString("category"), category.getString("id"), category.getString("image"), false))
                            }
                        }
                        categoryRV.adapter = BrowseCategoriesAdapter(categoriesList, this@BrowseCategoriesScreen)
                        displaySubCategories("1")
                    }
                } catch (e: HttpException) {
                    Snackbar.make(binding.categoryRV, "Server Busy", Snackbar.LENGTH_SHORT).show()
                } catch (e: JsonSyntaxException) {
                    Snackbar.make(binding.categoryRV, "Something Went Wrong", Snackbar.LENGTH_SHORT)
                        .show()
                } catch (e: SocketTimeoutException) {
                    Snackbar.make(
                        binding.categoryRV,
                        "Please check internet Connection",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }
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
        binding.categoryRV.adapter = BrowseCategoriesAdapter(tempList, this)
    }

    private fun displaySubCategories(categoryId: String) {

        CoroutineScope(Main).launch {
            binding.progressBar.visibility = View.VISIBLE
            try {
                val response = RetrofitBuilder.getRetrofitInstance()
                    .userBrowseSubCategories(BrowseCategoryReqModel(categoryId))
                val responseObject = JSONObject(response.string())
                if (responseObject.getInt("status") == 200) {
                    val subCategoriesArray = responseObject.getJSONArray("data")
                    val subCategoriesList = ArrayList<BrowserSubCategoryModel>()
                    for (index in 0 until subCategoriesArray.length()) {
                        val subCategory = subCategoriesArray.getJSONObject(index)
                        subCategoriesList.add(
                            BrowserSubCategoryModel(
                                subCategory.getString("category_id"),
                                subCategory.getString("id"),
                                subCategory.getString("image"),
                                subCategory.getString("sub_name")
                            )
                        )
                    }
                    binding.progressBar.visibility = View.GONE
                    binding.subCategoryRV.adapter = BrowseSubCategoriesAdapter(subCategoriesList)
                }
            } catch (e: HttpException) {
                Snackbar.make(binding.categoryRV, "Server Busy", Snackbar.LENGTH_SHORT).show()
                binding.progressBar.visibility = View.GONE
            } catch (e: JsonSyntaxException) {
                Snackbar.make(binding.categoryRV, "Something Went Wrong", Snackbar.LENGTH_SHORT)
                    .show()
                binding.progressBar.visibility = View.GONE
            } catch (e: SocketTimeoutException) {
                Snackbar.make(
                    binding.categoryRV,
                    "Please check internet Connection",
                    Snackbar.LENGTH_SHORT
                ).show()
                binding.progressBar.visibility = View.GONE
            }

        }
    }
}