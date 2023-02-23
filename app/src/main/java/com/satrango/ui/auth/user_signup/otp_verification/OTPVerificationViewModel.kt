package com.satrango.ui.auth.user_signup.otp_verification

import android.content.Context
import android.graphics.LinearGradient
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.satrango.remote.NetworkResponse
import com.satrango.utils.hasInternetConnection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.json.JSONObject

class OTPVerificationViewModel(private val repository: OTPVerificationRepository) : ViewModel() {

    val forgotPwdRequestOTP = MutableLiveData<NetworkResponse<String>>()
    val requestOTP = MutableLiveData<NetworkResponse<String>>()


    fun forgotPwdRequestOTP(context: Context): MutableLiveData<NetworkResponse<String>> {
//        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                try {
                    forgotPwdRequestOTP.value = NetworkResponse.Loading()
                    val response = async { repository.forgotPwdRequestOTP(context) }
                    val jsonObject = JSONObject(response.await().string())
                    Log.e("FORGOT RESPONSE:", jsonObject.toString())
//                    Toast.makeText(context, jsonObject.toString(), Toast.LENGTH_SHORT).show()
                    if (jsonObject.getInt("status") == 200) {
                        forgotPwdRequestOTP.value = NetworkResponse.Success(jsonObject.getString("id") + "|" + jsonObject.getInt("otp"))
                    } else {
                        forgotPwdRequestOTP.value = NetworkResponse.Failure(jsonObject.getString("message"))
                    }
                } catch (e: Exception) {
                    forgotPwdRequestOTP.value = NetworkResponse.Failure(e.message)
                }

            }
//        }
        return forgotPwdRequestOTP
    }

    fun requestOTP(context: Context): MutableLiveData<NetworkResponse<String>> {
//        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                try {
                    requestOTP.value = NetworkResponse.Loading()
                    val response = async { repository.requestOTP(context) }
                    val jsonObject = JSONObject(response.await().string())
//                    Log.e("OTP RESPONSE:", jsonObject.toString())
//                    Toast.makeText(context, jsonObject.toString(), Toast.LENGTH_SHORT).show()
                    if (jsonObject.getInt("status") == 200) {
                        requestOTP.value = NetworkResponse.Success(jsonObject.getInt("otp").toString())
                    } else {
                        requestOTP.value = NetworkResponse.Failure(jsonObject.getString("message"))
                    }
                } catch (e: Exception) {
                    requestOTP.value = NetworkResponse.Failure(e.message)
                }
            }
//        }
        return requestOTP
    }

}