package com.satrango.ui.service_provider.provider_dashboard.dashboard

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.satrango.remote.NetworkResponse
import com.satrango.ui.user.user_dashboard.drawer_menu.my_profile.models.Data
import com.satrango.utils.hasInternetConnection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import org.json.JSONObject

class ProviderDashboardViewModel(private val repository: ProviderDashboardRepository) :
    ViewModel() {

    val userProfile = MutableLiveData<NetworkResponse<Data>>()
    val saveLocation = MutableLiveData<NetworkResponse<String>>()

    fun userProfile(context: Context): MutableLiveData<NetworkResponse<Data>> {
        if (hasInternetConnection(context)) {
            userProfile.value = NetworkResponse.Loading()
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val response = repository.userProfile(context)
                    if (response.status == 200) {
                        userProfile.value = NetworkResponse.Success(response.data)
                    } else {
                        userProfile.value = NetworkResponse.Failure(response.message)
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

}