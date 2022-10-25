package com.satrango.ui.auth.provider_signup.provider_sign_up_one

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.satrango.remote.NetworkResponse
import com.satrango.ui.auth.provider_signup.provider_sign_up_one.models.Data
import com.satrango.ui.auth.provider_signup.provider_sign_up_one.models.ProviderOneModel
import com.satrango.utils.hasInternetConnection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.json.JSONObject

class ProviderSignUpOneViewModel(private val repository: ProviderSignUpOneRepository) :
    ViewModel() {

    var providersData =
        MutableLiveData<NetworkResponse<com.satrango.ui.auth.provider_signup.provider_sign_up_one.models.ProviderOneModel>>()

    fun professionsList(context: Context): MutableLiveData<NetworkResponse<ProviderOneModel>> {
//        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                try {
                    providersData.value = NetworkResponse.Loading()
                    val response = async { repository.providerData() }
                    val json = JSONObject(response.await().string())
                    val jsonData = json.getJSONObject("data")
                    val jsonStatus = json.getInt("status")
                    val jsonMessage = json.getString("message")
//                    Toast.makeText(context, jsonData.toString(), Toast.LENGTH_SHORT).show()
//                    for (index in 0..jsonData.length()) {
//                        val jsonIndex = jsonData.getJSONObject(index)
//                        Log.e("JSON:", Gson().toJson(jsonIndex))
//                    }
                    val data = Gson().fromJson(jsonData.toString(), Data::class.java)
//                    val data = Gson().fromJson(response.await().string(), ProviderOneModel::class.java)
                    if (jsonStatus == 200) {
//                        Toast.makeText(context, response.await().toString(), Toast.LENGTH_SHORT).show()
                        providersData.value = NetworkResponse.Success(ProviderOneModel(data, "Success", 200))
                    } else {
                        providersData.value = NetworkResponse.Failure(jsonMessage)
                    }
                } catch (e: Exception) {
                    providersData.value = NetworkResponse.Failure(e.message)
                }

            }
//        } else {
//            providersData.value = NetworkResponse.Failure("No Internet Connection!")
//        }
        return providersData
    }

}