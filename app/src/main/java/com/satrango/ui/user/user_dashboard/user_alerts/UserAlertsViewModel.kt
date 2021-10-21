package com.satrango.ui.user.user_dashboard.user_alerts

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.satrango.remote.NetworkResponse
import com.satrango.ui.user.user_dashboard.user_alerts.models.Data
import com.satrango.ui.user.user_dashboard.user_offers.models.OffersListReqModel
import com.satrango.utils.hasInternetConnection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.lang.Exception

class UserAlertsViewModel(private val repository: UserAlertsRepository): ViewModel() {

    val normalAlertsList = MutableLiveData<NetworkResponse<List<Data>>>()
    val actionableAlertsList = MutableLiveData<NetworkResponse<List<Data>>>()
    val userOffers = MutableLiveData<NetworkResponse<List<com.satrango.ui.user.user_dashboard.user_offers.models.Data>>>()
    val updateAlertsToRead = MutableLiveData<NetworkResponse<String>>()

    fun getNormalAlerts(context: Context): MutableLiveData<NetworkResponse<List<Data>>> {
        if (hasInternetConnection(context)) {
            normalAlertsList.value = NetworkResponse.Loading()
            viewModelScope.launch {
                try {
                    val response = async { repository.getUserAlerts(context, "1") }
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
                    val response = async { repository.getUserAlerts(context, "2") }
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

    fun getUserOffers(context: Context, requestBody: OffersListReqModel): MutableLiveData<NetworkResponse<List<com.satrango.ui.user.user_dashboard.user_offers.models.Data>>> {
        if (hasInternetConnection(context)) {
            userOffers.value = NetworkResponse.Loading()
            viewModelScope.launch {
                try {
                    val response = async { repository.getUserOffers(requestBody) }
                    if (response.await().status == 200) {
                        userOffers.value = NetworkResponse.Success(response.await().data)
                    } else {
                        userOffers.value = NetworkResponse.Failure(response.await().message)
                    }
                } catch (e: Exception) {
                    userOffers.value = NetworkResponse.Failure(e.message)
                }

            }
        } else {
            userOffers.value = NetworkResponse.Failure("No Internet Connection")
        }
        return userOffers
    }

    fun updateAlertsToRead(context: Context): MutableLiveData<NetworkResponse<String>> {
        if (hasInternetConnection(context)) {
            userOffers.value = NetworkResponse.Loading()
            viewModelScope.launch {
                try {
                    val response = async { repository.updateAlertsToRead(context) }
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
        } else {
            updateAlertsToRead.value = NetworkResponse.Failure("No Internet Connection")
        }
        return updateAlertsToRead
    }

}