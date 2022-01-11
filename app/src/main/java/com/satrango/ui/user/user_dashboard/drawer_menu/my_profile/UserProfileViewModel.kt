package com.satrango.ui.user.user_dashboard.drawer_menu.my_profile

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.satrango.remote.NetworkResponse
import com.satrango.ui.user.user_dashboard.drawer_menu.browse_categories.models.BrowseCategoryReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_profile.models.Data
import com.satrango.ui.user.user_dashboard.drawer_menu.my_profile.models.UserProfileUpdateReqModel
import com.satrango.utils.hasInternetConnection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.json.JSONObject

class UserProfileViewModel(private val repository: UserProfileRepository) : ViewModel() {

    val userProfileInfo = MutableLiveData<NetworkResponse<Data>>()
    val updateProfileInfo = MutableLiveData<NetworkResponse<String>>()
    val deleteUserAddress = MutableLiveData<NetworkResponse<String>>()

    fun userProfileInfo(context: Context, userId: String): MutableLiveData<NetworkResponse<Data>> {
//        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                try {
                    userProfileInfo.value = NetworkResponse.Loading()
                    val response = async { repository.userProfileInfo(userId) }
                    if (response.await().status == 200) {
                        userProfileInfo.value = NetworkResponse.Success(response.await().data)
                    } else {
                        userProfileInfo.value = NetworkResponse.Failure(response.await().message)
                    }
                } catch (e: Exception) {
                    userProfileInfo.value = NetworkResponse.Failure(e.message)
                }
            }
//        } else {
//            userProfileInfo.value = NetworkResponse.Failure("No internet Connection!")
//        }
        return userProfileInfo
    }

    fun updateProfileInfo(context: Context, requestBody: UserProfileUpdateReqModel): MutableLiveData<NetworkResponse<String>> {
//        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                try {
                    updateProfileInfo.value = NetworkResponse.Loading()
                    val response = async { repository.updateProfileInfo(requestBody) }
                    val responseObject = response.await()
                    val jsonObject = JSONObject(responseObject.string())
                    Log.e("PROFILE", jsonObject.toString())
                    if (jsonObject.getInt("status") == 200) {
                        updateProfileInfo.value = NetworkResponse.Success(jsonObject.getString("message"))
                    } else {
                        updateProfileInfo.value = NetworkResponse.Failure(jsonObject.getString("message"))
                    }
                } catch (e: Exception) {
                    updateProfileInfo.value = NetworkResponse.Failure("Error: " + e.message)
                }
            }
//        } else {
//            updateProfileInfo.value = NetworkResponse.Failure("No Internet Connection!")
//        }
        return updateProfileInfo
    }

    fun deleteUserAddress(context: Context, requestBody: BrowseCategoryReqModel): MutableLiveData<NetworkResponse<String>> {
//        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                try {
                    deleteUserAddress.value = NetworkResponse.Loading()
                    val response = async { repository.deleteUserAddress(requestBody) }
                    val jsonObject = JSONObject(response.await().string())
                    if (jsonObject.getInt("status") == 200) {
                        deleteUserAddress.value = NetworkResponse.Success("Address deleted!")
                    } else {
                        deleteUserAddress.value = NetworkResponse.Failure("Something went wrong!")
                    }
                } catch (e: java.lang.Exception) {
                    deleteUserAddress.value = NetworkResponse.Failure(e.message!!)
                }
            }
//        } else {
//            deleteUserAddress.value = NetworkResponse.Failure("No Internet Connection!")
//        }
        return deleteUserAddress
    }

}