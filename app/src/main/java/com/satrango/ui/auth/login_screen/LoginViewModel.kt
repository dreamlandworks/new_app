package com.satrango.ui.auth.login_screen

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.satrango.remote.NetworkResponse
import com.satrango.ui.auth.user_signup.models.UserLoginModel
import com.satrango.utils.hasInternetConnection
import kotlinx.coroutines.*
import org.json.JSONObject
import java.lang.Exception

class LoginViewModel(private val repository: LoginRepository): ViewModel() {

    val userLogin = MutableLiveData<NetworkResponse<String>>()

    fun userLogin(context: Context, requestBody: UserLoginModel): MutableLiveData<NetworkResponse<String>> {
        if (hasInternetConnection(context)) {
            userLogin.value = NetworkResponse.Loading()
            viewModelScope.launch {
                try {
                    val response = async { repository.login(requestBody) }
                    val jsonObject = JSONObject(response.await().string())
                    Log.e("LOGIN", jsonObject.toString())
                    if (jsonObject.getInt("status") == 200) {
                        userLogin.value = NetworkResponse.Success(jsonObject.getString("user id"))
                    } else {
                        userLogin.value = NetworkResponse.Failure(jsonObject.getString("message"))
                    }
                } catch (e: Exception) {
                    userLogin.value = NetworkResponse.Failure(e.message)
                }
            }
        }
        return userLogin
    }

}