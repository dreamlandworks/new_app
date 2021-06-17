package com.satrango.ui.user_dashboard.user_home_screen

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.user_dashboard.drawer_menu.browse_categories.models.BrowserSubCategoryModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserHomeViewModel(private val userHomeRepository: UserHomeRepository): ViewModel() {

    val popularServices = MutableLiveData<List<BrowserSubCategoryModel>>()

    fun getPopularServicesList() : MutableLiveData<List<BrowserSubCategoryModel>> {
        CoroutineScope(Dispatchers.Main).launch {
            popularServices.value = userHomeRepository.getPopularServices()
        }
        return popularServices
    }

}