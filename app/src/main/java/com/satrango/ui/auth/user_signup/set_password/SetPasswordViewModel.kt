package com.satrango.ui.auth.user_signup.set_password

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.satrango.remote.NetworkResponse
import com.satrango.ui.auth.user_signup.models.UserSignUpModel
import com.satrango.utils.hasInternetConnection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.json.JSONObject

class SetPasswordViewModel(private val repository: SetPasswordRepository) : ViewModel() {

    val resetPassword = MutableLiveData<NetworkResponse<String>>()
    val createNewUser = MutableLiveData<NetworkResponse<String>>()

    fun resetPassword(context: Context): MutableLiveData<NetworkResponse<String>> {
//        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                try {
                    resetPassword.value = NetworkResponse.Loading()
                    val response = async { repository.resetPasswordInServer(context) }
                    val jsonObject = JSONObject(response.await().string())
                    if (jsonObject.get("status") == 200) {
                        resetPassword.value = NetworkResponse.Success(jsonObject.getString("message"))
                    } else {
                        resetPassword.value = NetworkResponse.Failure(jsonObject.getString("message"))
                    }
                } catch (e: Exception) {
                    resetPassword.value = NetworkResponse.Failure(e.message)
                }
            }
//        }
        return resetPassword
    }

    fun createNewUser(context: Context, requestBody: UserSignUpModel): MutableLiveData<NetworkResponse<String>> {
//        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                try {
                    createNewUser.value = NetworkResponse.Loading()
                    val response = async { repository.createNewUser(requestBody) }
                    val jsonObject = JSONObject(response.await().string())
                    Log.e("NEW USER RESPONSE:", jsonObject.toString())
                    if (jsonObject.getInt("status") == 200) {
                        createNewUser.value =
                            NetworkResponse.Success(jsonObject.getString("referral_id"))
                    } else {
                        createNewUser.value =
                            NetworkResponse.Failure(jsonObject.getString("message"))
                    }
                } catch (e: java.lang.Exception) {
                    createNewUser.value = NetworkResponse.Failure(e.message)
                }
            }
//        } else {
//            createNewUser.value = NetworkResponse.Failure("No Internet Connection")
//        }

        return createNewUser
    }

}