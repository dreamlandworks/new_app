package com.satrango.ui.service_provider.provider_dashboard.provider_dashboard.my_bookings

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.satrango.remote.NetworkResponse
import com.satrango.ui.service_provider.provider_dashboard.provider_dashboard.my_bookings.models.BookingDetail
import com.satrango.ui.service_provider.provider_dashboard.provider_dashboard.my_bookings.models.ProviderBookingReqModel
import com.satrango.ui.service_provider.provider_dashboard.provider_dashboard.my_bookings.provider_booking_details.models.ExpenditureIncurredReqModel
import com.satrango.ui.service_provider.provider_dashboard.provider_dashboard.my_bookings.provider_booking_details.models.ExtraDemandReqModel
import com.satrango.utils.hasInternetConnection
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.lang.Exception

class ProviderBookingViewModel(private val repository: ProviderBookingRepository): ViewModel() {

    val bookingListWithDetails = MutableLiveData<NetworkResponse<List<BookingDetail>>>()
    val extraDemand = MutableLiveData<NetworkResponse<String>>()
    val expenditureIncurred = MutableLiveData<NetworkResponse<String>>()

    fun bookingListWithDetails(context: Context, requestBody: ProviderBookingReqModel): MutableLiveData<NetworkResponse<List<BookingDetail>>> {
        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                try {
                    bookingListWithDetails.value = NetworkResponse.Loading()
                    val request = async { repository.getBookingListWithDetails(requestBody) }
                    val response = request.await()
                    if (response.status == 200) {
                        bookingListWithDetails.value = NetworkResponse.Success(response.booking_details)
                    } else {
                        bookingListWithDetails.value = NetworkResponse.Failure(response.message)
                    }
                } catch (e: Exception) {

                }
            }
        } else {
            bookingListWithDetails.value = NetworkResponse.Failure("No Internet Connection")
        }
        return bookingListWithDetails
    }

    fun extraDemand(context: Context, requestBody: ExtraDemandReqModel): MutableLiveData<NetworkResponse<String>> {
        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                try {
                    extraDemand.value = NetworkResponse.Loading()
                    val request = async { repository.postExtraDemand(requestBody) }
                    val response = request.await()
                    val jsonResponse = JSONObject(response.string())
                    if (jsonResponse.getInt("status") == 200) {
                        extraDemand.value = NetworkResponse.Success(jsonResponse.getString("message"))
                    } else {
                        extraDemand.value = NetworkResponse.Failure(jsonResponse.getString("message"))
                    }
                } catch (e: Exception) {

                }
            }
        } else {
            extraDemand.value = NetworkResponse.Failure("No Internet Connection")
        }
        return extraDemand
    }

    fun expenditureIncurred(context: Context, requestBody: ExpenditureIncurredReqModel): MutableLiveData<NetworkResponse<String>> {
        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                try {
                    expenditureIncurred.value = NetworkResponse.Loading()
                    val request = async { repository.expenditureIncurred(requestBody) }
                    val response = request.await()
                    val jsonResponse = JSONObject(response.string())
                    if (jsonResponse.getInt("status") == 200) {
                        expenditureIncurred.value = NetworkResponse.Success(jsonResponse.getString("message"))
                    } else {
                        expenditureIncurred.value = NetworkResponse.Failure(jsonResponse.getString("message"))
                    }
                } catch (e: Exception) {

                }
            }
        } else {
            expenditureIncurred.value = NetworkResponse.Failure("No Internet Connection")
        }
        return expenditureIncurred
    }

}