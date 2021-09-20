package com.satrango.ui.user.user_dashboard.drawer_menu.my_bookings

import com.satrango.base.BaseRepository
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.user.user_dashboard.drawer_menu.my_bookings.models.MyBookingsReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_bookings.models.MyBookingsResModel
import okhttp3.ResponseBody

class MyBookingsRepository: BaseRepository() {

    suspend fun getMyBookings(requestBody: MyBookingsReqModel): MyBookingsResModel {
        return RetrofitBuilder.getUserRetrofitInstance().getMyBookingDetails(requestBody)
    }

    suspend fun generateOTP(bookingId: Int): ResponseBody {
        return RetrofitBuilder.getUserRetrofitInstance().getBookingStatusOTP(RetrofitBuilder.USER_KEY, bookingId)
    }

    suspend fun validateOTP(bookingId: Int, spId: Int): ResponseBody {
        return RetrofitBuilder.getUserRetrofitInstance().getBookingValidateOTP(RetrofitBuilder.USER_KEY, bookingId, spId)
    }

}