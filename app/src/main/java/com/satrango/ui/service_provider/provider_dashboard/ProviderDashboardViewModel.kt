package com.satrango.ui.service_provider.provider_dashboard

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.satrango.remote.NetworkResponse
import com.satrango.ui.service_provider.provider_dashboard.models.ProviderOnlineReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_profile.models.Data
import com.satrango.utils.hasInternetConnection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.json.JSONObject

class ProviderDashboardViewModel(private val repository: ProviderDashboardRepository) :
    ViewModel() {

    val userProfile = MutableLiveData<NetworkResponse<Data>>()
    val saveLocation = MutableLiveData<NetworkResponse<String>>()
    val onlineStatus = MutableLiveData<NetworkResponse<String>>()

    fun userProfile(context: Context): MutableLiveData<NetworkResponse<Data>> {
        if (hasInternetConnection(context)) {
            userProfile.value = NetworkResponse.Loading()
            viewModelScope.launch {
                try {
                    val response = async { repository.userProfile(context) }
                    Log.e("PROFILE: ", Gson().toJson(response.await()))
                    if (response.await().status == 200) {
                        userProfile.value = NetworkResponse.Success(response.await().data)
                    } else {
                        userProfile.value = NetworkResponse.Failure(response.await().message)
                    }
                } catch (e: Exception) {
                    userProfile.value = NetworkResponse.Failure(e.message)
                }
            }
        }
        return userProfile
    }

    fun saveLocation(context: Context, requestBody: ProviderLocationReqModel): MutableLiveData<NetworkResponse<String>> {
        if (hasInternetConnection(context)) {
            saveLocation.value = NetworkResponse.Loading()
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val response = repository.uploadUserLocation(requestBody)
                    val jsonResponse = JSONObject(response.string())
                    Log.e("LOCATION", jsonResponse.toString())
                    if (jsonResponse.getInt("status") == 200) {
                        saveLocation.value = NetworkResponse.Success(jsonResponse.getString("message"))
                    } else {
                        saveLocation.value = NetworkResponse.Failure(jsonResponse.getString("message"))
                    }
                } catch (e: Exception) {
                    saveLocation.value = NetworkResponse.Failure(e.message)
                }
            }
        }
        return saveLocation
    }

    fun onlineStatus(context: Context, requestBody: ProviderOnlineReqModel): MutableLiveData<NetworkResponse<String>> {
        if (hasInternetConnection(context)) {
            onlineStatus.value = NetworkResponse.Loading()
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val response = repository.updateProviderOnlineStatus(requestBody)
                    val jsonResponse = JSONObject(response.string())
                    Log.e("LOCATION", jsonResponse.toString())
                    if (jsonResponse.getInt("status") == 200) {
                        onlineStatus.value = NetworkResponse.Success(jsonResponse.getString("message"))
                    } else {
                        onlineStatus.value = NetworkResponse.Failure(jsonResponse.getString("message"))
                    }
                } catch (e: Exception) {
                    onlineStatus.value = NetworkResponse.Failure(e.message)
                }
            }
        }
        return onlineStatus
    }

}