package com.satrango.ui.service_provider.provider_dashboard.provider_dashboard.my_bids

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.satrango.remote.NetworkResponse
import com.satrango.ui.service_provider.provider_dashboard.provider_dashboard.my_bids.models.JobPostDetail
import com.satrango.ui.service_provider.provider_dashboard.provider_dashboard.my_bids.place_bid.models.ProviderBidEditReqModel
import com.satrango.ui.service_provider.provider_dashboard.provider_dashboard.my_bids.place_bid.models.ProviderDeleteBidAttachmentReqModel
import com.satrango.ui.service_provider.provider_dashboard.provider_dashboard.my_bookings.models.ProviderBookingReqModel
import com.satrango.ui.service_provider.provider_dashboard.provider_dashboard.my_bids.place_bid.models.ProviderPostBidReqModel
import com.satrango.ui.service_provider.provider_dashboard.provider_dashboard.my_bids.place_bid.models.ProviderPostBidResModel
import com.satrango.utils.UserUtils
import com.satrango.utils.hasInternetConnection
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.ResponseBody

class ProviderMyBidsViewModel(private val repository: ProviderMyBidsRepository) : ViewModel() {

    val bidsList = MutableLiveData<NetworkResponse<List<JobPostDetail>>>()
    val jobsList = MutableLiveData<NetworkResponse<List<JobPostDetail>>>()
    val postBid = MutableLiveData<NetworkResponse<ProviderPostBidResModel>>()
    val deleteBidAttachment = MutableLiveData<NetworkResponse<ResponseBody>>()
    val editBid = MutableLiveData<NetworkResponse<ResponseBody>>()

    fun bidsList(
        context: Context,
        requestBody: ProviderBookingReqModel
    ): MutableLiveData<NetworkResponse<List<JobPostDetail>>> {
        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                try {
                    bidsList.value = NetworkResponse.Loading()
                    val request = async { repository.getBidsList(requestBody) }
                    val response = request.await()
                    if (response.status == 200) {
                        bidsList.value = NetworkResponse.Success(response.job_post_details)
                    } else {
                        bidsList.value = NetworkResponse.Failure(response.message)
                    }
                } catch (e: Exception) {
                    bidsList.value = NetworkResponse.Failure(e.message!!)
                }
            }
        } else {
            bidsList.value = NetworkResponse.Failure("No Internet Connection!")
        }
        return bidsList
    }

    fun jobsList(
        context: Context,
        requestBody: ProviderBookingReqModel
    ): MutableLiveData<NetworkResponse<List<JobPostDetail>>> {
        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                try {
                    jobsList.value = NetworkResponse.Loading()
                    val request = async { repository.getBidJobsList(requestBody) }
                    val response = request.await()
                    if (response.status == 200) {
                        UserUtils.saveSelectedSPDetails(context, Gson().toJson(response))
                        jobsList.value = NetworkResponse.Success(response.job_post_details)
                    } else {
                        jobsList.value = NetworkResponse.Failure(response.message)
                    }
                } catch (e: Exception) {
                    jobsList.value = NetworkResponse.Failure(e.message!!)
                }
            }
        } else {
            jobsList.value = NetworkResponse.Failure("No Internet Connection!")
        }
        return jobsList
    }

    fun postBid(
        context: Context,
        requestBody: ProviderPostBidReqModel
    ): MutableLiveData<NetworkResponse<ProviderPostBidResModel>> {
        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                try {
                    postBid.value = NetworkResponse.Loading()
                    val request = async { repository.postBid(requestBody) }
                    val response = request.await()
                    if (response.status == 200) {
                        postBid.value = NetworkResponse.Success(response)
                    } else {
                        postBid.value = NetworkResponse.Failure(response.message)
                    }
                } catch (e: Exception) {
                    postBid.value = NetworkResponse.Failure(e.message!!)
                }
            }
        } else {
            postBid.value = NetworkResponse.Failure("No Internet Connection!")
        }
        return postBid
    }

    fun deleteBidAttachment(
        context: Context,
        requestBody: ProviderDeleteBidAttachmentReqModel
    ): MutableLiveData<NetworkResponse<ResponseBody>> {
        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                try {
                    deleteBidAttachment.value = NetworkResponse.Loading()
                    val request = async { repository.deleteBidAttachment(requestBody) }
                    val response = request.await()
                    deleteBidAttachment.value = NetworkResponse.Success(response)
                } catch (e: Exception) {
                    deleteBidAttachment.value = NetworkResponse.Failure(e.message!!)
                }
            }
        } else {
            deleteBidAttachment.value = NetworkResponse.Failure("No Internet Connection!")
        }
        return deleteBidAttachment
    }

    fun editBid(
        context: Context,
        requestBody: ProviderBidEditReqModel
    ): MutableLiveData<NetworkResponse<ResponseBody>> {
        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                try {
                    editBid.value = NetworkResponse.Loading()
                    val request = async { repository.editBids(requestBody) }
                    val response = request.await()
                    editBid.value = NetworkResponse.Success(response)
                } catch (e: Exception) {
                    editBid.value = NetworkResponse.Failure(e.message!!)
                }
            }
        } else {
            editBid.value = NetworkResponse.Failure("No Internet Connection!")
        }
        return editBid
    }

}