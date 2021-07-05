package com.satrango.ui.auth.user_signup.otp_verification

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.satrango.remote.NetworkResponse
import com.satrango.utils.hasInternetConnection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.lang.Exception

class OTPVerificationViewModel(private val repository: OTPVerificationRepository): ViewModel() {

    val forgotPwdRequestOTP = MutableLiveData<NetworkResponse<String>>()
    val requestOTP = MutableLiveData<NetworkResponse<String>>()


    fun forgotPwdRequestOTP(context: Context) : MutableLiveData<NetworkResponse<String>> {
        if (hasInternetConnection(context)) {
            try {
                forgotPwdRequestOTP.value = NetworkResponse.Loading()
                CoroutineScope(Dispatchers.Main).launch {
                    val response = repository.forgotPwdRequestOTP()
                    val jsonObject = JSONObject(response.string())
                    if (jsonObject.getInt("status") == 200) {
                        forgotPwdRequestOTP.value = NetworkResponse.Success(jsonObject.getString("id") + "|" + jsonObject.getInt("otp"))
                    } else {
                        forgotPwdRequestOTP.value = NetworkResponse.Failure(jsonObject.getString("message"))
                    }
                }
            } catch (e: Exception) {
                forgotPwdRequestOTP.value = NetworkResponse.Failure(e.message)
            }
        }
        return forgotPwdRequestOTP
    }

    fun requestOTP(context: Context) : MutableLiveData<NetworkResponse<String>> {
        if (hasInternetConnection(context)) {
            try {
                requestOTP.value = NetworkResponse.Loading()
                CoroutineScope(Dispatchers.Main).launch {
                    val response = repository.requestOTP()
                    val jsonObject = JSONObject(response.string())
                    if (jsonObject.getInt("status") == 200) {
                        requestOTP.value = NetworkResponse.Success(jsonObject.getInt("otp").toString())
                    } else {
                        requestOTP.value = NetworkResponse.Failure(jsonObject.getString("message"))
                    }
                }
            } catch (e: Exception) {
                requestOTP.value = NetworkResponse.Failure(e.message)
            }
        }
        return requestOTP
    }

}