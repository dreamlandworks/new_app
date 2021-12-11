package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.training

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.satrango.remote.NetworkResponse
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.training.model.ProviderMyTrainingResModel
import com.satrango.utils.hasInternetConnection
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class ProviderMyTrainingViewModel(private val repository: ProviderMyTrainingRepository): ViewModel() {

    val trainingList = MutableLiveData<NetworkResponse<ProviderMyTrainingResModel>>()

    fun getTrainingList(context: Context, subCategoryId: String): MutableLiveData<NetworkResponse<ProviderMyTrainingResModel>> {
        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                val response = async { repository.getTrainingVideos(context, subCategoryId) }
                val responseJson = response.await()
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

}