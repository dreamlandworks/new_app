package com.satrango.ui.user_dashboard.user_home_screen

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.satrango.base.BaseRepository
import com.satrango.remote.RetrofitBuilder
import com.satrango.remote.UserApiEndPoints
import com.satrango.ui.user_dashboard.drawer_menu.browse_categories.BrowseSubCategoriesAdapter
import com.satrango.ui.user_dashboard.drawer_menu.browse_categories.models.BrowseCategoryReqModel
import com.satrango.ui.user_dashboard.drawer_menu.browse_categories.models.BrowserCategoryModel
import com.satrango.ui.user_dashboard.drawer_menu.browse_categories.models.BrowserSubCategoryModel
import com.satrango.ui.user_dashboard.drawer_menu.browse_categories.models.UserKeyModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.HttpException
import java.net.SocketTimeoutException

class UserHomeRepository : BaseRepository() {

    suspend fun getPopularServices(): ArrayList<BrowserSubCategoryModel> {
        val subCategoriesList = ArrayList<BrowserSubCategoryModel>()

            try {
                val response = RetrofitBuilder.getRetrofitInstance().userBrowseSubCategories(BrowseCategoryReqModel("1", RetrofitBuilder.KEY))
                val responseObject = JSONObject(response.string())
                if (responseObject.getInt("status") == 200) {
                    val subCategoriesArray = responseObject.getJSONArray("data")
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
                }
                return subCategoriesList
            } catch (e: HttpException) {

            } catch (e: JsonSyntaxException) {

            } catch (e: SocketTimeoutException) {

            }
        return subCategoriesList
    }

    suspend fun getBrowseCategories(): List<BrowserCategoryModel> {
        val categoriesList = ArrayList<BrowserCategoryModel>()
        try {
            val response = RetrofitBuilder.getRetrofitInstance().userBrowseCategories(RetrofitBuilder.KEY)
            val responseObject = JSONObject(response.string())
            Log.e("BROWSER CATEGORIES", responseObject.toString())
            if (responseObject.getInt("status") == 200) {
                val categoriesArray = responseObject.getJSONArray("data")
                for (index in 0 until categoriesArray.length()) {
                    val category = categoriesArray.getJSONObject(index)
                    if (index == 0) {
                        categoriesList.add(BrowserCategoryModel(category.getString("category"), category.getString("id"), category.getString("image"), true))
                    } else {
                        categoriesList.add(BrowserCategoryModel(category.getString("category"), category.getString("id"), category.getString("image"), false))
                    }
                }
            }
        } catch (e: HttpException) {

        } catch (e: JsonSyntaxException) {

        } catch (e: SocketTimeoutException) {

        }

        return categoriesList
    }

}