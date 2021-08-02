package com.satrango.ui.user.user_dashboard.user_home_screen.user_location_change

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.satrango.remote.NetworkResponse
import com.satrango.utils.hasInternetConnection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.lang.Exception

class UserLocationChangeViewModel(private val repository: UserLocationChangeRepository): ViewModel() {

    val changeLocation = MutableLiveData<NetworkResponse<String>>()

    fun changeUserLocation(context: Context, requestBody: UserLocationChangeReqModel): MutableLiveData<NetworkResponse<String>> {
        if (hasInternetConnection(context)) {
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val response = repository.changeUserLocation(requestBody)
                    val jsonResponse = JSONObject(response.string())
                    if (jsonResponse.getInt("status") == 200) {
                        changeLocation.value = NetworkResponse.Success(jsonResponse.getString("message"))
                    } else {
                        changeLocation.value = NetworkResponse.Failure(jsonResponse.getString("message"))
                    }
                } catch (e: Exception) {
                    changeLocation.value = NetworkResponse.Failure(e.message)
                }
            }
        } else {
            changeLocation.value = NetworkResponse.Failure("No Internet Connection!")
        }
        return changeLocation
    }

}