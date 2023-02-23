package com.satrango.ui.auth.provider_signup.provider_sign_up_four

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.satrango.remote.NetworkResponse
import com.satrango.ui.auth.provider_signup.provider_sign_up_four.models.ProviderIdProofReqModel
import com.satrango.ui.auth.provider_signup.provider_sign_up_four.models.ProviderSignUpFourReqModel
import com.satrango.utils.hasInternetConnection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import org.json.JSONObject
import java.lang.Exception

class ProviderSignUpFourViewModel(private val repository: ProviderSignUpFourRepository): ViewModel() {

    val providerActivation = MutableLiveData<NetworkResponse<String>>()
    val uploadIdProof = MutableLiveData<NetworkResponse<String>>()

    fun providerActivation(context: Context, requestBody: ProviderSignUpFourReqModel): MutableLiveData<NetworkResponse<String>> {
//        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                try {
                    providerActivation.value = NetworkResponse.Loading()
                    val response = async { repository.serviceProviderActivation(requestBody) }
                    val jsonResponse = JSONObject(response.await().string())
//                    Log.e("TIMESLOTS:", jsonResponse.toString())
                    if (jsonResponse.getInt("status") == 200) {
                        providerActivation.value = NetworkResponse.Success(jsonResponse.getString("message"))
                    } else {
                        providerActivation.value = NetworkResponse.Failure(jsonResponse.getString("message"))
                    }
//                    Log.e("ACTIVATION:", jsonResponse.toString())
                } catch (e: Exception) {
//                    Log.e("ACTIVATION:", e.message!!)
                    providerActivation.value = NetworkResponse.Failure(e.message!!)
                }
            }
//        } else {
//            providerActivation.value = NetworkResponse.Failure("No Internet Connection!")
//        }
        return providerActivation
    }

    fun uploadIdProof(requestBody: ProviderIdProofReqModel): MutableLiveData<NetworkResponse<String>> {
//        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                try {
                    uploadIdProof.value = NetworkResponse.Loading()
                    val response = async { repository.uploadIdProof(requestBody) }
                    val jsonResponse = JSONObject(response.await().string())
                    if (jsonResponse.getInt("status") == 200) {
                        uploadIdProof.value = NetworkResponse.Success(jsonResponse.getString("message"))
                    } else {
                        uploadIdProof.value = NetworkResponse.Failure(jsonResponse.getString("message"))
                    }
                } catch (e: Exception) {
                    uploadIdProof.value = NetworkResponse.Failure(e.message!!)
                }
            }
//        } else {
//            uploadIdProof.value = NetworkResponse.Failure("No Internet Connection!")
//        }
        return uploadIdProof
    }


}