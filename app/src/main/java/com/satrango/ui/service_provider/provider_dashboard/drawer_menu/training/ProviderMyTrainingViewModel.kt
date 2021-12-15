package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.training

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.satrango.remote.NetworkResponse
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.training.model.ProviderMyTrainingResModel
import com.satrango.utils.hasInternetConnection
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.json.JSONObject

class ProviderMyTrainingViewModel(private val repository: ProviderMyTrainingRepository): ViewModel() {

    val trainingList = MutableLiveData<NetworkResponse<ProviderMyTrainingResModel>>()
    val submitYoutubePoints = MutableLiveData<NetworkResponse<String>>()

    fun getTrainingList(
        context: Context
    ): MutableLiveData<NetworkResponse<ProviderMyTrainingResModel>> {
        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                trainingList.value = NetworkResponse.Loading()
                val response = async { repository.getTrainingVideos(context) }
                val responseJson = response.await()
                Log.e("TRAINING:", Gson().toJson(responseJson))
                if (responseJson.status == 200) {
                    trainingList.value = NetworkResponse.Success(responseJson)
                } else {
                    trainingList.value = NetworkResponse.Failure(responseJson.message)
                }
            }
        } else {
            trainingList.value = NetworkResponse.Failure("No Internet Connection!")
        }
        return trainingList
    }


    fun submitYoutubePoints(
        context: Context,
        videoId: String,
        points: String
    ): MutableLiveData<NetworkResponse<String>> {
        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                submitYoutubePoints.value = NetworkResponse.Loading()
                val response = async { repository.submitYoutubePoints(context, videoId, points) }
                val responseJson = JSONObject(response.await().string())
                if (responseJson.getInt("status") == 200) {
                    submitYoutubePoints.value = NetworkResponse.Success(responseJson.getString("message"))
                } else {
                    submitYoutubePoints.value = NetworkResponse.Failure(responseJson.getString("message"))
                }
            }
        } else {
            submitYoutubePoints.value = NetworkResponse.Failure("No Internet Connection!")
        }
        return submitYoutubePoints
    }

}