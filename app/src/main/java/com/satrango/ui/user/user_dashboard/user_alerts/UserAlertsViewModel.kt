package com.satrango.ui.user.user_dashboard.user_alerts

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.satrango.remote.NetworkResponse
import com.satrango.ui.user.user_dashboard.user_alerts.models.Action
import com.satrango.ui.user.user_dashboard.user_alerts.models.Regular
import com.satrango.ui.user.user_dashboard.user_offers.models.OffersListReqModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.lang.Exception

class UserAlertsViewModel(private val repository: UserAlertsRepository): ViewModel() {

    val normalAlertsList = MutableLiveData<NetworkResponse<List<Regular>>>()
    val actionableAlertsList = MutableLiveData<NetworkResponse<List<Action>>>()
    val userOffers = MutableLiveData<NetworkResponse<List<com.satrango.ui.user.user_dashboard.user_offers.models.Data>>>()
    val updateAlertsToRead = MutableLiveData<NetworkResponse<String>>()

    fun getNormalAlerts(context: Context): MutableLiveData<NetworkResponse<List<Regular>>> {
//        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                try {
                    normalAlertsList.value = NetworkResponse.Loading()
                    val response = async { repository.getUserAlerts(context) }
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
                    val response = async { repository.getUserAlerts(context) }
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

    fun getUserOffers(context: Context, requestBody: OffersListReqModel): MutableLiveData<NetworkResponse<List<com.satrango.ui.user.user_dashboard.user_offers.models.Data>>> {
//        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                try {
                    userOffers.value = NetworkResponse.Loading()
                    val response = async { repository.getUserOffers(requestBody) }
//                    Log.e("OFFERS", Gson().toJson(response.await()))
                    if (response.await().status == 200) {
                        userOffers.value = NetworkResponse.Success(response.await().data)
                    } else {
                        userOffers.value = NetworkResponse.Failure(response.await().message)
                    }
                } catch (e: Exception) {
                    userOffers.value = NetworkResponse.Failure(e.message)
                }

            }
//        } else {
//            userOffers.value = NetworkResponse.Failure("No Internet Connection")
//        }
        return userOffers
    }

    fun updateAlertsToRead(context: Context, userType: String, lastAlertId: String): MutableLiveData<NetworkResponse<String>> {
//        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                try {
                    userOffers.value = NetworkResponse.Loading()
                    val response = async { repository.updateAlertsToRead(context, userType, lastAlertId) }
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