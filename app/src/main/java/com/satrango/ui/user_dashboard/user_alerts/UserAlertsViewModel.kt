package com.satrango.ui.user_dashboard.user_alerts

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.satrango.ui.user_dashboard.user_alerts.models.Data
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserAlertsViewModel(private val repository: UserAlertsRepository): ViewModel() {

    val normalAlertsList = MutableLiveData<List<Data>>()
    val actionableAlertsList = MutableLiveData<List<Data>>()

    fun getNormalAlerts(): MutableLiveData<List<Data>> {
        CoroutineScope(Dispatchers.Main).launch {
            normalAlertsList.value = repository.getUserAlerts("1")
        }
        return normalAlertsList
    }

    fun getActionableAlerts(): MutableLiveData<List<Data>> {
        CoroutineScope(Dispatchers.Main).launch {
            actionableAlertsList.value = repository.getUserAlerts("2")
        }
        return actionableAlertsList
    }

}