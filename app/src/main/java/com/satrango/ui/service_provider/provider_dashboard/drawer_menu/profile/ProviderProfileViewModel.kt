package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.profile

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.satrango.remote.NetworkResponse
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.models.ProviderBookingReqModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.profile.models.ProviderProfileProfessionResModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.profile.models.update_skills.UpdateSkillsReqModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.profile.models.update_tariff.UpdateTariffReqModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.json.JSONObject

class ProviderProfileViewModel(private val repository: ProviderProfileRepository) : ViewModel() {

    val professionalDetails = MutableLiveData<NetworkResponse<ProviderProfileProfessionResModel>>()
    val updateSkills = MutableLiveData<NetworkResponse<ResponseBody>>()
    val updateTariff = MutableLiveData<NetworkResponse<ResponseBody>>()

//    fun professionalDetails(
//        context: Context,
//        requestBody: ProviderBookingReqModel
//    ): MutableLiveData<NetworkResponse<ProviderProfileProfessionResModel>> {
////        if (hasInternetConnection(context)) {
//        viewModelScope.launch {
////                try {
//            professionalDetails.value = NetworkResponse.Loading()
//            val request = async { repository.getProfessionalDetails(requestBody) }
//            val response = request.await()
//            Toast.makeText(context, Gson().toJson(response), Toast.LENGTH_SHORT).show()
//            if (response.status == 200) {
//                professionalDetails.value = NetworkResponse.Success(response)
//            } else {
//                professionalDetails.value = NetworkResponse.Failure(response.message)
//            }
////                } catch (e: Exception) {
////                    professionalDetails.value = NetworkResponse.Failure(e.message)
////                }
//        }
////        } else {
////            professionalDetails.value = NetworkResponse.Failure("No Internet Connection")
////        }
//        return professionalDetails
//    }

    fun updateSkills(
        context: Context,
        requestBody: UpdateSkillsReqModel
    ): MutableLiveData<NetworkResponse<ResponseBody>> {
//        if (hasInternetConnection(context)) {
        viewModelScope.launch {
            try {
                updateSkills.value = NetworkResponse.Loading()
                val request = async { repository.updateSkills(requestBody) }
                val response = request.await()
                val jsonObject = JSONObject(response.string())
//                    Log.e("JSON", jsonObject.toString())
                if (jsonObject.getInt("status") == 200) {
                    updateSkills.value = NetworkResponse.Success(response)
                } else {
                    updateSkills.value = NetworkResponse.Failure(jsonObject.getString("message"))
                }
            } catch (e: Exception) {
                updateSkills.value = NetworkResponse.Failure(e.message)
            }
        }
//        } else {
//            updateSkills.value = NetworkResponse.Failure("No Internet Connection")
//        }
        return updateSkills
    }

    fun updateTariff(
        context: Context,
        requestBody: UpdateTariffReqModel
    ): MutableLiveData<NetworkResponse<ResponseBody>> {
//        if (hasInternetConnection(context)) {
        viewModelScope.launch {
            try {
                updateTariff.value = NetworkResponse.Loading()
                val request = async { repository.updateTariff(requestBody) }
                val response = request.await()
                val jsonObject = JSONObject(response.string())
                if (jsonObject.getInt("status") == 200) {
                    updateTariff.value = NetworkResponse.Success(response)
                } else {
                    updateTariff.value = NetworkResponse.Failure(jsonObject.getString("message"))
                }
            } catch (e: Exception) {
                updateTariff.value = NetworkResponse.Failure(e.message)
            }
        }
//        } else {
//            updateTariff.value = NetworkResponse.Failure("No Internet Connection")
//        }
        return updateTariff
    }

}