package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_account

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.satrango.remote.NetworkResponse
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_account.models.ProviderMyAccountResModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_account.reviews.models.ProviderReviewResModel
import com.satrango.utils.hasInternetConnection
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.lang.Exception

class ProviderMyAccountViewModel(private val repository: ProviderMyAccountRepository): ViewModel() {

    val myAccount = MutableLiveData<NetworkResponse<ProviderMyAccountResModel>>()
    val reviews = MutableLiveData<NetworkResponse<ProviderReviewResModel>>()

    fun myAccount(context: Context): MutableLiveData<NetworkResponse<ProviderMyAccountResModel>> {
//        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                try {
                    myAccount.value = NetworkResponse.Loading()
                    val request = async { repository.myAccount(context) }
                    val response = request.await()
                    if (response.status == 200) {
                        myAccount.value = NetworkResponse.Success(response)
                    } else {
                        myAccount.value = NetworkResponse.Failure(response.message)
                    }
                } catch (e: Exception) {
                    myAccount.value = NetworkResponse.Failure(e.message)
                }
            }
//        } else {
//            myAccount.value = NetworkResponse.Failure("No Internet Connection!")
//        }
        return myAccount
    }

    fun reviews(context: Context): MutableLiveData<NetworkResponse<ProviderReviewResModel>> {
//        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                try {
                    reviews.value = NetworkResponse.Loading()
                    val request = async { repository.reviews(context) }
                    val response = request.await()
                    if (response.status == 200) {
                        reviews.value = NetworkResponse.Success(response)
                    } else {
                        reviews.value = NetworkResponse.Failure(response.message)
                    }
                } catch (e: Exception) {
                    reviews.value = NetworkResponse.Failure(e.message)
                }
            }
//        } else {
//            reviews.value = NetworkResponse.Failure("No Internet Connection!")
//        }
        return reviews
    }

}