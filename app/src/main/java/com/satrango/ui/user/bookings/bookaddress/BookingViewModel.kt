package com.satrango.ui.user.bookings.bookaddress

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.satrango.remote.NetworkResponse
import com.satrango.ui.user.bookings.bookaddress.models.BlueCollarBookingReqModel
import com.satrango.ui.user.bookings.bookaddress.models.SingleMoveBookingReqModel
import com.satrango.utils.hasInternetConnection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class BookingViewModel(val repository: BookingRepository): ViewModel() {

    val singleMoveBooking = MutableLiveData<NetworkResponse<String>>()
    val blueCollarBooking = MutableLiveData<NetworkResponse<String>>()

    fun singleMoveBooking(context: Context, requestBody: SingleMoveBookingReqModel): MutableLiveData<NetworkResponse<String>> {
        if (hasInternetConnection(context)) {
            CoroutineScope(Dispatchers.Main).launch {
                singleMoveBooking.value = NetworkResponse.Loading()
                try {
                    val response = repository.bookSingleMoveServiceProvider(requestBody)
                    singleMoveBooking.value = NetworkResponse.Success(response.string())
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
            CoroutineScope(Dispatchers.Main).launch {
                singleMoveBooking.value = NetworkResponse.Loading()
                try {
                    val response = repository.bookBlueCollarServiceProvider(requestBody)
                    singleMoveBooking.value = NetworkResponse.Success(response.string())
                } catch (e: Exception) {
                    singleMoveBooking.value = NetworkResponse.Failure(e.message)
                }
            }
        } else {
            singleMoveBooking.value = NetworkResponse.Failure("No Internet Connection")
        }

        return singleMoveBooking
    }

}