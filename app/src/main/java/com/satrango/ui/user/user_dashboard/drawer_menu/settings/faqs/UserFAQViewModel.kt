package com.satrango.ui.user.user_dashboard.drawer_menu.settings.faqs

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.satrango.remote.NetworkResponse
import com.satrango.ui.user.user_dashboard.drawer_menu.settings.faqs.models.Data
import com.satrango.utils.hasInternetConnection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserFAQViewModel(private val repository: UserFAQRepository): ViewModel() {

    val faqsList = MutableLiveData<NetworkResponse<List<Data>>>()

    fun getFAQSList(context: Context): MutableLiveData<NetworkResponse<List<Data>>> {
        if (hasInternetConnection(context)) {
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val response = repository.getFAQs()
                    if (response.status == 200) {
                        faqsList.value = NetworkResponse.Success(response.data)
                    } else {
                        faqsList.value = NetworkResponse.Failure(response.message)
                    }
                } catch (e: Exception) {
                    faqsList.value = NetworkResponse.Failure(e.message)
                }
            }
        } else {
            faqsList.value = NetworkResponse.Failure("No Internet Connection!")
        }
        return faqsList
    }

}