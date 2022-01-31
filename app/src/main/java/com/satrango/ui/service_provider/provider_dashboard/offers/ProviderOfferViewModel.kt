package com.satrango.ui.service_provider.provider_dashboard.offers

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.satrango.remote.NetworkResponse
import com.satrango.ui.user.user_dashboard.user_alerts.UserAlertsRepository
import com.satrango.ui.user.user_dashboard.user_offers.models.Data
import com.satrango.ui.user.user_dashboard.user_offers.models.OffersListReqModel
import com.satrango.utils.hasInternetConnection
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.lang.Exception

class ProviderOfferViewModel(private val repository: ProviderOfferRepository): ViewModel() {

    val offersList = MutableLiveData<NetworkResponse<List<Data>>>()
    val userOfferRepository = UserAlertsRepository()

    fun getOffers(context: Context, requestBody: OffersListReqModel): MutableLiveData<NetworkResponse<List<Data>>> {
//        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                try {
                    offersList.value = NetworkResponse.Loading()
                    val request = async { userOfferRepository.getUserOffers(requestBody) }
                    val response = request.await()
                    if (response.status == 200) {
                        offersList.value = NetworkResponse.Success(response.data)
                    } else {
                        offersList.value = NetworkResponse.Failure(response.message)
                    }
                } catch (e: Exception) {
                    offersList.value = NetworkResponse.Failure(e.message)
                }
            }
//        }
        return offersList
    }

}