package com.satrango.ui.user.user_dashboard.search_service_providers.search_service_provider

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.satrango.remote.NetworkResponse
import com.satrango.ui.user.user_dashboard.search_service_providers.models.SearchServiceProviderReqModel
import com.satrango.utils.UserUtils
import com.satrango.utils.hasInternetConnection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.lang.Exception

class SearchServiceProviderViewModel(private val repository: SearchServiceProviderRepository): ViewModel() {

    val keywordsList = MutableLiveData<NetworkResponse<List<com.satrango.ui.user.user_dashboard.user_home_screen.models.Data>>>()
    val searchResultsList = MutableLiveData<NetworkResponse<com.satrango.ui.user.user_dashboard.search_service_providers.models.SearchServiceProviderResModel>>()

    fun getKeywordsList(context: Context): MutableLiveData<NetworkResponse<List<com.satrango.ui.user.user_dashboard.user_home_screen.models.Data>>> {
//        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                try {
                    keywordsList.value = NetworkResponse.Loading()
                    val response = async { repository.getKeyWords() }
                    if (response.await().status == 200) {
                        keywordsList.value = NetworkResponse.Success(response.await().data)
                    } else {
                        keywordsList.value = NetworkResponse.Failure(response.await().message)
                    }
                } catch (e: Exception) {
//                    Log.e("KEYWORDS", e.message!!)
                    keywordsList.value = NetworkResponse.Failure(e.message)
                }
            }
//        } else {
//            keywordsList.value = NetworkResponse.Failure("No Internet Connection")
//        }
        return keywordsList
    }

    fun getSearchResults(context: Context, requestBody: SearchServiceProviderReqModel): MutableLiveData<NetworkResponse<com.satrango.ui.user.user_dashboard.search_service_providers.models.SearchServiceProviderResModel>> {
//        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                try {
                    searchResultsList.value = NetworkResponse.Loading()
                    val response = async { repository.getSearchResults(requestBody) }
                    if (response.await().status == 200) {
                        UserUtils.setTempAddressId(context, response.await().temp_address_id.toString())
                        UserUtils.setSearchResultsId(context, response.await().search_results_id.toString())
//                        Log.e("JSON:", Gson().toJson(response.await()))
                        searchResultsList.value = NetworkResponse.Success(response.await())
                    } else {
                        searchResultsList.value = NetworkResponse.Failure(response.await().message)
                    }
                } catch (e: Exception) {
                    searchResultsList.value = NetworkResponse.Failure(e.message!!)
                }
            }
//        } else {
//            searchResultsList.value = NetworkResponse.Failure("No Internet Connection")
//        }
        return searchResultsList
    }

}