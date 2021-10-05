package com.satrango.ui.service_provider.provider_dashboard.plans

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.satrango.remote.NetworkResponse
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_account.models.ProviderMemberShipPlanPaymentReqModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_account.models.ProviderMemberShipPlanPaymentResModel
import com.satrango.ui.service_provider.provider_dashboard.plans.models.ProviderPlansResModel
import com.satrango.utils.hasInternetConnection
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.lang.Exception

class ProviderPlansViewModel(private val repository: ProviderPlansRepository): ViewModel() {

    val plans = MutableLiveData<NetworkResponse<ProviderPlansResModel>>()
    val saveMemberShip = MutableLiveData<NetworkResponse<ProviderMemberShipPlanPaymentResModel>>()

    fun plans(context: Context): MutableLiveData<NetworkResponse<ProviderPlansResModel>> {
        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                try {
                    val request = async { repository.getPlans(context) }
                    val response = request.await()
                    if (response.status == 200) {
                        plans.value = NetworkResponse.Success(response)
                    } else {
                        plans.value = NetworkResponse.Failure(response.message)
                    }
                } catch (e: Exception) {
                    plans.value = NetworkResponse.Failure(e.message)
                }
            }
        } else {
            plans.value = NetworkResponse.Failure("No Internet Connection")
        }
        return plans
    }

    fun saveMemberShip(context: Context, requestBody: ProviderMemberShipPlanPaymentReqModel): MutableLiveData<NetworkResponse<ProviderMemberShipPlanPaymentResModel>> {
        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                try {
                    val request = async { repository.saveMemberShip(requestBody) }
                    val response = request.await()
                    if (response.status == 200) {
                        saveMemberShip.value = NetworkResponse.Success(response)
                    } else {
                        saveMemberShip.value = NetworkResponse.Failure(response.message)
                    }
                } catch (e: Exception) {
                    saveMemberShip.value = NetworkResponse.Failure(e.message)
                }
            }
        } else {
            saveMemberShip.value = NetworkResponse.Failure("No Internet Connection")
        }
        return saveMemberShip
    }

}