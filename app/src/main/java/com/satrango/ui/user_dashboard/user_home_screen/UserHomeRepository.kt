package com.satrango.ui.user_dashboard.user_home_screen

import android.util.Log
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.google.gson.JsonSyntaxException
import com.satrango.base.BaseRepository
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.user_dashboard.drawer_menu.browse_categories.BrowseSubCategoriesAdapter
import com.satrango.ui.user_dashboard.drawer_menu.browse_categories.models.BrowseCategoryReqModel
import com.satrango.ui.user_dashboard.drawer_menu.browse_categories.models.BrowserSubCategoryModel
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
                val response = RetrofitBuilder.getRetrofitInstance().userBrowseSubCategories(BrowseCategoryReqModel("1"))
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
                Log.e("CATEGORIES: ", subCategoriesList.toString())
                return subCategoriesList
            } catch (e: HttpException) {

            } catch (e: JsonSyntaxException) {

            } catch (e: SocketTimeoutException) {

            }
        return subCategoriesList
    }


}