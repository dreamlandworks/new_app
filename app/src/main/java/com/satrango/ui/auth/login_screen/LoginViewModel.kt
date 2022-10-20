package com.satrango.ui.auth.login_screen

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.satrango.remote.NetworkResponse
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.auth.user_signup.models.UserLoginModel
import com.satrango.utils.UserUtils
import com.satrango.utils.hasInternetConnection
import kotlinx.coroutines.*
import org.json.JSONObject
import java.lang.Exception

class LoginViewModel(private val repository: LoginRepository): ViewModel() {

    val userLogin = MutableLiveData<NetworkResponse<String>>()
    val userLogout = MutableLiveData<NetworkResponse<String>>()

    fun userLogin(context: Context, requestBody: UserLoginModel): MutableLiveData<NetworkResponse<String>> {
//        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                try {
                    userLogin.value = NetworkResponse.Loading()
                    val response = async { repository.login(requestBody) }
                    val jsonObject = JSONObject(response.await().string())
//                    Log.e("LOGIN", jsonObject.toString())
                    if (jsonObject.getInt("status") == 200) {
                        UserUtils.saveUserName(context, jsonObject.getString("fname") + " " + jsonObject.getString("lname"))
                        UserUtils.saveUserProfilePic(context, jsonObject.getString("profile_image"))
                        userLogin.value = NetworkResponse.Success(jsonObject.getString("user_id"))
                    } else {
                        userLogin.value = NetworkResponse.Failure(jsonObject.getInt("status").toString())
                    }
                } catch (e: Exception) {
                    userLogin.value = NetworkResponse.Failure(e.message)
                }
            }
//        }
        return userLogin
    }

    fun userLogout(context: Context, requestBody: LogoutReqModel): MutableLiveData<NetworkResponse<String>> {
//        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                try {
                    userLogout.value = NetworkResponse.Loading()
                    val response = async { repository.logout(requestBody) }
                    val jsonObject = JSONObject(response.await().string())
//                    Log.e("LOGOUT", jsonObject.toString())
                    if (jsonObject.getInt("status") == 200) {
                        userLogout.value = NetworkResponse.Success(jsonObject.getString("message"))
                    } else {
                        userLogout.value = NetworkResponse.Failure(jsonObject.getString("message"))
                    }
                } catch (e: Exception) {
                    userLogout.value = NetworkResponse.Failure(e.message)
                }
            }
//        }
        return userLogout
    }

}