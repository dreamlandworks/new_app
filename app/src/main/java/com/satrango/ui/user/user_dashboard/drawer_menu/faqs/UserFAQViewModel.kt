package com.satrango.ui.user.user_dashboard.drawer_menu.faqs

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.satrango.base.BaseRepository
import com.satrango.remote.NetworkResponse
import com.satrango.utils.hasInternetConnection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserFAQViewModel(private val repository: UserFAQRepository): ViewModel() {

    val faqsList = MutableLiveData<NetworkResponse<List<com.satrango.ui.user.user_dashboard.drawer_menu.faqs.models.Data>>>()

    fun getFAQSList(context: Context): MutableLiveData<NetworkResponse<List<com.satrango.ui.user.user_dashboard.drawer_menu.faqs.models.Data>>> {
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