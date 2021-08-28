package com.satrango.ui.auth.provider_signup.provider_sign_up_five

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.satrango.remote.NetworkResponse
import com.satrango.remote.RetrofitBuilder
import com.satrango.utils.hasInternetConnection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import java.lang.Exception

class ProviderSignUpFiveViewModel(private val repository: ProviderSignUpFiveRepository): ViewModel() {

    val videoStatus = MutableLiveData<NetworkResponse<String>>()

    fun uploadVideo(context: Context, userId: RequestBody, videoNo: RequestBody, key: RequestBody, videoRecord: MultipartBody.Part): MutableLiveData<NetworkResponse<String>> {
        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                videoStatus.value = NetworkResponse.Loading()
                try {
                    val response = async { repository.uploadVideo(userId, videoNo, key, videoRecord) }
                    val jsonResponse = JSONObject(response.await().string())
                    if (jsonResponse.getInt("status") == 200) {
                        videoStatus.value = NetworkResponse.Success(jsonResponse.getString("message"))
                    } else {
                        videoStatus.value = NetworkResponse.Success(jsonResponse.getString("message"))
                    }
                } catch (e: Exception) {
                    videoStatus.value = NetworkResponse.Failure(e.message)
                }
            }
        } else {
            videoStatus.value = NetworkResponse.Failure("No Internet Connection!")
        }
        return videoStatus
    }
}