package com.satrango.ui.user.user_dashboard.drawer_menu.my_bookings

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.satrango.remote.NetworkResponse
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.models.ProviderBookingResumeReqModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.models.ProviderPauseBookingReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_bookings.models.BookingDetail
import com.satrango.ui.user.user_dashboard.drawer_menu.my_bookings.models.MyBookingsReqModel
import com.satrango.utils.hasInternetConnection
import kotlinx.coroutines.*
import org.json.JSONObject
import java.lang.Exception

class MyBookingsViewModel(private val repository: MyBookingsRepository): ViewModel() {

    var myBookings = MutableLiveData<NetworkResponse<List<BookingDetail>>>()
    var otpRequest = MutableLiveData<NetworkResponse<Int>>()
    var validateOTP = MutableLiveData<NetworkResponse<Int>>()
    var resumeBooking = MutableLiveData<NetworkResponse<String>>()
    var pauseBooking = MutableLiveData<NetworkResponse<String>>()

    fun getMyBookingDetails(context: Context, requestBody: MyBookingsReqModel): MutableLiveData<NetworkResponse<List<BookingDetail>>> {
        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                try {
                    myBookings.value = NetworkResponse.Loading()
                    val result = async { repository.getMyBookings(requestBody) }
                    val response = result.await()
                    if (response.status == 200) {
                        myBookings.value = NetworkResponse.Success(response.booking_details)
                    } else {
                        myBookings.value = NetworkResponse.Failure(response.message)
                    }
                } catch (e: Exception) {
                    myBookings.value = NetworkResponse.Failure(e.message)
                }
            }
        } else {
            myBookings.value = NetworkResponse.Failure("No Internet Connection!")
        }
        return myBookings
    }

    fun otpRequest(context: Context, bookingId: Int, userType: String): MutableLiveData<NetworkResponse<Int>> {
        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                try {
                    otpRequest.value = NetworkResponse.Loading()
                    val result = async { repository.generateOTP(bookingId, userType) }
                    val response = JSONObject(result.await().string())
                    if (response.getInt("status") == 200) {
                        otpRequest.value = NetworkResponse.Success(response.getInt("otp"))
                    } else {
                        otpRequest.value = NetworkResponse.Failure(response.getString("message"))
                    }
                } catch (e: Exception) {
                    otpRequest.value = NetworkResponse.Failure(e.message)
                }
            }
        } else {
            otpRequest.value = NetworkResponse.Failure("No Internet Connection!")
        }
        return otpRequest
    }

    fun validateOTP(context: Context, bookingId: Int, spId: Int): MutableLiveData<NetworkResponse<Int>> {
        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                try {
                    validateOTP.value = NetworkResponse.Loading()
                    val result = async { repository.validateOTP(bookingId, spId) }
                    val response = JSONObject(result.await().string())
                    if (response.getInt("status") == 200) {
                        validateOTP.value = NetworkResponse.Success(response.getInt("otp"))
                    } else {
                        validateOTP.value = NetworkResponse.Failure(response.getString("message"))
                    }
                } catch (e: Exception) {
                    validateOTP.value = NetworkResponse.Failure(e.message)
                }
            }
        } else {
            validateOTP.value = NetworkResponse.Failure("No Internet Connection!")
        }
        return validateOTP
    }

    fun pauseBooking(context: Context, requestBody: ProviderPauseBookingReqModel): MutableLiveData<NetworkResponse<String>> {
        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                try {
                    pauseBooking.value = NetworkResponse.Loading()
                    val result = async { repository.pauseBooking(requestBody) }
                    val response = JSONObject(result.await().string())
                    if (response.getInt("status") == 200) {
                        pauseBooking.value = NetworkResponse.Success(response.getString("message"))
                    } else {
                        pauseBooking.value = NetworkResponse.Failure(response.getString("message"))
                    }
                } catch (e: Exception) {
                    pauseBooking.value = NetworkResponse.Failure(e.message)
                }
            }
        } else {
            pauseBooking.value = NetworkResponse.Failure("No Internet Connection!")
        }
        return pauseBooking
    }

    fun resumeBooking(context: Context, requestBody: ProviderBookingResumeReqModel): MutableLiveData<NetworkResponse<String>> {
        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                try {
                    resumeBooking.value = NetworkResponse.Loading()
                    val result = async { repository.resumeBooking(requestBody) }
                    val response = JSONObject(result.await().string())
                    if (response.getInt("status") == 200) {
                        resumeBooking.value = NetworkResponse.Success(response.getString("message"))
                    } else {
                        resumeBooking.value = NetworkResponse.Failure(response.getString("message"))
                    }
                } catch (e: Exception) {
                    resumeBooking.value = NetworkResponse.Failure(e.message)
                }
            }
        } else {
            resumeBooking.value = NetworkResponse.Failure("No Internet Connection!")
        }
        return resumeBooking
    }


}