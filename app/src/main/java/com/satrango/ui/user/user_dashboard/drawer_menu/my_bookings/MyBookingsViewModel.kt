package com.satrango.ui.user.user_dashboard.drawer_menu.my_bookings

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.satrango.remote.NetworkResponse
import com.satrango.ui.user.user_dashboard.drawer_menu.my_bookings.models.BookingDetail
import com.satrango.ui.user.user_dashboard.drawer_menu.my_bookings.models.MyBookingsReqModel
import com.satrango.utils.hasInternetConnection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class MyBookingsViewModel(private val repository: MyBookingsRepository): ViewModel() {

    var myBookings = MutableLiveData<NetworkResponse<List<BookingDetail>>>()

    fun getMyBookingDetails(context: Context, requestBody: MyBookingsReqModel): MutableLiveData<NetworkResponse<List<BookingDetail>>> {

        if (hasInternetConnection(context)) {
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val response = repository.getMyBookings(requestBody)
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

}