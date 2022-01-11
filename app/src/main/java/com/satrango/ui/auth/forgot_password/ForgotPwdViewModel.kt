package com.satrango.ui.auth.forgot_password

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.satrango.remote.NetworkResponse
import com.satrango.utils.hasInternetConnection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.json.JSONObject

class ForgotPwdViewModel(private val repository: ForgotPwdRepository) : ViewModel() {

    val verifyUser = MutableLiveData<NetworkResponse<String>>()

    fun verifyUser(context: Context, requestBody: ForgotPwdVerifyReqModel): MutableLiveData<NetworkResponse<String>> {
//        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                try {
                    verifyUser.value = NetworkResponse.Loading()
                    val response = async { repository.verifyUser(requestBody) }
                    val jsonObject = JSONObject(response.await().string())
                    if (jsonObject.getInt("status") == 200) {
                        verifyUser.value = NetworkResponse.Success(jsonObject.getString("message"))
                    } else {
                        verifyUser.value = NetworkResponse.Failure(jsonObject.getString("message"))
                    }
                } catch (e: Exception) {
                    verifyUser.value = NetworkResponse.Failure(e.message)
                }
            }
//        } else {
//            verifyUser.value = NetworkResponse.Failure("No Internet Connection!")
//        }
        return verifyUser
    }

}