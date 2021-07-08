package com.satrango.ui.user.user_dashboard.drawer_menu.browse_categories

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.satrango.remote.NetworkResponse
import com.satrango.ui.user.user_dashboard.drawer_menu.browse_categories.models.BrowserCategoryModel
import com.satrango.ui.user.user_dashboard.drawer_menu.browse_categories.models.BrowserSubCategoryModel
import com.satrango.utils.hasInternetConnection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.lang.Exception

class BrowseCategoriesViewModel(private val repository: BrowseCategoriesRepository): ViewModel() {

    val browseCategoriesList = MutableLiveData<NetworkResponse<List<BrowserCategoryModel>>>()
    val browseSubCategoriesList = MutableLiveData<NetworkResponse<List<BrowserSubCategoryModel>>>()

    fun getBrowseCategories(context: Context): MutableLiveData<NetworkResponse<List<BrowserCategoryModel>>> {
        if (hasInternetConnection(context)) {
            CoroutineScope(Dispatchers.Main).launch {
                browseCategoriesList.value = NetworkResponse.Loading()
                try {
                    val categoriesList = ArrayList<BrowserCategoryModel>()
                    val response = repository.getBrowseCategories()
                    val responseObject = JSONObject(response.string())
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
                        browseCategoriesList.value = NetworkResponse.Success(categoriesList)
                    } else {
                        browseCategoriesList.value = NetworkResponse.Failure("No Categories Found")
                    }
                } catch (e: Exception) {
                    browseCategoriesList.value = NetworkResponse.Failure(e.message)
                }
            }
        } else {
            browseCategoriesList.value = NetworkResponse.Failure("No Internet Connection")
        }

        return browseCategoriesList
    }

    fun getBrowseSubCategories(context: Context, categoryId: String): MutableLiveData<NetworkResponse<List<BrowserSubCategoryModel>>> {
        if (hasInternetConnection(context)) {
            CoroutineScope(Dispatchers.Main).launch {
                browseSubCategoriesList.value = NetworkResponse.Loading()
                try {
                    val subCategoriesList = ArrayList<BrowserSubCategoryModel>()
                    val response = repository.getBrowseSubCategories(categoryId)
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
                        browseSubCategoriesList.value = NetworkResponse.Success(subCategoriesList)
                    } else {
                        browseSubCategoriesList.value = NetworkResponse.Failure("No Sub Categories Found!")
                    }
                } catch (e: Exception) {
                    browseSubCategoriesList.value = NetworkResponse.Failure(e.message)
                }
            }
        } else {
            browseSubCategoriesList.value = NetworkResponse.Failure("No Internet Connection")
        }

        return browseSubCategoriesList
    }
}