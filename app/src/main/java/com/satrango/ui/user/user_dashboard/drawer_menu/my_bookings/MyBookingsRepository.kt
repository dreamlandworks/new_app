package com.satrango.ui.user.user_dashboard.drawer_menu.my_bookings

import com.satrango.base.BaseRepository
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.user.user_dashboard.drawer_menu.my_bookings.models.MyBookingsReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_bookings.models.MyBookingsResModel

class MyBookingsRepository: BaseRepository() {

    suspend fun getMyBookings(requestBody: MyBookingsReqModel): MyBookingsResModel {
        return RetrofitBuilder.getUserRetrofitInstance().getMyBookingDetails(requestBody)
    }

}