package com.satrango.ui.user.user_dashboard.user_home_screen.user_location_change

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.satrango.remote.NetworkResponse
import com.satrango.ui.user.user_dashboard.user_home_screen.user_location_change.models.AllLocationsResModel
import com.satrango.ui.user.user_dashboard.user_home_screen.user_location_change.models.DataX
import com.satrango.ui.user.user_dashboard.user_home_screen.user_location_change.models.UserLocationChangeReqModel
import com.satrango.ui.user.user_dashboard.user_home_screen.user_location_change.models.UserLocationChangeResModel
import com.satrango.utils.hasInternetConnection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.lang.Exception

class UserLocationChangeViewModel(private val repository: UserLocationChangeRepository): ViewModel() {

    val locations = MutableLiveData<NetworkResponse<List<DataX>>>()

    fun allLocations(context: Context): MutableLiveData<NetworkResponse<List<DataX>>> {
//        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                try {
                    val response = async { repository.getAllLocation() }
                    if (response.await().status == 200) {
                        locations.value = NetworkResponse.Success(response.await().data)
                    } else {
                        locations.value = NetworkResponse.Failure(response.await().message)
                    }
                } catch (e: Exception) {
                    locations.value = NetworkResponse.Failure(e.message)
                }
            }
//        } else {
//            locations.value = NetworkResponse.Failure("No Internet Connection!")
//        }
        return locations
    }

}