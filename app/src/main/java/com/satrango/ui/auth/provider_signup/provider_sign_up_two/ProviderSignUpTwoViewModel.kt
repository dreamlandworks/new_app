package com.satrango.ui.auth.provider_signup.provider_sign_up_two

import android.content.Context
import android.util.Log
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
import java.lang.Exception

class ProviderSignUpTwoViewModel(private val repository: ProviderSignUpTwoRepository): ViewModel() {

    val keywords = MutableLiveData<NetworkResponse<List<com.satrango.ui.auth.provider_signup.provider_sign_up_two.models.Data>>>()

    fun getKeywords(context: Context, subCatId: String): MutableLiveData<NetworkResponse<List<com.satrango.ui.auth.provider_signup.provider_sign_up_two.models.Data>>> {
//        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                try {
                    keywords.value = NetworkResponse.Loading()
                    val response = async { repository.getKeyWords(subCatId) }
                    if (response.await().status == 200) {
                        keywords.value = NetworkResponse.Success(response.await().data)
                    } else {
                        keywords.value = NetworkResponse.Failure(response.await().message)
                    }
                } catch (e: Exception) {
                    keywords.value = NetworkResponse.Failure(e.message!!)
                }
            }
//        } else {
//            keywords.value = NetworkResponse.Failure("No Internet Connection!")
//        }
        return keywords
    }

}