package com.satrango.ui.user.user_dashboard.drawer_menu.settings.faqs

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.satrango.remote.NetworkResponse
import com.satrango.ui.user.user_dashboard.drawer_menu.settings.faqs.models.Data
import com.satrango.utils.hasInternetConnection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class UserFAQViewModel(private val repository: UserFAQRepository): ViewModel() {

    val faqsList = MutableLiveData<NetworkResponse<List<Data>>>()

    fun getFAQSList(context: Context): MutableLiveData<NetworkResponse<List<Data>>> {
//        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                try {
                    val response = async { repository.getFAQs() }
                    if (response.await().status == 200) {
                        faqsList.value = NetworkResponse.Success(response.await().data)
                    } else {
                        faqsList.value = NetworkResponse.Failure(response.await().message)
                    }
                } catch (e: Exception) {
                    faqsList.value = NetworkResponse.Failure(e.message)
                }
            }
//        } else {
//            faqsList.value = NetworkResponse.Failure("No Internet Connection!")
//        }
        return faqsList
    }

}