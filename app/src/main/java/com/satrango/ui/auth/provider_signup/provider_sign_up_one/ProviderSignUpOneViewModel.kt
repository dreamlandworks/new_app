package com.satrango.ui.auth.provider_signup.provider_sign_up_one

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.satrango.remote.NetworkResponse
import com.satrango.utils.hasInternetConnection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProviderSignUpOneViewModel(private val repository: ProviderSignUpOneRepository) :
    ViewModel() {

    var providersData =
        MutableLiveData<NetworkResponse<com.satrango.ui.auth.provider_signup.provider_sign_up_one.models.ProviderOneModel>>()

    fun professionsList(context: Context): MutableLiveData<NetworkResponse<com.satrango.ui.auth.provider_signup.provider_sign_up_one.models.ProviderOneModel>> {
        if (hasInternetConnection(context)) {
            CoroutineScope(Dispatchers.Main).launch {
                providersData.value = NetworkResponse.Loading()
                try {
                    val response = repository.providerData()
                    if (response.status == 200) {
                        providersData.value = NetworkResponse.Success(response)
                    } else {
                        providersData.value = NetworkResponse.Failure(response.message)
                    }
                } catch (e: Exception) {
                    providersData.value = NetworkResponse.Failure(e.message)
                }

            }
        } else {
            providersData.value = NetworkResponse.Failure("No Internet Connection!")
        }
        return providersData
    }

}