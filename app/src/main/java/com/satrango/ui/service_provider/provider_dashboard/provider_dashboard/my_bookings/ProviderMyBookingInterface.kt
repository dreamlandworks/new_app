package com.satrango.ui.service_provider.provider_dashboard.provider_dashboard.my_bookings

interface ProviderMyBookingInterface {

    fun requestOTP(bookingId: Int)

    fun markComplete(extraDemand: String, bookingId: Int)

    fun pauseBooking(bookingId: Int)

    fun resumeBooking(bookingId: Int)

}