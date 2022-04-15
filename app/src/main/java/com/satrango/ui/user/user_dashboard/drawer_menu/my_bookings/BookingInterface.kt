package com.satrango.ui.user.user_dashboard.drawer_menu.my_bookings

import com.satrango.ui.user.user_dashboard.drawer_menu.my_bookings.models.BookingDetail

interface BookingInterface {

    fun startMessaging(bookingDetails: BookingDetail)

}