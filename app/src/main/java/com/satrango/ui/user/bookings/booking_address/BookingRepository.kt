package com.satrango.ui.user.bookings.booking_address

import com.satrango.base.BaseRepository
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.user.bookings.booking_address.models.BlueCollarBookingReqModel
import com.satrango.ui.user.bookings.booking_address.models.SingleMoveBookingReqModel
import com.satrango.ui.user.bookings.booking_attachments.models.MultiMoveReqModel
import com.satrango.ui.user.bookings.change_address.AddBookingAddressReqModel
import okhttp3.ResponseBody

class BookingRepository: BaseRepository() {

    suspend fun bookSingleMoveServiceProvider(requestBody: SingleMoveBookingReqModel): ResponseBody {
        return RetrofitBuilder.getUserRetrofitInstance().bookSingleMoveProvider(requestBody)
    }

    suspend fun bookBlueCollarServiceProvider(requestBody: BlueCollarBookingReqModel): ResponseBody {
        return RetrofitBuilder.getUserRetrofitInstance().bookBlueCollarProvider(requestBody)
    }

    suspend fun bookMultiMoveServiceProvider(requestBody: MultiMoveReqModel): ResponseBody {
        return RetrofitBuilder.getUserRetrofitInstance().bookMultiMoveProvider(requestBody)
    }

    suspend fun addBookingAddress(requestBody: AddBookingAddressReqModel): ResponseBody {
        return RetrofitBuilder.getUserRetrofitInstance().addBookingAddress(requestBody)
    }

}