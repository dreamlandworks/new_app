package com.satrango.ui.service_provider.provider_dashboard.provider_dashboard.my_bookings

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.satrango.remote.NetworkResponse
import com.satrango.ui.service_provider.provider_dashboard.provider_dashboard.my_bookings.models.BookingDetail
import com.satrango.ui.service_provider.provider_dashboard.provider_dashboard.my_bookings.models.ProviderBookingReqModel
import com.satrango.utils.hasInternetConnection
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.lang.Exception

class ProviderBookingViewModel(private val repository: ProviderBookingRepository): ViewModel() {

    val bookingListWithDetails = MutableLiveData<NetworkResponse<List<BookingDetail>>>()

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

}