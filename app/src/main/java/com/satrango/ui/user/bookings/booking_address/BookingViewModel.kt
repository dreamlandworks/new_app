package com.satrango.ui.user.bookings.booking_address

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.satrango.remote.NetworkResponse
import com.satrango.ui.user.bookings.provider_response.PaymentConfirmReqModel
import com.satrango.ui.user.bookings.booking_address.models.BlueCollarBookingReqModel
import com.satrango.ui.user.bookings.booking_address.models.SingleMoveBookingReqModel
import com.satrango.ui.user.bookings.booking_attachments.models.MultiMoveReqModel
import com.satrango.ui.user.bookings.change_address.AddBookingAddressReqModel
import com.satrango.ui.user.bookings.view_booking_details.models.BookingDetailsReqModel
import com.satrango.ui.user.bookings.view_booking_details.models.BookingDetailsResModel
import com.satrango.ui.user.bookings.view_booking_details.models.ProviderResponseReqModel
import com.satrango.utils.UserUtils
import com.satrango.utils.hasInternetConnection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.lang.Exception

class BookingViewModel(val repository: BookingRepository): ViewModel() {

    val singleMoveBooking = MutableLiveData<NetworkResponse<String>>()
    val blueCollarBooking = MutableLiveData<NetworkResponse<String>>()
    val multiMoveBooking = MutableLiveData<NetworkResponse<String>>()
    val addBookingAddress = MutableLiveData<NetworkResponse<String>>()
    val confirmBooking = MutableLiveData<NetworkResponse<String>>()
    val viewBookingDetails = MutableLiveData<NetworkResponse<BookingDetailsResModel>>()
    val providerResponse = MutableLiveData<NetworkResponse<String>>()

    fun singleMoveBooking(context: Context, requestBody: SingleMoveBookingReqModel): MutableLiveData<NetworkResponse<String>> {
        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                singleMoveBooking.value = NetworkResponse.Loading()
                try {
                    val response = async { repository.bookSingleMoveServiceProvider(requestBody) }
                    val jsonResponse = JSONObject(response.await().string())
                    Log.e("BOOKING RESPONSE:", jsonResponse.toString())
                    if (jsonResponse.getInt("status") == 200) {
                        UserUtils.saveBookingId(context, jsonResponse.getInt("booking_id").toString())
                        UserUtils.saveBookingRefId(context, jsonResponse.getString("booking_ref_id"))
                        singleMoveBooking.value = NetworkResponse.Success(jsonResponse.getString("message"))
                    } else {
                        singleMoveBooking.value = NetworkResponse.Failure(jsonResponse.getString("message"))
                    }
                } catch (e: Exception) {
                    singleMoveBooking.value = NetworkResponse.Failure(e.message)
                }
            }
        } else {
            singleMoveBooking.value = NetworkResponse.Failure("No Internet Connection")
        }

        return singleMoveBooking
    }

    fun blueCollarBooking(context: Context, requestBody: BlueCollarBookingReqModel): MutableLiveData<NetworkResponse<String>> {
        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                blueCollarBooking.value = NetworkResponse.Loading()
                try {
                    val response = async { repository.bookBlueCollarServiceProvider(requestBody) }
                    val jsonResponse = JSONObject(response.await().string())
                    if (jsonResponse.getInt("status") == 200) {
                        UserUtils.saveBookingId(context, jsonResponse.getInt("booking_id").toString())
                        UserUtils.saveBookingRefId(context, jsonResponse.getString("booking_ref_id"))
                        blueCollarBooking.value = NetworkResponse.Success(jsonResponse.getString("message"))
                    } else {
                        blueCollarBooking.value = NetworkResponse.Failure(jsonResponse.getString("message"))
                    }
                } catch (e: Exception) {
                    blueCollarBooking.value = NetworkResponse.Failure(e.message)
                }
            }
        } else {
            blueCollarBooking.value = NetworkResponse.Failure("No Internet Connection")
        }
        return blueCollarBooking
    }

    fun multiMoveBooking(context: Context, requestBody: MultiMoveReqModel): MutableLiveData<NetworkResponse<String>> {
        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                multiMoveBooking.value = NetworkResponse.Loading()
                try {
                    val response = async { repository.bookMultiMoveServiceProvider(requestBody) }
                    val jsonResponse = JSONObject(response.await().string())
                    if (jsonResponse.getInt("status") == 200) {
                        UserUtils.saveBookingId(context, jsonResponse.getInt("booking_id").toString())
                        UserUtils.saveBookingRefId(context, jsonResponse.getString("booking_ref_id"))
                        multiMoveBooking.value = NetworkResponse.Success(jsonResponse.getString("message"))
                    } else {
                        multiMoveBooking.value = NetworkResponse.Failure(jsonResponse.getString("message"))
                    }
                } catch (e: Exception) {
                    multiMoveBooking.value = NetworkResponse.Failure(e.message)
                }
            }
        } else {
            multiMoveBooking.value = NetworkResponse.Failure("No Internet Connection")
        }
        return multiMoveBooking
    }

    fun addBookingAddress(context: Context, requestBody: AddBookingAddressReqModel): MutableLiveData<NetworkResponse<String>> {
        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                addBookingAddress.value = NetworkResponse.Loading()
                try {
                    val response = async { repository.addBookingAddress(requestBody) }
                    addBookingAddress.value = NetworkResponse.Success(response.await().string())
                } catch (e: Exception) {
                    addBookingAddress.value = NetworkResponse.Failure(e.message)
                }
            }
        } else {
            addBookingAddress.value = NetworkResponse.Failure("No Internet Connection")
        }
        return addBookingAddress
    }

    fun confirmPayment(context: Context, requestBody: PaymentConfirmReqModel): MutableLiveData<NetworkResponse<String>> {
        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                confirmBooking.value = NetworkResponse.Loading()
                try {
                    val response = async { repository.confirmBooking(requestBody) }
                    confirmBooking.value = NetworkResponse.Success(response.await().string())
                } catch (e: Exception) {
                    confirmBooking.value = NetworkResponse.Failure(e.message)
                }
            }
        } else {
            confirmBooking.value = NetworkResponse.Failure("No Internet Connection")
        }
        return confirmBooking
    }

    fun viewBookingDetails(context: Context, requestBody: BookingDetailsReqModel): MutableLiveData<NetworkResponse<BookingDetailsResModel>> {
        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                viewBookingDetails.value = NetworkResponse.Loading()
                try {
                    val response = async { repository.viewBookingDetails(requestBody) }
                    viewBookingDetails.value = NetworkResponse.Success(response.await())
                } catch (e: Exception) {
                    viewBookingDetails.value = NetworkResponse.Failure(e.message)
                }
            }
        } else {
            viewBookingDetails.value = NetworkResponse.Failure("No Internet Connection")
        }
        return viewBookingDetails
    }

    fun setProviderResponse(context: Context, requestBody: ProviderResponseReqModel): MutableLiveData<NetworkResponse<String>> {
        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                providerResponse.value = NetworkResponse.Loading()
                try {
                    val response = async { repository.setProviderResponse(requestBody) }
                    providerResponse.value = NetworkResponse.Success(response.await().string())
                } catch (e: Exception) {
                    providerResponse.value = NetworkResponse.Failure(e.message)
                }
            }
        } else {
            providerResponse.value = NetworkResponse.Failure("No Internet Connection")
        }
        return providerResponse
    }

}