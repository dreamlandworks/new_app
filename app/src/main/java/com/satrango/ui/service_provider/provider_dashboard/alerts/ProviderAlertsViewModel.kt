package com.satrango.ui.service_provider.provider_dashboard.alerts

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.satrango.remote.NetworkResponse
import com.satrango.ui.user.user_dashboard.user_alerts.models.Data
import com.satrango.utils.hasInternetConnection
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.lang.Exception

class ProviderAlertsViewModel(private val repository: ProviderAlertRepository): ViewModel() {

    val normalAlertsList = MutableLiveData<NetworkResponse<List<Data>>>()
    val actionableAlertsList = MutableLiveData<NetworkResponse<List<Data>>>()

    fun getNormalAlerts(context: Context): MutableLiveData<NetworkResponse<List<Data>>> {
        if (hasInternetConnection(context)) {
            normalAlertsList.value = NetworkResponse.Loading()
            viewModelScope.launch {
                try {
                    val response = async { repository.getProviderAlerts("1") }
                    if (response.await().status == 200) {
                        normalAlertsList.value = NetworkResponse.Success(response.await().data)
                    } else {
                        normalAlertsList.value = NetworkResponse.Failure(response.await().message)
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
            viewModelScope.launch {
                try {
                    val response = async { repository.getProviderAlerts("2") }
                    if (response.await().status == 200) {
                        actionableAlertsList.value = NetworkResponse.Success(response.await().data)
                    } else {
                        actionableAlertsList.value = NetworkResponse.Failure(response.await().message)
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