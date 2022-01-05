package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings

interface ProviderMyBookingInterface {

    fun requestOTP(bookingId: Int, categoryId: String, userId: String, spId: String)

    fun markComplete(extraDemand: String, bookingId: Int)

    fun pauseBooking(bookingId: Int)

    fun resumeBooking(bookingId: Int)

}