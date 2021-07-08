package com.satrango.ui.user.user_dashboard.user_alerts

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.satrango.remote.NetworkResponse
import com.satrango.ui.user.user_dashboard.user_alerts.models.Data
import com.satrango.utils.hasInternetConnection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class UserAlertsViewModel(private val repository: UserAlertsRepository): ViewModel() {

    val normalAlertsList = MutableLiveData<NetworkResponse<List<Data>>>()
    val actionableAlertsList = MutableLiveData<NetworkResponse<List<Data>>>()

    fun getNormalAlerts(context: Context): MutableLiveData<NetworkResponse<List<Data>>> {
        if (hasInternetConnection(context)) {
            normalAlertsList.value = NetworkResponse.Loading()
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val response = repository.getUserAlerts("1")
                    if (response.status == 200) {
                        normalAlertsList.value = NetworkResponse.Success(response.data)
                    } else {
                        normalAlertsList.value = NetworkResponse.Failure(response.message)
                    }
                } catch (e: Exception) {
                    normalAlertsList.value = NetworkResponse.Failure(e.message)
                }

            }
        } else {
            normalAlertsList.value = NetworkResponse.Failure("No Internet Connection")
        }
        return normalAlertsList
    }

    fun getActionableAlerts(context: Context): MutableLiveData<NetworkResponse<List<Data>>> {
        if (hasInternetConnection(context)) {
            actionableAlertsList.value = NetworkResponse.Loading()
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val response = repository.getUserAlerts("2")
                    if (response.status == 200) {
                        actionableAlertsList.value = NetworkResponse.Success(response.data)
                    } else {
                        actionableAlertsList.value = NetworkResponse.Failure(response.message)
                    }
                } catch (e: Exception) {
                    actionableAlertsList.value = NetworkResponse.Failure(e.message)
                }

            }
        } else {
            actionableAlertsList.value = NetworkResponse.Failure("No Internet Connection")
        }
        return actionableAlertsList
    }

}