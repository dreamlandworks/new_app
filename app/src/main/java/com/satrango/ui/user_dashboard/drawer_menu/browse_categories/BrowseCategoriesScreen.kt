package com.satrango.ui.user_dashboard.drawer_menu.browse_categories

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.gson.JsonSyntaxException
import com.satrango.R
import com.satrango.databinding.ActivityBrowseCategoriesScreenBinding
import com.satrango.remote.RetrofitBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.HttpException
import java.net.SocketTimeoutException

class BrowseCategoriesScreen : AppCompatActivity() {

    private lateinit var binding: ActivityBrowseCategoriesScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBrowseCategoriesScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolBar = binding.root.findViewById<View>(R.id.toolBar)
        toolBar.findViewById<ImageView>(R.id.toolBarBackBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarBackTVBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text = resources.getString(R.string.browse_categories)


        binding.apply {

            CoroutineScope(Main).launch {

                try {
                    val response = RetrofitBuilder.getRetrofitInstance().userBrowseCategories()
                    val responseObject = JSONObject(response.string())
                    if (responseObject.getInt("status") == 200) {
                        val categoriesArray = responseObject.getJSONArray("data")
                        val categoriesList = ArrayList<BrowserCategoryModel>()
                        for (index in 0 until categoriesArray.length()) {
                            val category = categoriesArray.getJSONObject(index)
                            categoriesList.add(
                                BrowserCategoryModel(
                                    category.getString("category"),
                                    category.getString("id"),
                                    category.getString("image")
                                )
                            )
                        }
                        categoryRV.adapter = BrowseCategoriesAdapter(categoriesList)
                    }

                } catch (e: HttpException) {
                    Snackbar.make(binding.categoryRV, "Server Busy", Snackbar.LENGTH_SHORT).show()
                } catch (e: JsonSyntaxException) {
                    Snackbar.make(binding.categoryRV, "Something Went Wrong", Snackbar.LENGTH_SHORT).show()
                } catch (e: SocketTimeoutException) {
                    Snackbar.make(binding.categoryRV, "Please check internet Connection", Snackbar.LENGTH_SHORT).show()
                }

            }

            CoroutineScope(Main).launch {

                try {
                    val response = RetrofitBuilder.getRetrofitInstance().userBrowseSubCategories()
                    val responseObject = JSONObject(response.string())
                    if (responseObject.getInt("status") == 200) {
                        val categoriesArray = responseObject.getJSONArray("data")
                        val subCategoriesList = ArrayList<BrowserSubCategoryModel>()
                        for (index in 0 until categoriesArray.length()) {
                            val category = categoriesArray.getJSONObject(index)
                            subCategoriesList.add(
                                BrowserSubCategoryModel(
                                    category.getString("category_id"),
                                    category.getString("id"),
                                    category.getString("image"),
                                    category.getString("sub_name")
                                )
                            )
                        }
                        subCategoryRV.adapter = BrowseSubCategoriesAdapter(subCategoriesList)
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

    }
}