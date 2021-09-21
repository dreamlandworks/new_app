package com.satrango.ui.service_provider.provider_dashboard.provider_dashboard.my_bookings.provider_booking_details.models

data class GetBookingStatusListResModel(
    val booking_status_details: List<BookingStatusDetail>,
    val message: String,
    val status: Int
)