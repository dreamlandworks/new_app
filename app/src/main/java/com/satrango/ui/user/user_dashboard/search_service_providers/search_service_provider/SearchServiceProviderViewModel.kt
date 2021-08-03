package com.satrango.ui.user.user_dashboard.search_service_providers.search_service_provider

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.satrango.remote.NetworkResponse
import com.satrango.ui.user.user_dashboard.search_service_providers.models.SearchServiceProviderReqModel
import com.satrango.utils.hasInternetConnection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class SearchServiceProviderViewModel(private val repository: SearchServiceProviderRepository): ViewModel() {

    val keywordsList = MutableLiveData<NetworkResponse<List<com.satrango.ui.user.user_dashboard.user_home_screen.models.Data>>>()
    val searchResultsList = MutableLiveData<NetworkResponse<List<com.satrango.ui.user.user_dashboard.search_service_providers.models.Data>>>()

    fun getKeywordsList(context: Context): MutableLiveData<NetworkResponse<List<com.satrango.ui.user.user_dashboard.user_home_screen.models.Data>>> {
        if (hasInternetConnection(context)) {
            CoroutineScope(Dispatchers.Main).launch {
                keywordsList.value = NetworkResponse.Loading()
                try {
                    val response = repository.getKeyWords()
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

    fun getSearchResults(context: Context, requestBody: SearchServiceProviderReqModel): MutableLiveData<NetworkResponse<List<com.satrango.ui.user.user_dashboard.search_service_providers.models.Data>>> {
        if (hasInternetConnection(context)) {
            CoroutineScope(Dispatchers.Main).launch {
                searchResultsList.value = NetworkResponse.Loading()
                try {
                    val response = repository.getSearchResults(requestBody)
                    if (response.status == 200) {
                         searchResultsList.value = NetworkResponse.Success(response.data)
                    } else {
                        searchResultsList.value = NetworkResponse.Failure(response.message)
                    }
                } catch (e: Exception) {
                    searchResultsList.value = NetworkResponse.Failure(e.message!!)
                }
            }
        } else {
            searchResultsList.value = NetworkResponse.Failure("No Internet Connection")
        }
        return searchResultsList
    }

}