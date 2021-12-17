package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.training

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.satrango.remote.NetworkResponse
import com.satrango.ui.service_provider.provider_dashboard.dashboard.leaderboard.models.CitiesResModel
import com.satrango.ui.service_provider.provider_dashboard.dashboard.leaderboard.models.LeaderboardResModel
import com.satrango.ui.service_provider.provider_dashboard.dashboard.models.ProviderDashboardResModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.training.model.ProviderMyTrainingResModel
import com.satrango.utils.hasInternetConnection
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.json.JSONObject

class ProviderMyTrainingViewModel(private val repository: ProviderMyTrainingRepository): ViewModel() {

    val trainingList = MutableLiveData<NetworkResponse<ProviderMyTrainingResModel>>()
    val submitYoutubePoints = MutableLiveData<NetworkResponse<String>>()
    val leaderboardList = MutableLiveData<NetworkResponse<LeaderboardResModel>>()
    val citiesList = MutableLiveData<NetworkResponse<CitiesResModel>>()
    val providerDashboardDetails = MutableLiveData<NetworkResponse<ProviderDashboardResModel>>()

    fun getTrainingList(
        context: Context
    ): MutableLiveData<NetworkResponse<ProviderMyTrainingResModel>> {
        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                try {
                    trainingList.value = NetworkResponse.Loading()
                    val response = async { repository.getTrainingVideos(context) }
                    val responseJson = response.await()
                    Log.e("TRAINING:", Gson().toJson(responseJson))
                    if (responseJson.status == 200) {
                        trainingList.value = NetworkResponse.Success(responseJson)
                    } else {
                        trainingList.value = NetworkResponse.Failure(responseJson.message)
                    }
                } catch (e: Exception) {
                    trainingList.value = NetworkResponse.Failure(e.message)
                }

            }
        } else {
            trainingList.value = NetworkResponse.Failure("No Internet Connection!")
        }
        return trainingList
    }

    fun getLeaderboardList(
        context: Context,
        cityId: String
    ): MutableLiveData<NetworkResponse<LeaderboardResModel>> {
        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                try {
                    leaderboardList.value = NetworkResponse.Loading()
                    val response = async { repository.getLeaderboardList(context, cityId) }
                    val responseJson = response.await()
                    Log.e("LEADERBOARD:", Gson().toJson(responseJson))
                    if (responseJson.status == 200) {
                        leaderboardList.value = NetworkResponse.Success(responseJson)
                    } else {
                        leaderboardList.value = NetworkResponse.Failure(responseJson.message)
                    }
                } catch (e: Exception) {
                    leaderboardList.value = NetworkResponse.Failure(e.message)
                }
            }
        } else {
            leaderboardList.value = NetworkResponse.Failure("No Internet Connection!")
        }
        return leaderboardList
    }


    fun submitYoutubePoints(
        context: Context,
        videoId: String,
        points: String
    ): MutableLiveData<NetworkResponse<String>> {
        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                try {
                    submitYoutubePoints.value = NetworkResponse.Loading()
                    val response = async { repository.submitYoutubePoints(context, videoId, points) }
                    val responseJson = JSONObject(response.await().string())
                    if (responseJson.getInt("status") == 200) {
                        submitYoutubePoints.value = NetworkResponse.Success(responseJson.getString("message"))
                    } else {
                        submitYoutubePoints.value = NetworkResponse.Failure(responseJson.getString("message"))
                    }
                } catch (e: Exception) {
                    submitYoutubePoints.value = NetworkResponse.Failure(e.message)
                }


            }
        } else {
            submitYoutubePoints.value = NetworkResponse.Failure("No Internet Connection!")
        }
        return submitYoutubePoints
    }

    fun getCitiesList(
        context: Context
    ): MutableLiveData<NetworkResponse<CitiesResModel>> {
        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                citiesList.value = NetworkResponse.Loading()
                val response = async { repository.getCities() }
                val responseJson = response.await()
                if (responseJson.status == 200) {
                    citiesList.value = NetworkResponse.Success(responseJson)
                } else {
                    citiesList.value = NetworkResponse.Failure(responseJson.message)
                }
            }
        } else {
            citiesList.value = NetworkResponse.Failure("No Internet Connection!")
        }
        return citiesList
    }

    fun providerDashboardDetails(
        context: Context,
        cityId: String
    ): MutableLiveData<NetworkResponse<ProviderDashboardResModel>> {
        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                providerDashboardDetails.value = NetworkResponse.Loading()
                val response = async { repository.getProviderDashboardDetails(context, cityId) }
                val responseJson = response.await()
                if (responseJson.status == 200) {
                    providerDashboardDetails.value = NetworkResponse.Success(responseJson)
                } else {
                    providerDashboardDetails.value = NetworkResponse.Failure(responseJson.message)
                }
            }
        } else {
            providerDashboardDetails.value = NetworkResponse.Failure("No Internet Connection!")
        }
        return providerDashboardDetails
    }

}