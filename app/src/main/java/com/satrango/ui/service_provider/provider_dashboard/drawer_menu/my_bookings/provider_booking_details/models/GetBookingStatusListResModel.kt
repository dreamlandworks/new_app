package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.models

data class GetBookingStatusListResModel(
    val booking_status_details: List<BookingStatusDetail>,
    val message: String,
    val status: Int
)