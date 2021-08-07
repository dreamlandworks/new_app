package com.satrango.ui.user.bookings.bookaddress

import com.satrango.base.BaseRepository
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.user.bookings.bookaddress.models.BlueCollarBookingReqModel
import com.satrango.ui.user.bookings.bookaddress.models.SingleMoveBookingReqModel
import okhttp3.ResponseBody

class BookingRepository: BaseRepository() {

    suspend fun bookSingleMoveServiceProvider(requestBody: SingleMoveBookingReqModel): ResponseBody {
        return RetrofitBuilder.getUserRetrofitInstance().bookSingleMoveProvider(requestBody)
    }

    suspend fun bookBlueCollarServiceProvider(requestBody: BlueCollarBookingReqModel): ResponseBody {
        return RetrofitBuilder.getUserRetrofitInstance().bookBlueCollarProvider(requestBody)
    }

}