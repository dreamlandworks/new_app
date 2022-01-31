package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings

interface ProviderMyBookingInterface {

    fun requestOTP(bookingId: Int, categoryId: String, userId: String, spId: String, userFcmToken: String, spFcmToken: String)

    fun markComplete(extraDemand: String, bookingId: Int, categoryId: String, userId: String)

    fun pauseBooking(bookingId: Int)

    fun resumeBooking(bookingId: Int)

}