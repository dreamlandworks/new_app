package com.satrango.ui.service_provider.provider_dashboard.alerts

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.satrango.remote.NetworkResponse
import com.satrango.ui.user.user_dashboard.user_alerts.models.Action
import com.satrango.ui.user.user_dashboard.user_alerts.models.Data
import com.satrango.ui.user.user_dashboard.user_alerts.models.Regular
import com.satrango.utils.hasInternetConnection
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.lang.Exception

class ProviderAlertsViewModel(private val repository: ProviderAlertRepository): ViewModel() {

    val normalAlertsList = MutableLiveData<NetworkResponse<List<Regular>>>()
    val actionableAlertsList = MutableLiveData<NetworkResponse<List<Action>>>()
    val updateAlertsToRead = MutableLiveData<NetworkResponse<String>>()

    fun getNormalAlerts(context: Context): MutableLiveData<NetworkResponse<List<Regular>>> {
//        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                try {
                    normalAlertsList.value = NetworkResponse.Loading()
                    val response = async { repository.getProviderAlerts(context) }
                    if (response.await().status == 200) {
                        normalAlertsList.value = NetworkResponse.Success(response.await().regular)
                    } else {
                        normalAlertsList.value = NetworkResponse.Failure(response.await().message)
                    }
                } catch (e: Exception) {
                    normalAlertsList.value = NetworkResponse.Failure(e.message)
                }

            }
//        } else {
//            normalAlertsList.value = NetworkResponse.Failure("No Internet Connection")
//        }
        return normalAlertsList
    }

    fun getActionableAlerts(context: Context): MutableLiveData<NetworkResponse<List<Action>>> {
//        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                try {
                    actionableAlertsList.value = NetworkResponse.Loading()
                    val response = async { repository.getProviderAlerts(context) }
                    if (response.await().status == 200) {
                        actionableAlertsList.value = NetworkResponse.Success(response.await().action)
                    } else {
                        actionableAlertsList.value = NetworkResponse.Failure(response.await().message)
                    }
                } catch (e: Exception) {
                    actionableAlertsList.value = NetworkResponse.Failure(e.message)
                }

            }
//        } else {
//            actionableAlertsList.value = NetworkResponse.Failure("No Internet Connection")
//        }
        return actionableAlertsList
    }

    fun updateAlertsToRead(context: Context, alertType: String): MutableLiveData<NetworkResponse<String>> {
//        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                try {
                    updateAlertsToRead.value = NetworkResponse.Loading()
                    val response = async { repository.updateAlertToRead(context, alertType) }
                    val jsonResponse = JSONObject(response.await().string())
                    if (jsonResponse.getInt("id") == 200) {
                        updateAlertsToRead.value = NetworkResponse.Success(jsonResponse.getString("message"))
                    } else {
                        updateAlertsToRead.value = NetworkResponse.Failure(jsonResponse.getString("message"))
                    }
                } catch (e: Exception) {
                    updateAlertsToRead.value = NetworkResponse.Failure(e.message)
                }

            }
//        } else {
//            updateAlertsToRead.value = NetworkResponse.Failure("No Internet Connection")
//        }
        return updateAlertsToRead
    }

}