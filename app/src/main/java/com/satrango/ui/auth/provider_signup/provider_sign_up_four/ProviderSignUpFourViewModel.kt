package com.satrango.ui.auth.provider_signup.provider_sign_up_four

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.satrango.remote.NetworkResponse
import com.satrango.ui.auth.provider_signup.provider_sign_up_four.models.ProviderSignUpFourReqModel
import com.satrango.utils.hasInternetConnection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import java.lang.Exception

class ProviderSignUpFourViewModel(private val repository: ProviderSignUpFourRepository): ViewModel() {

    val providerActivation = MutableLiveData<NetworkResponse<String>>()

    fun providerActivation(context: Context, requestBody: ProviderSignUpFourReqModel): MutableLiveData<NetworkResponse<String>> {
        if (hasInternetConnection(context)) {
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    providerActivation.value = NetworkResponse.Loading()
                    val response = repository.serviceProviderActivation(requestBody)
                    val res = response.string()
                    Log.e("ACTIVATION:", res)
                    providerActivation.value = NetworkResponse.Success(res)
                } catch (e: Exception) {
                    Log.e("ACTIVATION:", e.message!!)
                    providerActivation.value = NetworkResponse.Failure(e.message!!)
                }
            }
        } else {
            providerActivation.value = NetworkResponse.Failure("No Internet Connection!")
        }
        return providerActivation
    }


}