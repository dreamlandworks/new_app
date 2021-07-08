package com.satrango.ui.user.user_dashboard.user_home_screen

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.satrango.remote.NetworkResponse
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.user.user_dashboard.drawer_menu.browse_categories.models.BrowserCategoryModel
import com.satrango.ui.user.user_dashboard.drawer_menu.browse_categories.models.BrowserSubCategoryModel
import com.satrango.ui.user.user_dashboard.user_home_screen.models.Data
import com.satrango.utils.hasInternetConnection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.lang.Exception

class UserHomeViewModel(private val userHomeRepository: UserHomeRepository): ViewModel() {

    val popularServices = MutableLiveData<NetworkResponse<List<BrowserSubCategoryModel>>>()
    val browseCategoriesList = MutableLiveData<NetworkResponse<List<BrowserCategoryModel>>>()
    val keywordsList = MutableLiveData<NetworkResponse<List<Data>>>()

    fun getPopularServicesList(context: Context): MutableLiveData<NetworkResponse<List<BrowserSubCategoryModel>>> {
        if (hasInternetConnection(context)) {
            CoroutineScope(Dispatchers.Main).launch {
                val popularServicesList = ArrayList<BrowserSubCategoryModel>()
                popularServices.value = NetworkResponse.Loading()
                try {
                    val response = userHomeRepository.getPopularServices()
                    val responseObject = JSONObject(response.string())
                    if (responseObject.getInt("status") == 200) {
                        val subCategoriesArray = responseObject.getJSONArray("data")
                        for (index in 0 until subCategoriesArray.length()) {
                            val subCategory = subCategoriesArray.getJSONObject(index)
                            popularServicesList.add(
                                BrowserSubCategoryModel(
                                    subCategory.getString("category_id"),
                                    subCategory.getString("id"),
                                    subCategory.getString("image"),
                                    subCategory.getString("sub_name")
                                )
                            )
                        }
                    }
                    popularServices.value = NetworkResponse.Success(popularServicesList)
                } catch (e: Exception) {
                    popularServices.value = NetworkResponse.Failure(e.message)
                }
            }
        } else {
            popularServices.value = NetworkResponse.Failure("No Internet Connection!")
        }
        return popularServices
    }

    fun getBrowseCategories(context: Context): MutableLiveData<NetworkResponse<List<BrowserCategoryModel>>> {
        if (hasInternetConnection(context)) {
            CoroutineScope(Dispatchers.Main).launch {
                browseCategoriesList.value = NetworkResponse.Loading()
                try {
                    val categoriesList = ArrayList<BrowserCategoryModel>()
                    val response = userHomeRepository.getBrowseCategories()
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
                    browseCategoriesList.value = NetworkResponse.Success(categoriesList)
                } catch (e: Exception) {
                    browseCategoriesList.value = NetworkResponse.Failure(e.message)
                }
            }
        } else {
            browseCategoriesList.value = NetworkResponse.Failure("No Internet Connection")
        }

        return browseCategoriesList
    }

    fun getKeywordsList(context: Context): MutableLiveData<NetworkResponse<List<Data>>> {
        if (hasInternetConnection(context)) {
            CoroutineScope(Dispatchers.Main).launch {
                keywordsList.value = NetworkResponse.Loading()
                try {
                    val response = userHomeRepository.getKeyWords()
//                    Log.e("KEYWORDS", response.string())
                    if (response.status == 200) {
                        keywordsList.value = NetworkResponse.Success(response.data)
                    } else {
                        keywordsList.value = NetworkResponse.Failure(response.message)
                    }
                } catch (e: Exception) {
                    Log.e("KEYWORDS", e.message!!)
                    keywordsList.value = NetworkResponse.Failure(e.message)
                }
            }
        } else {
            keywordsList.value = NetworkResponse.Failure("No Internet Connection")
        }

        return keywordsList
    }

}