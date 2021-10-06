package com.satrango.ui.user.user_dashboard.drawer_menu.settings

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.satrango.remote.NetworkResponse
import com.satrango.ui.user.user_dashboard.drawer_menu.settings.complaints.models.ComplaintReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.settings.complaints.models.ComplaintResModel
import com.satrango.ui.user.user_dashboard.drawer_menu.settings.complaints.models.Data
import com.satrango.ui.user.user_dashboard.drawer_menu.settings.feedback.models.FeedbackReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.settings.feedback.models.FeedbackResModel
import com.satrango.ui.user.user_dashboard.drawer_menu.settings.requests.models.ComplaintRequestResModel
import com.satrango.utils.hasInternetConnection
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.lang.Exception

class SettingsViewModel(private val repository: SettingsRepository) : ViewModel() {

    val complaintModules = MutableLiveData<NetworkResponse<List<Data>>>()
    val postComplaint = MutableLiveData<NetworkResponse<ComplaintResModel>>()
    val postFeedback = MutableLiveData<NetworkResponse<FeedbackResModel>>()
    val complaintRequests = MutableLiveData<NetworkResponse<ComplaintRequestResModel>>()

    fun complaintModules(context: Context): MutableLiveData<NetworkResponse<List<Data>>> {
        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                try {
                    complaintModules.value = NetworkResponse.Loading()
                    val response = async { repository.getComplaintModules() }
                    val data = response.await()
                    if (data.status == 200) {
                        complaintModules.value = NetworkResponse.Success(data.data)
                    } else {
                        complaintModules.value = NetworkResponse.Failure(data.message)
                    }
                } catch (e: Exception) {
                    complaintModules.value = NetworkResponse.Failure(e.message)
                }
            }
        } else {
            complaintModules.value = NetworkResponse.Failure("No Internet Connection")
        }
        return complaintModules
    }

    fun postComplaint(context: Context, requestBody: ComplaintReqModel): MutableLiveData<NetworkResponse<ComplaintResModel>> {
        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                try {
                    postComplaint.value = NetworkResponse.Loading()
                    val response = async { repository.postComplaint(requestBody) }
                    val data = response.await()
                    if (data.status == 200) {
                        postComplaint.value = NetworkResponse.Success(data)
                    } else {
                        postComplaint.value = NetworkResponse.Failure(data.message)
                    }
                } catch (e: Exception) {
                    postComplaint.value = NetworkResponse.Failure(e.message)
                }
            }
        } else {
            postComplaint.value = NetworkResponse.Failure("No Internet Connection")
        }
        return postComplaint
    }

    fun postFeedback(context: Context, requestBody: FeedbackReqModel): MutableLiveData<NetworkResponse<FeedbackResModel>> {
        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                try {
                    postFeedback.value = NetworkResponse.Loading()
                    val response = async { repository.postFeedback(requestBody) }
                    val data = response.await()
                    if (data.status == 200) {
                        postFeedback.value = NetworkResponse.Success(data)
                    } else {
                        postFeedback.value = NetworkResponse.Failure(data.message)
                    }
                } catch (e: Exception) {
                    postFeedback.value = NetworkResponse.Failure(e.message)
                }
            }
        } else {
            postFeedback.value = NetworkResponse.Failure("No Internet Connection")
        }
        return postFeedback
    }

    fun complaintRequests(context: Context): MutableLiveData<NetworkResponse<ComplaintRequestResModel>> {
        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                try {
                    complaintRequests.value = NetworkResponse.Loading()
                    val response = async { repository.complaintRequests(context) }
                    val data = response.await()
                    if (data.status == 200) {
                        complaintRequests.value = NetworkResponse.Success(data)
                    } else {
                        complaintRequests.value = NetworkResponse.Failure(data.message)
                    }
                } catch (e: Exception) {
                    complaintRequests.value = NetworkResponse.Failure(e.message)
                }
            }
        } else {
            complaintRequests.value = NetworkResponse.Failure("No Internet Connection")
        }
        return complaintRequests
    }

}